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

@CommandInfo(name = "resetStats", description = "Reset a player's stats")
@Permission(UserRank.ADMIN)
public class ResetStatsCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0) {
            sender.sendMessage("§c/" + label + " <player>");
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        UserDatabase userDatabase = Reflexed.get().getUserDatabase();

        if(!player.hasPlayedBefore()) {
            sender.sendMessage("§cThat player has never played before.");
            return false;
        }

        if(player.getPlayer() != null) {
            User user = User.getUser(player.getPlayer());

            if(user == null) {
                sender.sendMessage("§cSomething went wrong.");
                return false;
            }

            user.setKills(0);
            user.setDeaths(0);
            user.setPlayTime(0);

            userDatabase.saveConfig();
            userDatabase.reloadConfig();
            sender.sendMessage("§7You have reset the stats of §d" + player.getName() + "§7.");
            return false;
        }

        // the player is offline
        ConfigurationSection section = Reflexed.get()
                .getUserDatabase()
                .getOfflineUser(player);

        if(section == null) {
            sender.sendMessage("§7Something went wrong.");
            return false;
        }

        section.set("kills", 0);
        section.set("deaths", 0);
        section.set("playTime", 0);

        userDatabase.saveConfig();
        userDatabase.reloadConfig();
        sender.sendMessage("§7You have reset the stats of §d" + player.getName() + "§7.");
        return false;
    }

}
