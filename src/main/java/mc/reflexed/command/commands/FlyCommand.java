package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = "fly", description = "Toggle fly mode.")
@Permission(UserRank.MODERATOR)
public class FlyCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender commandSender, String[] args, String label) {
        if (!(commandSender instanceof Player commandPlayer)) {
            commandSender.sendMessage("§cYou must be a player to execute this command!");
            return false;
        }

        Player player = commandPlayer;
        if (args.length >= 1) {
            Player newPlayer = Bukkit.getServer().getPlayer(args[0]);
            if (newPlayer == null) {
                ChatUtil.message("§cThat player is not online or does not exist!", commandPlayer);
                return false;
            }
            player = newPlayer;
        }

        boolean flyState = player.getAllowFlight();
        player.setAllowFlight(!flyState);
        if (commandPlayer != player) {
            ChatUtil.message("§7You have toggled §d" + player.getName() + "§7's fly mode to §d" + !flyState + "§7!", commandPlayer);
        }
        ChatUtil.message("§7You have toggled fly mode to §d" + !flyState + "§7!", player);
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            return names.toArray(new String[0]);
        }
        return new String[0];
    }

}
