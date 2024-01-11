package mc.reflexed.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.CommandManager;
import mc.reflexed.command.FlexedCommand;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "unregister")
public class TestCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        sender.sendMessage("§cUnregistering command...");

        FlexedCommand command = Reflexed.get().getCommandManager().getCommandFromExecutor(this);
        Reflexed.get().getCommandManager().unregister(command);

        sender.sendMessage("§aUnregistered command!");
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        return new String[] {args.length > 0 ? "test" + args.length : "test"};
    }
}
