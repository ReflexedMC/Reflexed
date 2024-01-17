package mc.reflexed.map;

import mc.reflexed.Reflexed;
import mc.reflexed.combat.CombatTag;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.map.block.ReflexedBlock;
import mc.reflexed.map.block.ReflexedCobweb;
import mc.reflexed.user.User;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

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

        if(player.getGameMode() == GameMode.CREATIVE) return;

        boolean isWhiteConcrete = event.getBlock().getType() == Material.WHITE_CONCRETE;
        boolean isCobWeb = event.getBlock().getType() == Material.COBWEB;

        if(isWhiteConcrete) {
            this.blocks.add(ReflexedBlock.fromLocation(player, event.getBlock().getLocation()));
        }

        if(isCobWeb) {
            this.blocks.add(new ReflexedCobweb(player, event.getBlock().getLocation()));
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

        if(tag != null) {
            tag.unregister();
        }

        CombatTag.tag((Player) event.getEntity(), (Player) event.getDamager());

        User damager = User.getUser((Player) event.getDamager());
        User user = User.getUser((Player) event.getEntity());

        if(damager != null && user != null) {
            event.setDamage(0);
        }
    }

    @EventInfo
    public void onBlockDestroy(BlockBreakEvent event) {
        UserRank rank = User.getUser(event.getPlayer()).getRank();

        if(rank.getLevel() < UserRank.ADMIN.getLevel()) {
            event.setCancelled(true);
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

        if(event.getTo().getY() <= 35) {
            player.teleport(Reflexed.get().getGameMap().getDatabase().getSpawn());

            CombatTag tag = CombatTag.getTag(player);

            User user = User.getUser(player);
            user.setDeaths(user.getDeaths() + 1);

            if(tag != null) {
                tag.unregister();

                User killer = User.getUser(tag.getDamager());
                killer.setKills(killer.getKills() + 1);

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
            user.setDeaths(user.getDeaths() + 1);
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

        player.getInventory().addItem(stick,
                new ItemStack(Material.WHITE_CONCRETE, 32),
                new ItemStack(Material.ENDER_PEARL, 1),
                new ItemStack(Material.COBWEB, 1)
        );
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
