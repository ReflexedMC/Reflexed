package mc.reflexed;

import lombok.Getter;
import mc.reflexed.command.CommandManager;
import mc.reflexed.commands.TestCommand;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

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

        commandManager.register(new TestCommand());
        eventManager.register(this);
    }

    @EventInfo
    public void onMove(PlayerMoveEvent e) {
        e.getPlayer().sendMessage("Stop moving!");
    }

    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
