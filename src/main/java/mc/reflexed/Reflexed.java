package mc.reflexed;

import lombok.Getter;
import mc.reflexed.command.CommandManager;
import mc.reflexed.command.ReflexedCommand;
import mc.reflexed.command.commands.GrantCommand;
import mc.reflexed.command.commands.TestCommand;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class Reflexed extends JavaPlugin {

    private final CommandManager commandManager;
    private final EventManager eventManager;

    private UserDatabase userDatabase;

    public Reflexed() {
        this.commandManager = new CommandManager(this);
        this.eventManager = new EventManager(this);
    }

    @Override
    public void onEnable() {
        eventManager.onEnable();

        ReflexedCommand.createCommands(
                new TestCommand(), new GrantCommand()
        );

        eventManager.register(this);

        if(!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Failed to create data folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        userDatabase = new UserDatabase(new File(getDataFolder(), "users.yml"));

        Bukkit.getOnlinePlayers().forEach(player -> {
            User user = userDatabase.getUser(player);
            User.getUsers().add(user);
        });
    }

    @Override
    public void onDisable() {
        commandManager.onDisable();

        List<User> users = new ArrayList<>(User.getUsers());
        users.forEach((user) -> {
            userDatabase.saveUser(user);
            User.getUsers().remove(user);
        });
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent e) {
        User user = userDatabase.getUser(e.getPlayer());
        User.getUsers().add(user);

        ChatUtil.broadcast(String.valueOf(User.getUsers().size()));
    }


    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
