package mc.reflexed.map;

import mc.reflexed.Reflexed;
import mc.reflexed.combat.CombatTag;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.map.block.ReflexedBlock;
import mc.reflexed.map.block.ReflexedCobweb;
import mc.reflexed.map.block.ReflexedConcrete;
import mc.reflexed.user.User;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class GameMap {

    private final List<ReflexedBlock> blocks = new ArrayList<>();

    private final Reflexed plugin;
    private final MapDatabase database;

    public GameMap(Reflexed plugin) {
        this.plugin = plugin;
        this.database = new MapDatabase(new File(plugin.getDataFolder(), "map.yml"));

        this.plugin.getEventManager().register(this);
    }

    @EventInfo
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        MapDatabase database = Reflexed.get().getGameMap().getDatabase();

        boolean isWhiteConcrete = event.getBlock().getType() == Material.WHITE_CONCRETE;
        boolean isCobWeb = event.getBlock().getType() == Material.COBWEB;
        boolean isAboveMaxHeight = event.getBlock().getLocation().getY() > database.getMaxBuildHeight();

        if(player.getGameMode() != GameMode.CREATIVE) {

            if (isAboveMaxHeight) {
                event.setCancelled(true);
                ChatUtil.message("§cYou cannot build there", player);
                return;
            }

            if (isWhiteConcrete) this.blocks.add(new ReflexedConcrete(player, event.getBlock().getLocation()));
            if (isCobWeb) this.blocks.add(new ReflexedCobweb(player, event.getBlock().getLocation()));
        }

        boolean allowBuild = Reflexed.get().getBuildMode().contains(player);

        if(!allowBuild && !isWhiteConcrete && !isCobWeb) {
            event.setCancelled(true);

            User user = User.getUser(player);

            if(user.getRank().getLevel() >= UserRank.ADMIN.getLevel()) ChatUtil.message("§cYou must enable build to do this", player);
        }
    }

    @EventInfo
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.getEntityType() != EntityType.PLAYER) return;
        if(e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        e.setCancelled(true);
    }

    @EventInfo
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        if(event.getDamager().getType() != EntityType.PLAYER) return;

        CombatTag tag = CombatTag.getTag((Player) event.getEntity());
        MapDatabase database = Reflexed.get().getGameMap().getDatabase();

        if(event.getDamager().getLocation().getY() > database.getMaxBuildHeight()
                || event.getEntity().getLocation().getY() > database.getMaxBuildHeight()) {
            event.setCancelled(true);
            return;
        }

        if(tag != null) {
            tag.unregister();
        }

        CombatTag.tag((Player) event.getEntity(), (Player) event.getDamager());

        User damager = User.getUser((Player) event.getDamager());
        User user = User.getUser((Player) event.getEntity());

        if(damager != null && user != null) {
            event.setDamage(0);

            if (Reflexed.get().isComboMode()) {
                new BukkitRunnable(){
                    public void run(){
                        ((Player) event.getEntity()).setNoDamageTicks(0);
                    }
                }.runTaskLater(Reflexed.get(), 1);
            }

        }
    }

    @EventInfo
    public void onInventoryDrag(InventoryDragEvent event) {
        for(int i : event.getInventorySlots()) {
            if ((event.getView().getSlotType(i) == InventoryType.SlotType.CRAFTING || event.getView().getSlotType(i) == InventoryType.SlotType.FUEL)){
                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();
                player.updateInventory();
            }
        }
    }

    @EventInfo
    public void onBlockDestroy(BlockBreakEvent event) {
        UserRank rank = User.getUser(event.getPlayer()).getRank();

        boolean allowBuild = Reflexed.get().getBuildMode().contains(event.getPlayer());

        if(!allowBuild) {
            event.setCancelled(true);

            if(rank.getLevel() >= UserRank.ADMIN.getLevel()) ChatUtil.message("§cYou must enable build to do this", event.getPlayer());
            return;
        }

        this.blocks.stream()
                .filter(reflexedBlock -> reflexedBlock.getLocation().equals(event.getBlock().getLocation()))
                .findFirst()
                .ifPresent(block -> block.remove(this));
    }

    @EventInfo
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location spawn = this.database.getSpawn();

        if(spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }

    @EventInfo
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(event.getTo().getY() <= 35 && player.getGameMode() != GameMode.CREATIVE) {
            player.teleport(Reflexed.get().getGameMap().getDatabase().getSpawn());

            CombatTag tag = CombatTag.getTag(player);

            User user = User.getUser(player);
            user.setDeaths(user.getDeaths() + 1);
            user.setKillStreak(0);
            user.getSidebar().update();

            if(tag != null) {
                tag.unregister();

                User killer = User.getUser(tag.getDamager());
                killer.setKills(killer.getKills() + 1);
                killer.setKillStreak(killer.getKillStreak() + 1);

                killer.setXp(killer.getXp() + 100);

                if(killer.getKillStreak() % 5 == 0) {
                    killer.setXp(killer.getXp() + (200 * (int)(killer.getKillStreak() % 5)));
                    ChatUtil.broadcast("§d§l" + killer.getPlayer().getName() + " §a§lis on a §akill-streak §a§lof §d§l" + (int)killer.getKillStreak() + "§a§l!");
                }

                user.setKillStreak(0);
                killer.getSidebar().update();

                ChatUtil.broadcast("§d" + player.getName() + " §7was killed by §d" + tag.getDamager().getName() + "§7!");
                ChatUtil.message("§cYou were killed by §d" + tag.getDamager().getName() + "§c!", player);
                ChatUtil.message("§aYou now have §d" + (int)killer.getKills() + " §akills! with a KDR of §d" + MathUtil.toFixed(killer.getKDR(), 2) + "§a!", tag.getDamager());

                int pearls = getItemCount(tag.getDamager(), Material.ENDER_PEARL), cobwebs = getItemCount(tag.getDamager(), Material.COBWEB);

                if(pearls < 3) tag.getDamager().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                if(cobwebs < 2) tag.getDamager().getInventory().addItem(new ItemStack(Material.COBWEB));

                player.setHealth(20);
                player.setFoodLevel(20);
                giveStuff(player, true);
                return;
            }

            giveStuff(player, true);
            ChatUtil.broadcast("§d" + player.getName() + " §7died!");
            player.setHealth(20);
            player.setFoodLevel(20);
        }
    }

    @EventInfo
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;

        event.setCancelled(true);
    }

    public void giveStuff(Player player, boolean clear) {
        if(clear) {
            player.getInventory().clear();
        }

        ItemStack stick = new ItemStack(Material.STICK);
        stick.getItemMeta().displayName(Component.text("§a§lKnockback Stick"));
        stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        if (User.getUser(player).getHotbarHashedData() == null) {
            player.getInventory().addItem(stick,
                    new ItemStack(Material.WHITE_CONCRETE, 32),
                    new ItemStack(Material.ENDER_PEARL, 1),
                    new ItemStack(Material.COBWEB, 1)
            );
            return;
        }

        for(int i = 0; i < 9; i++) {
            char c = User.getUser(player).getHotbarHashedData().charAt(i);

            switch (c) {
                case '1' -> player.getInventory().setItem(i, stick);
                case '2' -> player.getInventory().setItem(i, new ItemStack(Material.ENDER_PEARL, 1));
                case '3' -> player.getInventory().setItem(i, new ItemStack(Material.COBWEB, 1));
                case '4' -> player.getInventory().setItem(i, new ItemStack(Material.WHITE_CONCRETE, 32));
                default -> player.getInventory().setItem(i, null);
            }
        }
    }

    public int getItemCount(Player player, Material material) {
        int count = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item == null) continue;
            if(item.getType() != material) continue;

            count += item.getAmount();
        }

        return count;
    }

}
