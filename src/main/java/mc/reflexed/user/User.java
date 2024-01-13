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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter @Setter
public class User {

    private static final EventManager eventManager = Reflexed.get().getEventManager();

    @Getter
    private static List<User> users = new ArrayList<>();

    private final Player player;

    @Savable(Type.ENUM)
    private UserRank rank;

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

    @EventInfo
    public void onQuit(Player player, PlayerQuitEvent e) {
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

    public static User getUser(Player target) {
        return users.stream().filter(user -> user.getPlayer().equals(target)).findFirst().orElse(null);
    }
}
