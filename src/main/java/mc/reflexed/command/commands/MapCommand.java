package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.map.MapDatabase;
import mc.reflexed.user.data.UserRank;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "map", description = "Map Configuration Command")
@Permission(UserRank.ADMIN)
public class MapCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        SetSpawnCommand setSpawnCommand = Reflexed.get()
                .getCommandManager()
                .getCommands()
                .stream()
                .filter(command -> command.getExecutor() instanceof SetSpawnCommand)
                .map(command -> (SetSpawnCommand) command.getExecutor())
                .findFirst()
                .orElse(null);

        MapDatabase database = Reflexed.get().getGameMap().getDatabase();

        if(setSpawnCommand == null) {
            sender.sendMessage("§cAn error occurred while executing this command.");
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage("§c/map <setSpawn/maxBuildHeight>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "setspawn" -> setSpawnCommand.execute(sender, args, label);
            case "maxbuildheight" -> {

                if (args.length == 1) {
                    sender.sendMessage("§c/map maxBuildHeight <height>");
                    return false;
                }

                try {
                    int height = Integer.parseInt(args[1]);

                    database.setMaxBuildHeight(height);

                    database.saveConfig();
                    database.reloadConfig();
                    sender.sendMessage("§aSuccessfully set the max build height to §d" + height + "§a!");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c/map maxBuildHeight <height>");
                }
            }
            default -> sender.sendMessage("§c/map <setSpawn/maxBuildHeight>");
        }
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            return new String[] {"setSpawn", "maxBuildHeight"};
        }

        return new String[0];
    }
}
