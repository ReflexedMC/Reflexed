package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "combo", description = "Enables and disables combo mode")
@Permission(UserRank.ADMIN)
public class ComboCommand implements ICommandExecutor {
    @Override
    public boolean execute(CommandSender commandSender, String[] strings, String s) {
        Reflexed.get().setComboMode(!Reflexed.get().isComboMode());
        if (!Reflexed.get().isComboMode()) {
//            loop through all players and reset hurt time
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setNoDamageTicks(10);
                player.setMaximumNoDamageTicks(10);
            });
        }
        ChatUtil.broadcast("§aCombo mode has been " + (Reflexed.get().isComboMode() ? "enabled" : "disabled") + "!");
        return false;
    }
}
