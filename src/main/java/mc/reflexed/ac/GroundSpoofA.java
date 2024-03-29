package mc.reflexed.ac;

import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ACUtil;
import mc.reflexed.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(
        name = "Ground Spoof",
        description = "Checks if the player is spoofing their onGround status",
        type = CheckType.EXPERIMENTAL
)
public class GroundSpoofA extends Check {

    private int onGroundTick, realOnGroundTick;

    @EventInfo
    @SuppressWarnings("deprecation")
    public void onMove(Player player, PlayerMoveEvent e) {
        if(ACUtil.areBlocksAround(player)) return;

        if(realOnGroundTick == 0 && onGroundTick > 5) {
            flag("onGroundTick=" + onGroundTick, "realOnGroundTick=" + realOnGroundTick);
        }

        this.realOnGroundTick = (ACUtil.isOnGround(player)) ? this.realOnGroundTick + 1 : 0;
        this.onGroundTick = (player.isOnGround()) ? this.onGroundTick + 1 : 0;
    }

}
