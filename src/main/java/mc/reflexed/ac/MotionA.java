package mc.reflexed.ac;

import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ACUtil;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Motion", description = "Checks for Bhop", type = CheckType.EXPERIMENTAL)
public class MotionA extends Check {

    private double lastDist, lastLastDist;
    private boolean lastOnGround;

    @EventInfo
    public void onMove(Player player, PlayerMoveEvent e) {
        if(player.getAllowFlight() || ACUtil.isColliding(player)) {
            this.lastDist = 0;
            this.lastOnGround = true;
            return;
        }

        double dist = (e.getTo().getX() - e.getFrom().getX()) * (e.getTo().getX() - e.getFrom().getX()) + (e.getTo().getZ() - e.getFrom().getZ()) * (e.getTo().getZ() - e.getFrom().getZ());

        double lastDist = this.lastDist;
        this.lastDist = dist;

        // double packets
        double lastLastDist = this.lastLastDist;
        this.lastLastDist = lastDist;

        boolean lastOnGround = this.lastOnGround;
        this.lastOnGround = ACUtil.isOnGround(player);

        float friction = 0.91F;
        double shiftedLastDist = lastDist * friction;
        double equal = dist - shiftedLastDist;
        double scaledEqual = equal * 138;

        double lastShiftedLastDist = lastLastDist * friction;
        double lastEqual = lastDist - lastShiftedLastDist;
        double lastScaledEqual = lastEqual * 138;

        if(((scaledEqual > 1 && scaledEqual < 27) && (lastScaledEqual > 1 && lastScaledEqual < 27))
                && dist > 0.2 && lastDist > 0.2 && lastLastDist > 0.2
                && !ACUtil.isOnGround(player) && !lastOnGround && !this.lastOnGround) {

            flag("scaledEqual=" + scaledEqual,
                    "lastScaledEqual=" + lastScaledEqual,
                    "dist=" + dist,
                    "lastDist=" + lastDist,
                    "lastLastDist=" + lastLastDist
            );
        }
    }
}
