package mc.reflexed;

import lombok.Getter;
import lombok.Setter;
import mc.reflexed.ac.*;
import mc.reflexed.ac.check.CheckManager;
import mc.reflexed.command.CommandManager;
import mc.reflexed.command.ReflexedCommand;
import mc.reflexed.command.commands.*;
import mc.reflexed.discord.Discord;
import mc.reflexed.event.EventManager;
import mc.reflexed.event.data.EventInfo;
import mc.reflexed.map.GameMap;
import mc.reflexed.map.block.ReflexedBlock;
import mc.reflexed.user.User;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.user.data.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    private final Discord discord;

    private UserDatabase userDatabase;
    private GameMap gameMap;
    private ReflexedAC ac;

    public Reflexed() {
        this.commandManager = new CommandManager(this);
        this.eventManager = new EventManager(this);
        this.discord = new Discord(this);
    }

    @Override
    public void onEnable() {
        eventManager.onEnable();
        discord.onEnable();

        ReflexedCommand.createCommands(
                new TestCommand(), new GrantCommand(),
                new SetSpawnCommand(), new SpawnCommand(),
                new StatsCommand(), new GamemodeCommand(),
                new FlyCommand(), new ResetStatsCommand(),
                new MapCommand(), new BuildCommand(),
                new ComboCommand(), new PingCommand(),
                new LeaderboardCommand(), new HotbarCommand(),
                new VanishCommand(), new DiscordComamnd(),
                new InvSeeCommand()
        );

        CheckManager.addChecks(
                MotionA.class, StepA.class,
                GroundSpoofA.class, AutoClickerA.class
        );

        if(!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Failed to create data folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        userDatabase = new UserDatabase(new File(getDataFolder(), "users.yml"));
        gameMap = new GameMap(this);

        eventManager.register(this);
        ac = new ReflexedAC(eventManager, new AntiCheatConsumer() {
            @Override
            public void punish(Player player) {
                player.kick(Component.text("§7You have been kicked for §dcheating!"));
            }

            @Override
            public boolean accept(Player player) {
                return User.getUser(player).getRank().getLevel() >= UserRank.MODERATOR.getLevel();
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> userDatabase.getUser(player));
    }

    @Override
    public void onDisable() {
        commandManager.onDisable();
        discord.onDisable();

        List<User> users = new ArrayList<>(User.getUsers());
        users.forEach((user) -> {
            user.setPlayTime(user.playTime());

            userDatabase.saveUser(user);
            User.getUsers().remove(user);
        });

        List<ReflexedBlock> blocks = new ArrayList<>(gameMap.getBlocks());
        blocks.forEach((block) -> block.remove(gameMap));
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent e) {
        userDatabase.getUser(e.getPlayer());

        Player player = e.getPlayer();
        gameMap.giveStuff(player, true);
        player.teleport(gameMap.getDatabase().getSpawn());

        e.joinMessage(Component.text(getJoinMessage(User.getUser(player))));
    }

    @EventInfo
    public void onQuit(PlayerQuitEvent e) {
        User user = User.getUser(e.getPlayer());

        gameMap.getBlocks(user).forEach((block) -> block.remove(gameMap));

        e.quitMessage(Component.text(getQuitMessage(user)));
    }

    public static String getJoinMessage(User user) {
        return String.format("§7[§d+§7] %s %s", user.getRank().getPrefix(), user.getPlayer().getName());
    }

    public static String getQuitMessage(User user) {
        return String.format("§7[§c-§7] %s %s", user.getRank().getPrefix(), user.getPlayer().getName());
    }

    public static Reflexed get() {
        return getPlugin(Reflexed.class);
    }

}
