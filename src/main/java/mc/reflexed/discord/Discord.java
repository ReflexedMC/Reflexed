package mc.reflexed.discord;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import mc.reflexed.Reflexed;
import mc.reflexed.event.data.EventInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.EventListener;
import java.util.Objects;

@Getter
public class Discord {

    private final Reflexed plugin;
    private final YamlConfiguration config;

    private JDA jda;
    private TextChannel channel;
    private boolean enabled = false;

    public Discord(Reflexed plugin) {
        this.plugin = plugin;
        this.config = this.loadConfig();

        if(Objects.requireNonNull(config.getString("token")).equalsIgnoreCase("TOKEN_HERE")) {
            this.plugin.getLogger().warning("You need to set your Discord bot token in discord.yml!");
            return;
        }

        this.enabled = true;
    }

    public void onEnable() {
        if(!enabled) return;

        this.init();

        if(channel != null) {
            this.channel.sendMessage("✔️ Server Started!").queue();
        }
    }

    public void onDisable() {
        if(!enabled) return;

        if(channel != null) {
            this.channel.sendMessage("❌ Server Stopped!").queue();
        }

        this.jda.shutdown();
    }

    protected final void init() {
        if(!enabled) return;

        this.jda = JDABuilder.createDefault(this.config.getString("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("Logged in as " + this.jda.getSelfUser().getName() + "!");

        if(this.config.getLong("channel") == -1) {
            this.plugin.getLogger().warning("You need to set your Discord channel ID in discord.yml!");
            return;
        }

        this.channel = this.jda.getTextChannelById(this.config.getLong("channel"));
        this.jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(@NotNull MessageReceivedEvent event) {
                if(event.getChannel().getIdLong() != channel.getIdLong()) return;
                if(event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong()) return;

                String message = event.getMessage().getContentRaw();
                String username = event.getAuthor().getGlobalName() == null ? event.getAuthor().getName() : event.getAuthor().getGlobalName();

                Bukkit.broadcast(Component.text(String.format("§7[§dDiscord§7] §7%s§7: §f%s", username, message)));
            }
        });
        this.plugin.getEventManager().register(this);
    }

    @EventInfo
    public void onJoin(PlayerJoinEvent event) {
        this.channel.sendMessage(String.format("✔️ %s joined!", event.getPlayer().getName())).queue();
    }

    @EventInfo
    public void onLeave(PlayerQuitEvent event) {
        this.channel.sendMessage(String.format("✖️ %s left!", event.getPlayer().getName())).queue();
    }

    @EventInfo
    public void onChat(AsyncChatEvent event) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(event.message())
                .replace("@", "`@`");

        this.channel.sendMessage(String.format("**%s**: %s", event.getPlayer().getName(), plainText)).queue();
    }

    protected final YamlConfiguration loadConfig() {
        File file = new File(this.plugin.getDataFolder(), "discord.yml");

        if (!file.exists()) this.plugin.saveResource("discord.yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if(!config.contains("token")) config.set("token", "TOKEN_HERE");
        if(!config.contains("channel")) config.set("channel", -1);

        return config;
    }
}
