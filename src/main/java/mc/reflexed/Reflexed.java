package mc.reflexed;

import lombok.Getter;
import mc.reflexed.command.CommandManager;
import mc.reflexed.commands.GrantCommand;
import mc.reflexed.commands.TestCommand;
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
        commandManager.register(
                new GrantCommand(), new TestCommand()
        );
        eventManager.register(this);

        if(!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Failed to create data folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        userDatabase = new UserDatabase(new File(getDataFolder(), "users.yml"));

        Bukkit.getOnlinePlayers().forEach(player -> User.getUsers().add(userDatabase.getUser(player)));
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
        User.getUsers().add(userDatabase.getUser(e.getPlayer()));

        ChatUtil.broadcast(String.valueOf(User.getUsers().size()));
    }

    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
