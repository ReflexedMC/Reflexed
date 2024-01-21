package mc.reflexed.ac;

import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ACUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(
        name = "Step",
        description = "Detects if the player is stepping up blocks that are too high",
        type = CheckType.EXPERIMENTAL
)
public class StepA extends Check {

    @EventInfo
    public void onMove(Player player, PlayerMoveEvent e) {
        double motionY = e.getTo().getY() - e.getFrom().getY();

        if(motionY > 0.6 && ACUtil.areBlocksAround(player)) {
            flag("motionY=" + motionY);
        }
    }

}
