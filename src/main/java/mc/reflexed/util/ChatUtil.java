package mc.reflexed.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@UtilityClass
public class ChatUtil extends Util {

    private final String color = "\u00a7";
    private final String PREFIX = String.format("%s[%sReflexed%s] %s", color, color, color, color);

    public static void broadcast(String s) {
        server.broadcast(Component.text(s));
    }

    public static void message(String s, Player p) {
        p.sendMessage(String.format("%s%s", PREFIX, s));
    }

}