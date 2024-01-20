package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.user.data.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Ref;

@CommandInfo(name = "resetstats", description = "Reset a player's stats")
@Permission(UserRank.ADMIN)
public class ResetStatsCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0) {
            sender.sendMessage("§c/" + label + " <player>");
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if(!player.hasPlayedBefore() && !player.isOnline()) {
            sender.sendMessage("§cPlayer not found");
            return false;
        }

        if(player.isOnline()) {
            User user = User.getUser(player.getPlayer());

            user.setKills(0);
            user.setDeaths(0);
            user.setKillStreak(0);
            user.setLevel(0);
            user.setXp(0);

            user.getSidebar().update();

            sender.sendMessage("§aSuccessfully reset stats for " + player.getName());
            user.getPlayer().sendMessage("§aYour stats have been reset");
            return true;
        }

        UserDatabase database = Reflexed.get().getUserDatabase();

        if(database == null) {
            sender.sendMessage("§cUser database is not loaded!");
            return false;
        }

        if(!database.getYamlConfiguration().contains(player.getUniqueId().toString())) {
            sender.sendMessage("§cPlayer not found");
            return false;
        }

        ConfigurationSection section = database
                .getYamlConfiguration()
                .getConfigurationSection(player.getUniqueId().toString());

        if(section == null) {
            sender.sendMessage("§cSomething went wrong while looking for player " + player.getName());
            return false;
        }

        section.set("kills", 0);
        section.set("deaths", 0);
        section.set("killStreak", 0);
        section.set("level", 0);
        section.set("xp", 0);

        database.saveConfig();

        sender.sendMessage("§aSuccessfully reset stats for " + player.getName());
        return false;
    }

}
