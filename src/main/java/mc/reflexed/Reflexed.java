package mc.reflexed;

import lombok.Getter;
import lombok.Setter;
import mc.reflexed.combat.CombatTag;
import mc.reflexed.command.CommandManager;
import mc.reflexed.command.ReflexedCommand;
import mc.reflexed.command.commands.*;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.map.GameMap;
import mc.reflexed.user.User;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class Reflexed extends JavaPlugin {

    private final List<Player> buildMode = new ArrayList<>();
    @Setter
    private boolean comboMode = false;

    private final CommandManager commandManager;
    private final EventManager eventManager;

    private UserDatabase userDatabase;
    private GameMap gameMap;

    public Reflexed() {
        this.commandManager = new CommandManager(this);
        this.eventManager = new EventManager(this);
    }

    @Override
    public void onEnable() {
        eventManager.onEnable();

        ReflexedCommand.createCommands(
                new TestCommand(), new GrantCommand(),
                new SetSpawnCommand(), new SpawnCommand(),
                new StatsCommand(), new GamemodeCommand(),
                new FlyCommand(), new ResetStatsCommand(),
                new MapCommand(), new BuildCommand(),
                new ComboCommand()
        );

        eventManager.register(this);

        if(!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Failed to create data folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        userDatabase = new UserDatabase(new File(getDataFolder(), "users.yml"));
        gameMap = new GameMap(this);

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
            user.setPlayTime(user.playTime());

            userDatabase.saveUser(user);
            User.getUsers().remove(user);
        });
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent e) {
        User user = userDatabase.getUser(e.getPlayer());
        Player player = e.getPlayer();

        gameMap.giveStuff(player, true);
        player.teleport(gameMap.getDatabase().getSpawn());

        User.getUsers().add(user);
    }


    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
