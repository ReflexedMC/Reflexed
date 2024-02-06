package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "invsee", description = "View another player's inventory")
@Permission(UserRank.MODERATOR)
public class InvSeeCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("§cYou must be a player to use this command!"));
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage(Component.text("§cUsage: /" + label + " <player>"));
            return false;
        }

        Player target = player.getServer().getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(Component.text("§cPlayer not found!"));
            return false;
        }

        openInventory(player, target);
        return false;
    }

    public void openInventory(Player sender, Player target) {
        if(sender == target) {
            sender.sendMessage(Component.text("§cAre you a dumbass? You can already view your inventory"));
            return;
        }

        sender.openInventory(target.getInventory());
        sender.sendMessage(Component.text("§aYou are now viewing §e" + target.getName() + "§a's inventory"));
    }

}
