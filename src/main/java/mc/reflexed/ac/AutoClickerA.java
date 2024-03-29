package mc.reflexed.ac;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mc.reflexed.ac.check.Check;
import mc.reflexed.ac.check.data.CheckInfo;
import mc.reflexed.ac.check.data.CheckType;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.util.ChatUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(
        name = "Auto Clicker",
        description = "Checks if the player is using an auto clicker",
        maxVl = 100
)
public class AutoClickerA extends Check {

    private final List<CPS> cps = new ArrayList<>();

    private CPS current;

    @EventInfo
    public void onSwing(Player player, PlayerArmSwingEvent event) {
        if(cps.size() >= 4) {
            cps.remove(0);
        }

        if(current == null) {
            current = new CPS(1, System.currentTimeMillis());
            return;
        }

        current.setClicks(current.getClicks() + 1);

        if(System.currentTimeMillis() - current.getSince() > 1000) {
            cps.add(current);
            current = new CPS(1, System.currentTimeMillis());
            return;
        }

        int max = -1, min = -1;
        int countSame = 0, lastMax = -1, lastMin = -1;
        for(CPS cps : cps) {
            if(max == -1 || cps.getClicks() > max) {
                max = cps.getClicks();
            }

            if(min == -1 || cps.getClicks() < min) {
                min = cps.getClicks();
            }

            if(lastMax == max || lastMin == min) {
                countSame++;
            }

            lastMax = max;
            lastMin = min;
        }

        if(max == -1 || min == -1) return;

        if((max - min) > 5 && max > 25) {
            flag("max=" + max,
                    "min=" + min,
                    "countSame=" + countSame
            );
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    private static class CPS {
        private int clicks;
        private long since;
    }


}
