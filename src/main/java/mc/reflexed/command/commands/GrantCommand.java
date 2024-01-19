package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.user.data.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = "grant", description = "grants a rank to a player")
@Permission(UserRank.ADMIN)
public class GrantCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length < 2) {
            sender.sendMessage(Component.text("§cUsage: /grant <player> <rank>"));
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        UserDatabase database = Reflexed.get().getUserDatabase();

        if(!target.hasPlayedBefore() & target.getPlayer() == null) {
            sender.sendMessage(Component.text("§cThat player has never played before."));
            return false;
        }

        if(target.getPlayer() != null) {
            User user = User.getUsers().stream()
                    .filter(u -> u.getPlayer().getUniqueId().equals(target.getUniqueId()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                sender.sendMessage(Component.text("§cUser not found!"));
                return false;
            }

            UserRank rank = getRank(args[1]);

            if (rank == null) {
                sender.sendMessage(Component.text("§cRank not found!"));
                return false;
            }

            user.updateRank(rank);

            if (rank == user.getRank()) {
                sender.sendMessage(Component.text("§7Successfully updated rank for §d" + target.getName() + "§7 to " + rank.getPrefix() + "§7!"));
                return false;
            }

            return false;
        }

        UserRank rank = getRank(args[1]);

        if(rank == null) {
            sender.sendMessage(Component.text("§cRank not found!"));
            return false;
        }

        ConfigurationSection section = Reflexed.get()
                .getUserDatabase()
                .getOfflineUser(target);

        if(section == null) {
            sender.sendMessage(Component.text("§cSomething went wrong."));
            return false;
        }

        section.set("rank", rank.name());

        database.saveConfig();
        database.reloadConfig();
        sender.sendMessage(Component.text("§7Successfully updated rank for §d" + target.getName() + "§7 to " + rank.getPrefix() + "§7!"));
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            return names.toArray(new String[0]);
        }

        if(args.length == 2) {
            return getAllRankNames();
        }

        return new String[0];
    }

    public String[] getAllRankNames() {
        String[] names = new String[UserRank.values().length];
        for (int i = 0; i < UserRank.values().length; i++) {
            names[i] = UserRank.values()[i].name().toLowerCase();
        }
        return names;
    }

    public UserRank getRank(String name) {
        return UserRank.valueOf(name.toUpperCase());
    }

}