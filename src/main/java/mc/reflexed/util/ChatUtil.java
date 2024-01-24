package mc.reflexed.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@UtilityClass
public class ChatUtil extends Util {

    public static void broadcast(Component component) {
        server.broadcast(component);
    }

    public static void message(Component component, Player player) {
        player.sendMessage(component);
    }

    public static void broadcast(String s) {
        broadcast(Component.text(s));
    }

    public static void message(String s, Player p) {
        message(Component.text(s), p);
    }

}