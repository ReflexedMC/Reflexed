package mc.reflexed.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = "grant", description = "grants a rank to a player", permission = "reflexed.grant")
public class GrantCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /grant <player> <rank>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage("§cPlayer not found!");
            return false;
        }

        User user = User.getUser(target);

        if(user == null) {
            sender.sendMessage("§cUser not found!");
            return false;
        }

        UserRank rank = getRank(args[1]);

        if(rank == null) {
            sender.sendMessage("§cRank not found!");
            return false;
        }

        user.updateRank(rank);
        
        if(rank == user.getRank()) {
            sender.sendMessage("§aSuccessfully updated rank for " + target.getName() + " to " + rank.name().toLowerCase() + "!");
            return false;
        }
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            return names.toArray(new String[0]);
        }

        if(args.length == 2) {
            return getAllRankNames();
        }

        return new String[0];
    }

    public String[] getAllRankNames() {
        String[] names = new String[UserRank.values().length];
        for (int i = 0; i < UserRank.values().length; i++) {
            names[i] = UserRank.values()[i].name().toLowerCase();
        }
        return names;
    }

    public UserRank getRank(String name) {
        return UserRank.valueOf(name.toUpperCase());
    }

}
