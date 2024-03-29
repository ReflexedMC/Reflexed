package mc.reflexed.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import mc.reflexed.Reflexed;
import mc.reflexed.combat.CombatTag;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.user.data.Savable;
import mc.reflexed.user.data.Type;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class User {

    private static final EventManager eventManager = Reflexed.get().getEventManager();

    @Getter
    private static List<User> users = new ArrayList<>();

    private final Player player;

    private UserSidebar sidebar;

    @Savable(Type.ENUM)
    private UserRank rank;

    @Savable(value = Type.NUMBER, numberType = Double.class)
    private double kills, deaths, level = 1, xp;
    private double killStreak;

    @Savable(value = Type.NUMBER, numberType = Long.class)
    private long playTime;

    private long joinSince;

    private boolean pearlCooldown;
    private long pearlCooldownTime;
    private double enderPearlDeaths;

    @Savable(Type.STRING)
    private String hotbarHashedData = "142300000";

    public User(Player player, UserRank rank) {
        this.player = player;
        this.rank = rank;
        this.sidebar = new UserSidebar(this);
        this.sidebar.update();

        this.joinSince = System.currentTimeMillis();
        eventManager.register(this, player);
        users.add(this);
    }

    public void updateRank(UserRank rank) {
        this.rank = rank;
        this.sidebar.update();

        ChatUtil.message(String.format("§aYour rank has been updated to %s§a!", rank.getPrefix()), player);

        Reflexed.get().getUserDatabase().saveUser(this);
    }

    public void hashHotbar() {
        StringBuilder hashedHotbar = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);

            switch (item != null ? item.getType() : null) {
                case STICK:
                    hashedHotbar.append(hashedHotbar.toString().contains("1") ? "0" : "1");
                    break;
                case ENDER_PEARL:
                    hashedHotbar.append(hashedHotbar.toString().contains("2") ? "0" : "2");
                    break;
                case COBWEB:
                    hashedHotbar.append(hashedHotbar.toString().contains("3") ? "0" : "3");
                    break;
                case WHITE_CONCRETE:
                    hashedHotbar.append(hashedHotbar.toString().contains("4") ? "0" : "4");
                    break;
                default:
                    hashedHotbar.append("0");
            }
        }

        if (!hashedHotbar.toString().contains("1")) set(hashedHotbar, '1');
        if (!hashedHotbar.toString().contains("2")) set(hashedHotbar, '2');
        if (!hashedHotbar.toString().contains("3")) set(hashedHotbar, '3');
        if (!hashedHotbar.toString().contains("4")) set(hashedHotbar, '4');

        hotbarHashedData = hashedHotbar.toString();
    }

    public void set(StringBuilder stringBuilder, char c) {
        for (int j = 0; j < 9; j++) {
            if (stringBuilder.charAt(j) == '0') {
                stringBuilder.setCharAt(j, c);
                break;
            }
        }
    }

    public void resetHotbar() {
        hotbarHashedData = "142300000";
    }

    @EventInfo
    public void onQuit(Player player, PlayerQuitEvent e) {
        this.playTime = playTime();

        CombatTag tag = CombatTag.getTag(player);

        Reflexed.get().getBuildMode().remove(player);

        if(tag != null) {
            User damager = User.getUser(tag.getDamager());
            User user = User.getUser(tag.getPlayer());

            if(damager != null) {
                damager.setKills(damager.getKills() + 1);
                user.setDeaths(user.getDeaths() + 1);

                damager.setKillStreak(damager.getKillStreak() + 1);
                user.setKillStreak(0);

                if(damager.getKillStreak() % 5 == 0) {
                    ChatUtil.broadcast("§d§l" + damager.getPlayer().getName() + " §a§lis on a §akill-streak §a§lof §d§l" + (int)damager.getKillStreak() + "§a§l!");
                }

                user.getSidebar().update();
                damager.getSidebar().update();

                ChatUtil.message(String.format("§aYou killed %s§a!", player.getName()), user.getPlayer());
                ChatUtil.broadcast("§d" + player.getName() + " §7was killed by §d" + tag.getDamager().getName() + "§7!");
            }

            tag.unregister();
        }

        Reflexed.get().getUserDatabase().saveUser(this);
        eventManager.unregister(this);
        users.remove(this);
    }

    @EventInfo
    public void onChat(Player player, AsyncChatEvent e) {
        e.setCancelled(true);

        String rankAndPlayer = String.format("%s %s", rank.getPrefix(), player.getName());
        String message = String.format("§7[§6☆ %s§7] %s§7:§r %s", (int)level, rankAndPlayer, PlainTextComponentSerializer.plainText().serialize(e.message()));

        ChatUtil.broadcast(message);
    }

    @EventInfo
    public void onRespawn(Player player, PlayerRespawnEvent event) {
        Reflexed.get().getGameMap().giveStuff(player, true);
    }

    @EventInfo
    public void onItemUsed(Player player, PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if(item == null) return;
            if(item.getType() != Material.ENDER_PEARL) return;

            enderPearlDeaths = deaths;

            if(pearlCooldown && pearlCooldownTime != -1) {
                long timeLeft = pearlCooldownTime - System.currentTimeMillis();

                if(timeLeft <= 0) {
                    pearlCooldown = false;
                    pearlCooldownTime = -1;
                    return;
                }

                ChatUtil.message(String.format("§cYou cannot use this for another %s seconds!", MathUtil.round(timeLeft / 1000.0, 1)), player);
                event.setCancelled(true);
                return;
            }

            pearlCooldown = true;
            pearlCooldownTime = System.currentTimeMillis() + 5000;
        }
    }

    @EventInfo
    public void onTeleport(Player player, PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (deaths == enderPearlDeaths) return;
        event.setCancelled(true);
    }

    @EventInfo
    public void onDrop(Player player, PlayerDropItemEvent event) {
        if (rank == UserRank.ADMIN) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    public double getKDR() {
        if(deaths == 0) return kills;
        if(kills == 0) return 0.0;

        return kills / deaths;
    }

//
    public void setXp(double xp) {
        this.xp = xp;

        double maxXP = getMaxXP(level);
        if(this.xp >= maxXP) {
            this.xp = this.xp - maxXP;
            this.level += 1;

            ChatUtil.message("§aYou are now level §d" + (int)level + "§a!", player);
            player.sendTitle("§aLevel Up!", String.format("§7You are now level §d%s", (int)level), 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
    }

    public long playTime() {
        return (System.currentTimeMillis() - joinSince) + playTime;
    }

    public static double getMaxXP(double level) {
        return 500 + (level == 1 ? 0 : (level - 1) * 500);
    }

    public static User getUser(Player target) {
        return users.stream().filter(user -> user.getPlayer().equals(target)).findFirst().orElse(null);
    }

    public static List<User> getUsers(UserRank rank) {
        return users.stream().filter(user -> user.getRank() == rank).toList();
    }
}
