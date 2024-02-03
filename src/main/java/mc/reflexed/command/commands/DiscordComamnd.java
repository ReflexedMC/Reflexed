package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "discord", aliases = { "dc" }, description = "View the discord link")
public class DiscordComamnd implements ICommandExecutor {
    @Override
    public boolean execute(CommandSender commandSender, String[] strings, String s) {
        commandSender.sendMessage("§dJoin our discord at §nhttps://discord.gg/Y2pUBXKV");
        return false;
    }
}
