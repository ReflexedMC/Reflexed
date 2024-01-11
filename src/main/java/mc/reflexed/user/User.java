package mc.reflexed.user;

import java.util.ArrayList;
import java.util.List;

import io.papermc.paper.event.player.AbstractChatEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import lombok.Getter;
import lombok.Setter;
import mc.reflexed.Reflexed;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ChatUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter @Setter
public class User {

    private static final EventManager eventManager = Reflexed.get().getEventManager();

    public static List<User> users = new ArrayList<>();

    private final Player player;
    private UserRank rank;

    public User(Player player, UserRank rank) {
        this.player = player;
        this.rank = rank;

        users.add(this);
        eventManager.register(this, player);
    }

    public void updateRank(UserRank rank) {
        this.rank = rank;
        ChatUtil.message(String.format("§aYour rank has been updated to %s§a!", rank.getPrefix()), player);
    }

    @EventInfo
    public void onQuit(Player player, PlayerQuitEvent e) {
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

    public static User getUser(Player target) {
        return users.stream().filter(user -> user.getPlayer().equals(target)).findFirst().orElse(null);
    }
}
