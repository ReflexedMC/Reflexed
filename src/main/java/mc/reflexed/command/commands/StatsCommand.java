package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.MathUtil;
import net.kyori.adventure.text.Component;
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

        OfflinePlayer player = (sender instanceof Player) ? (Player) sender : null;

        if(args.length >= 1) {
            player = sender.getServer().getOfflinePlayer(args[0]);
        }

        if(player.getPlayer() == null) {
            ConfigurationSection user = Reflexed.get()
                    .getUserDatabase()
                    .getOfflineUser(player);

            if (user == null) {
                sender.sendMessage(Component.text("§cPlayer not found!"));
                return false;
            }

            if (!user.isSet("kills") || !user.isSet("deaths")) {
                sender.sendMessage(Component.text("§cPlayer has no stats!"));
                return false;
            }

            double kills = user.getDouble("kills");
            double deaths = user.getDouble("deaths");

            sendStats(sender, kills, deaths, kills / deaths,  UserRank.forName(Objects.requireNonNull(user.getString("rank"))).getPrefix());
            return false;
        }

        User user = User.getUser(player.getPlayer());

        sendStats(sender, user.getKills(), user.getDeaths(), user.getKills() / user.getDeaths(), user.getRank().getPrefix());
        return false;
    }

    private void sendStats(CommandSender sender, double kills, double deaths, double kd, String rank) {
        sender.sendMessage(Component.text("§d§l" + sender.getName() + "'s Stats"));
        sender.sendMessage(Component.text("§d• Kills: §f" + (int)kills));
        sender.sendMessage(Component.text("§d• Deaths: §f" + (int)deaths));
        sender.sendMessage(Component.text("§d• KDR: §f" + MathUtil.toFixed(kd, 2)));
        sender.sendMessage(Component.text("§d• Rank: §f" + rank));
    }
}
