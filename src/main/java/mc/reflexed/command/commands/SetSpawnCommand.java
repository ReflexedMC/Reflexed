package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.map.MapDatabase;
import mc.reflexed.user.data.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "setSpawn", description = "Sets the spawn point of the world")
@Permission(UserRank.ADMIN)
public class SetSpawnCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("§cOnly players can use this command"));
            return false;
        }

        MapDatabase database = Reflexed.get().getGameMap().getDatabase();

        Location location = player.getLocation();
        location.getWorld().setSpawnLocation(location);

        database.setSpawn(location);
        database.saveConfig();
        database.reloadConfig();

        player.sendMessage(Component.text("§aSpawn set"));
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        return new String[0];
    }
}
