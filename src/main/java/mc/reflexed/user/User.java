package mc.reflexed.user;

import java.util.ArrayList;
import java.util.List;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import mc.reflexed.Reflexed;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.user.data.Savable;
import mc.reflexed.user.data.Type;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

    private boolean pearlCooldown;
    private long pearlCooldownTime;

    @Savable(Type.NUMBER)
    private int playTime;
    private int joinTime;

    public User(Player player, UserRank rank) {
        this.player = player;
        this.rank = rank;
        eventManager.register(this, player);
    }

    public void updateRank(UserRank rank) {
        this.rank = rank;

        ChatUtil.message(String.format("§aYour rank has been updated to %s§a!", rank.getPrefix()), player);

        Reflexed.get().getUserDatabase().saveUser(this);
    }

    public int fetchPlayTime() {
        return (int) (playTime + (System.currentTimeMillis() - joinTime));
    }

    @EventInfo
    public void onQuit(Player player, PlayerQuitEvent e) {
        playTime = fetchPlayTime();
        Reflexed.get().getUserDatabase().saveUser(this);

        eventManager.unregister(this);
        users.remove(this);

        ChatUtil.broadcast(String.valueOf(User.getUsers().size()));
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


    public double getKDR() {
        if(deaths == 0) return kills;
        if(kills == 0) return 0.0;

        return kills / deaths;
    }

    public static User getUser(Player target) {
        return users.stream().filter(user -> user.getPlayer().equals(target)).findFirst().orElse(null);
    }

    public static List<User> getUsers(UserRank rank) {
        return users.stream().filter(user -> user.getRank() == rank).toList();
    }
}
