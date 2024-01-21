package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "ping", description = "Get ping of a player")
public class PingCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Component.text("§c/" + label + " <player>"));
            return false;
        }

        Player target = (args.length == 0) ? (Player) sender :  sender.getServer().getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(Component.text("§cPlayer not found"));
            return false;
        }

        if(sender == target) {
            ChatUtil.message(String.format("§7Your ping is §d%sms", target.getPing()), (Player) sender);
            return false;
        }

        ChatUtil.message(String.format("§d%s's §7ping is §d%sms", target.getName(), target.getPing()), (Player) sender);
        return false;
    }
}
