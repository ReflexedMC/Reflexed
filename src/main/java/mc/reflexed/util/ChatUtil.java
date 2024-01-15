package mc.reflexed.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@UtilityClass
public class ChatUtil extends Util {

    public static void broadcast(String s) {
        server.broadcast(Component.text(s));
    }

    public static void message(String s, Player p) {
        p.sendMessage(Component.text(s));
    }

}