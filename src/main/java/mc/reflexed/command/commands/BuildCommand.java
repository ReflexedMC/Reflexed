package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = "build", description = "Enables and disables build mode")
@Permission(UserRank.ADMIN)
public class BuildCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return false;
        }

        List<Player> buildMode = Reflexed.get().getBuildMode();

        if(buildMode.contains(player)) {
            buildMode.remove(player);
            ChatUtil.message("§cBuild mode disabled!", player);

            return false;
        }

        buildMode.add(player);
        ChatUtil.message("§aBuild mode enabled!", player);
        return false;
    }

}
