package mc.reflexed.user;

import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public record UserSidebar(User user) {

    public void update() {
        user.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Reflexed", Criteria.create("dummy"), Component.text("§d§lReflexed"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String[] board = {
                "§7---------------",
                "§7Level:§r §6☆ " + (int)user.getLevel(),
                "§7  • XP: " + (int)user.getXp() + "§7/" + (int)User.getMaxXP(user.getLevel()),
                "§r",
                "§7Kills:§r " + (int)user.getKills(),
                "§7Deaths:§r " + (int)user.getDeaths(),
                "§7KDR§7:§r " + MathUtil.toFixed(user.getKDR(), 2),
                "§r",
                "§7Rank:§r " + user.getRank().getPrefix(),
                "§7---------------",
        };

        List<String> boardList = new ArrayList<>(List.of(board));
        for(int i = 0; i < boardList.size(); i++) {
            String line = boardList.get(i);

            while (boardList.indexOf(line) != boardList.lastIndexOf(line)) {
                boardList.set(i, line = line + "§r");
            }
        }

        for(int i = 0; i < boardList.size(); i++) {
            Score score = objective.getScore(boardList.get(i));
            score.setScore(boardList.size() - i);
        }

        user.getPlayer().setScoreboard(scoreboard);
    }
}
