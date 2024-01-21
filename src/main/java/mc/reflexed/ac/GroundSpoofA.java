package mc.reflexed.ac;

import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ACUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(
        name = "Ground Spoof",
        description = "Checks if the player is spoofing their onGround status",
        type = CheckType.EXPERIMENTAL
)
public class GroundSpoofA extends Check {

    private boolean onGround, lastOnGround;
    private boolean realOnGround, realLastOnGround;

    @EventInfo
    @SuppressWarnings("deprecation")
    public void onMove(Player player, PlayerMoveEvent e) {
        if((onGround && lastOnGround && !realOnGround && !realLastOnGround)) {
            flag("onGround=true",
                    "lastOnGround=true",
                    "realOnGround=false",
                    "realLastOnGround=false");
        }

        this.lastOnGround = this.onGround;
        this.onGround = player.isOnGround();

        this.realLastOnGround = this.realOnGround;
        this.realOnGround = ACUtil.isOnGround(player);

        this.realLastOnGround = this.realOnGround;
        this.realOnGround = ACUtil.isOnGround(player);
    }
}
