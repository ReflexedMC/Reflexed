package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = "gmc", aliases = { "gmc", "gms", "gmsp", "gma" }, description = "Change your gamemode.")
@Permission(UserRank.ADMIN)
public class GamemodeCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player commandPlayer)) {
            sender.sendMessage("§cYou must be a player to execute this command!");
            return false;
        }

        Player player;

        if (args.length >= 1) {
            Player newPlayer = Bukkit.getServer().getPlayer(args[0]);
            if (newPlayer == null) {
                ChatUtil.message("§cThat player is not online or does not exist!", commandPlayer);
                return false;
            }
            player = newPlayer;
        } else {
            player = commandPlayer;
        }

        switch (label) {
            case "gmc" -> updateGamemode(commandPlayer, player, GameMode.CREATIVE);
            case "gms" -> updateGamemode(commandPlayer, player, GameMode.SURVIVAL);
            case "gmsp" -> updateGamemode(commandPlayer, player, GameMode.SPECTATOR);
            case "gma" -> updateGamemode(commandPlayer, player, GameMode.ADVENTURE);
        }
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            return names.toArray(new String[0]);
        }
        return new String[0];
    }

    private void updateGamemode(Player commandPlayer, Player player, GameMode gameMode) {
        if (commandPlayer != player) {
            ChatUtil.message("§7You have updated §d" + player.getName() + "§7's gamemode to §d" + gameMode.name() + "§7!", commandPlayer);
        }
        ChatUtil.message("§7Your gamemode has been updated to §d" + gameMode.name() + "§7!", player);
        player.setGameMode(gameMode);
    }

}
