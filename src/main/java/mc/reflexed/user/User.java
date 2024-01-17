package mc.reflexed.user;

import java.sql.Ref;
import java.util.ArrayList;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class User {

    private static final EventManager eventManager = Reflexed.get().getEventManager();

    @Getter
    private static List<User> users = new ArrayList<>();

    private final Player player;

    @Savable(Type.ENUM)
    private UserRank rank;

    @Savable(Type.NUMBER)
    private double kills, deaths;

    @Savable(Type.NUMBER)
    private long playTime;

    private long joinSince;

    private boolean pearlCooldown;
    private long pearlCooldownTime;
    private double enderPearlDeaths;

    public User(Player player, UserRank rank) {
        this.player = player;
        this.rank = rank;
        this.joinSince = System.currentTimeMillis();
        eventManager.register(this, player);
    }

    public void updateRank(UserRank rank) {
        this.rank = rank;

        ChatUtil.message(String.format("§aYour rank has been updated to %s§a!", rank.getPrefix()), player);

        Reflexed.get().getUserDatabase().saveUser(this);
    }

    @EventInfo
    public void onQuit(Player player, PlayerQuitEvent e) {
        this.playTime = playTime();

        CombatTag tag = CombatTag.getTag(player);

        if(tag != null) {
            User damager = User.getUser(tag.getDamager());
            User user = User.getUser(tag.getDamager());

            if(damager != null) {
                damager.setKills(damager.getKills() + 1);
                user.setDeaths(user.getDeaths() + 1);

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
        String message = String.format("%s§7:§r %s", rankAndPlayer, PlainTextComponentSerializer.plainText().serialize(e.message()));

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

    public double getKDR() {
        if(deaths == 0) return kills;
        if(kills == 0) return 0.0;

        return kills / deaths;
    }

    public long playTime() {
        return (System.currentTimeMillis() - joinSince) + playTime;
    }

    public static User getUser(Player target) {
        return users.stream().filter(user -> user.getPlayer().equals(target)).findFirst().orElse(null);
    }

    public static List<User> getUsers(UserRank rank) {
        return users.stream().filter(user -> user.getRank() == rank).toList();
    }
}
