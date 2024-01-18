package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.combat.CombatTag;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "spawn", description = "Teleports you to spawn")
public class SpawnCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("§cOnly players can use this command"));
            return false;
        }

        Location spawn = Reflexed.get().getGameMap().getDatabase().getSpawn();
        boolean isCombatTagged = CombatTag.getTag(player) != null;

        if(isCombatTagged) {
            player.sendMessage(Component.text("§cYou cannot teleport to spawn while combat tagged"));
            return false;
        }

        if(spawn == null) {
            player.sendMessage(Component.text("§cSpawn has not been set"));
            return false;
        }

        player.teleport(spawn);
        player.sendMessage(Component.text("§aTeleported to spawn"));
        return false;
    }
}

