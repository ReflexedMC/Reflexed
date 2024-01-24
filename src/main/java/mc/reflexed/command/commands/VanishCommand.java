package mc.reflexed.command.commands;

import lombok.Getter;
import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.Permission;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandInfo(name = "vanish", aliases = { "v" }, description = "Vanish from other players")
@Permission(UserRank.MODERATOR)
public class VanishCommand implements ICommandExecutor {

    @Getter
    private final Map<Player, BukkitTask> vanished = new HashMap<>();

    public VanishCommand() {
        super();
        Reflexed.get().getEventManager().register(this);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return false;
        }

        if (vanished.get(player) != null) {
            vanished.get(player).cancel();
            vanished.remove(player);

            player.sendActionBar(Component.text("§aYou are no longer vanished!"));
            player.sendMessage("§aYou are no longer vanished!");
            Reflexed.get().getServer().getOnlinePlayers()
                    .stream()
                    .filter(p -> User.getUser(p).getRank().getLevel() < UserRank.MODERATOR.getLevel())
                    .forEach(p -> p.showPlayer(Reflexed.get(), player));

            ChatUtil.broadcast(Reflexed.getJoinMessage(User.getUser(player)));
            return false;
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Reflexed.get(), () -> {
            player.sendActionBar(Component.text("§aYou are vanished!"));
        }, 0, 20);

        vanished.put(player, task);

        player.sendMessage("§aYou are now vanished!");
        Reflexed.get().getServer().getOnlinePlayers()
                .stream()
                .filter(p -> User.getUser(p).getRank().getLevel() < UserRank.MODERATOR.getLevel())
                .forEach(p -> p.hidePlayer(Reflexed.get(), player));

        ChatUtil.broadcast(Reflexed.getQuitMessage(User.getUser(player)));
        return false;
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent e) {
        vanished.forEach((player, task) -> e.getPlayer().hidePlayer(Reflexed.get(), player));
    }

    @EventInfo
    public void onQuit(PlayerQuitEvent e) {
        vanished.remove(e.getPlayer());
    }



}
