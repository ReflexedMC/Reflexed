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

    public boolean areBlocksAround(Player player) {
        for(int x = -1; x < 2; x++) {
            for(int z = -1; z < 2; z++) {
                if(player.getWorld().getBlockAt(player.getLocation().add(x, -1, z)).getType().isSolid()) {
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
