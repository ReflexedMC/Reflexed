package mc.reflexed.user;

import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.*;

public record UserSidebar(User user) {

    public void update() {
        Scoreboard scoreboard = user.getPlayer().getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", Criteria.create("dummy"), Component.text("§d§lReflexed"));

        String[] sidebar = {
                "                  ",
                "§7Rank: §f" + user.getRank().getPrefix(),
                "§7Kills: §f" + (int)(user.getKills()),
                "§7Deaths: §f" + (int)(user.getDeaths()),
                "§7K/D: §f" + MathUtil.toFixed(user.getKDR(), 2),
                "                  "
        };

        for(int i = 0; i < sidebar.length; i++) {
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            Score score = objective.getScore(sidebar[i]);
            score.setScore(i + 1);
        }


        user.getPlayer().setScoreboard(scoreboard);
    }
}
