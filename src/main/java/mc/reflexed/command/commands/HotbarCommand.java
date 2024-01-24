package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HotbarCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to execute this command!");
            return false;
        }

        User user = User.getUser(player);
        user.hashHotbar();
        player.sendMessage("§7Your hotbar has been saved.");

        return false;

    }
}
