package mc.reflexed.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class ACUtil extends Util {

    public boolean isOnGround(Player player) {
        double expand = 0.3;
        for(double x = -expand; x <= expand; x += expand) {
            for(double z = -expand; z <= expand; z += expand) {
                if(player.getLocation().add(x, -0.5001, z).getBlock().getType().isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isColliding(Player player) {
        for(double x = -0.3; x <= 0.3; x += 0.3) {
            for(double y = -0.3; y <= 1.8; y += 0.3) {
                for(double z = -0.3; z <= 0.3; z += 0.3) {
                    if(player.getLocation().add(x, y, z).getBlock().getType().isSolid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
