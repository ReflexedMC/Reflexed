package mc.reflexed;

import lombok.Getter;
import mc.reflexed.command.CommandManager;
import mc.reflexed.commands.GrantCommand;
import mc.reflexed.commands.TestCommand;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.user.User;
import mc.reflexed.user.UserRank;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static mc.reflexed.user.User.users;

@Getter
public final class Reflexed extends JavaPlugin {

    private final CommandManager commandManager;
    private final EventManager eventManager;

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
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent e) {
        users.add(new User(e.getPlayer(), UserRank.DEFAULT));
    }

    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
