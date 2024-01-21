package mc.reflexed.ac;

import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ACUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(
        name = "Step",
        description = "Detects if the player is stepping up blocks that are too high",
        type = CheckType.EXPERIMENTAL
)
public class StepA extends Check {

    private double lastMotionY;

    @EventInfo
    public void onMove(Player player, PlayerMoveEvent e) {
        double motionY = e.getTo().getY() - e.getFrom().getY();

        double lastMotionY = this.lastMotionY;
        this.lastMotionY = motionY;

        if(lastMotionY > 0.6 && motionY > 0.6 && ACUtil.areBlocksAround(player)) {
            flag("motionY=" + motionY);
        }
    }

    @EventInfo
    public void onTeleport(Player player, PlayerTeleportEvent e) {
        this.lastMotionY = 0;
    }

}
