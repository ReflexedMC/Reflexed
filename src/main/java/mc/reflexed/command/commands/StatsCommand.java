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

@CommandInfo(name = "stats", aliases = { "kills", "deaths", "playtime" }, description = "View stats of a player or yourself")
public class StatsCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0 && (!(sender instanceof Player))) {
            sender.sendMessage(Component.text("§c/" + label + " <player>"));
            return false;
        }

        OfflinePlayer player = sender instanceof Player ? (OfflinePlayer) sender : null;

        if(args.length >= 1) {
            player = Bukkit.getOfflinePlayer(args[0]);

            if(!player.hasPlayedBefore() && player.getPlayer() == null) {
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

                sendStats(sender, player.getName(), user.getKills(), user.getDeaths(), user.getKDR(), user.getLevel(), user.getRank().getPrefix(), user.playTime());
                return false;
            }

            ConfigurationSection section = Reflexed.get()
                    .getUserDatabase()
                    .getOfflineUser(player);

            if(section == null) {
                sender.sendMessage(Component.text("§cSomething went wrong."));
                return false;
            }

            double kills = section.contains("kills") ? section.getDouble("kills") : 0;
            double deaths = section.contains("deaths") ? section.getDouble("deaths") : 0;
            double kdr = kills == 0 || deaths == 0 ? 0 : (kills / deaths);
            double playTime = section.contains("playTime") ? section.getDouble("playTime") : 0;
            double level = section.contains("level") ? section.getDouble("level") : 1;

            String rank = Objects.requireNonNull(UserRank.forName(Objects.requireNonNull(section.getString("rank")))).getPrefix();

            sendStats(sender, player.getName(), kills, deaths, kdr, level, rank, (int) playTime);
            return false;
        }

        if(player.getPlayer() == null) {
            sender.sendMessage(Component.text("§cSomething went wrong."));
            return false;
        }

        User user = User.getUser(player.getPlayer());

        sendStats(sender, sender.getName(), user.getKills(), user.getDeaths(), user.getKDR(), user.getLevel(), user.getRank().getPrefix(), user.playTime());
        return false;
    }

    private void sendStats(CommandSender sender, String name, double kills, double deaths, double kd, double level, String rank, long playTime) {
        double days = (double) playTime / 86400000;
        double hours = ((double) playTime % 86400000) / 3600000;
        double minutes = (((double) playTime % 86400000) % 3600000) / 60000;
        double seconds = ((((double) playTime % 86400000) % 3600000) % 60000) / 1000;

        String playtimeString = "";

        if (days >= 1) playtimeString = MathUtil.toFixed(days, 2) + " days";
        else if (hours >= 1) playtimeString = MathUtil.toFixed(hours, 2) + " hours";
        else if (minutes >= 1) playtimeString = MathUtil.toFixed(minutes, 2) + " minutes";
        else if (seconds >= 1) playtimeString = MathUtil.toFixed(seconds, 2) + " seconds";

        if(playtimeString.isEmpty()) {
            playtimeString = "Has not connected before.";
        }

        sender.sendMessage(Component.text("§d§l" + name + "'s Stats"));
        sender.sendMessage(Component.text("§d• Kills: §f" + (int)kills));
        sender.sendMessage(Component.text("§d• Deaths: §f" + (int)deaths));
        sender.sendMessage(Component.text("§d• KDR: §f" + MathUtil.toFixed(kd, 2)));
        sender.sendMessage(Component.text("§d• Level: §f" + level));
        sender.sendMessage(Component.text("§d• Playtime: §f" + playtimeString));
        sender.sendMessage(Component.text("§d• Rank: §f" + rank));
    }
}
