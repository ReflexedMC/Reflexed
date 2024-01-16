package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "onlyAdmin")
@Permission(UserRank.ADMIN)
public class TestCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        sender.sendMessage(Component.text("Test command executed!"));
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        return new String[] {args.length > 0 ? "test" + args.length : "test"};
    }
}