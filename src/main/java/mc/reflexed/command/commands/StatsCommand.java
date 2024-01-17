package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandInfo(name = "stats", description = "View stats of a player or yourself")
public class StatsCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0 && (!(sender instanceof Player))) {
            sender.sendMessage(Component.text("§c/stats <player>"));
            return false;
        }

        OfflinePlayer player = (OfflinePlayer) sender;

        if(args.length >= 1) {
            player = Bukkit.getOfflinePlayer(args[0]);

            if(!player.hasPlayedBefore()) {
                sender.sendMessage(Component.text("§cThat player has never played before."));
                return false;
            }

            if(player.isOnline()) {
                Player onlinePlayer = player.getPlayer();

                User user = User.getUser(onlinePlayer);

                if(user == null) {
                    sender.sendMessage(Component.text("§cSomething went wrong."));
                    return false;
                }

                sendStats(sender, player.getName(), user.getKills(), user.getDeaths(), user.getKDR(), user.getRank().getPrefix(), user.fetchPlayTime());
                return false;
            }

            ConfigurationSection section = Reflexed.get()
                    .getUserDatabase()
                    .getOfflineUser(player);

            if(section == null) {
                sender.sendMessage(Component.text("§cSomething went wrong."));
                return false;
            }

            double kills = section.getDouble("kills");
            double deaths = section.getDouble("deaths");
            double kdr = kills / deaths;
            double playTime = section.getDouble("playTime");

            String rank = Objects.requireNonNull(UserRank.forName(Objects.requireNonNull(section.getString("rank")))).getPrefix();

            sendStats(sender, player.getName(), kills, deaths, kdr, rank, (int) playTime);
            return false;
        }

        if(player.getPlayer() == null) {
            sender.sendMessage(Component.text("§cSomething went wrong."));
            return false;
        }

        User user = User.getUser(player.getPlayer());

        sendStats(sender, sender.getName(), user.getKills(), user.getDeaths(), user.getKDR(), user.getRank().getPrefix(), user.fetchPlayTime());
        return false;
    }

    private void sendStats(CommandSender sender, String name, double kills, double deaths, double kd, String rank, int playTime) {
//        format int in milliseconds to days, hours, minutes, seconds
        double days = (double) playTime / 86400000;
        double hours = ((double) playTime % 86400000) / 3600000;
        double minutes = (((double) playTime % 86400000) % 3600000) / 60000;
        double seconds = ((((double) playTime % 86400000) % 3600000) % 60000) / 1000;

        String playtimeString = "";
        if (days >= 1) {
            playtimeString = MathUtil.toFixed(days, 2) + "days";
        } else if (hours >= 1) {
            playtimeString = MathUtil.toFixed(hours, 2) + "hours";
        } else if (minutes >= 1) {
            playtimeString = MathUtil.toFixed(minutes, 2) + "minutes";
        } else if (seconds >= 1) {
            playtimeString = MathUtil.toFixed(seconds, 2) + "seconds";
        }

        sender.sendMessage(Component.text("§d§l" + name + "'s Stats"));
        sender.sendMessage(Component.text("§d• Kills: §f" + (int)kills));
        sender.sendMessage(Component.text("§d• Deaths: §f" + (int)deaths));
        sender.sendMessage(Component.text("§d• KDR: §f" + MathUtil.toFixed(kd, 2)));
        sender.sendMessage(Component.text("§d• Playtime: §f" + playtimeString));
        sender.sendMessage(Component.text("§d• Rank: §f" + rank));
    }
}
