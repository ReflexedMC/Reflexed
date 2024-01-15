package mc.reflexed.command;

import mc.reflexed.Reflexed;
import mc.reflexed.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReflexedCommand extends FlexedCommand {

    private int level;

    protected ReflexedCommand(ICommandExecutor executor) {
        super(executor);

        Permission level = executor.getClass().getAnnotation(Permission.class);

        if(level != null) {
            this.level = level.value().getLevel();
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return this.executor.execute(sender, args, label);
        }

        User user = User.getUser(player);

        if(player.isOp()) return this.executor.execute(sender, args, label);

        if(user == null) {
            player.sendMessage("§cAn error occurred while executing this command. a re-log may fix this issue.");
            return true;
        }

        if(user.getRank().getLevel() < this.level) {
            player.sendMessage("§cYou do not have permission to execute this command.");
            return true;
        }

        return this.executor.execute(sender, args, label);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(!(sender instanceof Player player)) {
            return super.tabComplete(sender, alias, args);
        }

        User user = User.getUser(player);

        if(player.isOp()) return super.tabComplete(sender, alias, args);

        if(user == null || user.getRank().getLevel() < this.level) {
            return List.of();
        }

        return super.tabComplete(sender, alias, args);
    }

    public static void createCommands(ICommandExecutor... executor) {
        CommandManager manager = Reflexed.get().getCommandManager();

        for(ICommandExecutor exec : executor) {
            SimpleCommandMap commandMap = manager.getCommandMap();

            ReflexedCommand cmd = new ReflexedCommand(exec);

            commandMap.register(cmd.getInfo().fallback(), cmd);
            manager.getCommands().add(cmd);
            manager.reloadAllCommands();
        }
    }
}
