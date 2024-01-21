package mc.reflexed.command.commands;

import mc.reflexed.Reflexed;
import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.user.UserDatabase;
import mc.reflexed.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.*;
import java.util.stream.Collectors;

@CommandInfo(name = "leaderboard", description = "Get the leaderboard")
public class LeaderboardCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(args.length == 0) {
            sender.sendMessage(Component.text("§c/" + label + " <kills/level/kdr> <page>"));
            return false;
        }

        if(!args[0].equalsIgnoreCase("kills") && !args[0].equalsIgnoreCase("level") && !args[0].equalsIgnoreCase("kdr")) {
            sender.sendMessage(Component.text("§c/" + label + " <kills/level/kdr> <page>"));
            return false;
        }

        if(args.length == 1) {
            sendLeaderboard(sender, args, 1);
            return false;
        }

        if(!args[1].matches("[0-9]+")) {
            sender.sendMessage(Component.text("§c/" + label + " <kills/level/kdr> <page>"));
            return false;
        }

        sendLeaderboard(sender, args, Integer.parseInt(args[1]));
        return false;
    }

    @Override
    public String[] tabComplete(CommandSender sender, String[] args, String label) {
        if(args.length == 1) {
            return new String[] { "kills", "level", "kdr" };
        }

        return new String[0];
    }

    public void sendLeaderboard(CommandSender sender, String[] args, int page) {
        UserDatabase userDatabase = Reflexed.get().getUserDatabase();
        YamlConfiguration config = userDatabase.getYamlConfiguration();

        HashMap<String, Double> leaderboard = new HashMap<>();

        String type = args[0];
        switch (type.toLowerCase()) {
            case "kills" -> {
                for (String key : config.getKeys(false)) {
                    if(!config.isConfigurationSection(key)) continue;

                    setLeaderboard(leaderboard, key, "kills");
                }
            }
            case "level" -> {
                for (String key : config.getKeys(false)) {
                    if(!config.isConfigurationSection(key)) continue;

                    setLeaderboard(leaderboard, key, "level");
                }
            }
            case "kdr" -> {
                for(String key : config.getKeys(false)) {
                    if(!config.isConfigurationSection(key)) continue;

                    ConfigurationSection section = userDatabase.getOfflineUser(UUID.fromString(key));
                    if(section == null) continue;

                    double kills = section.contains("kills") ? section.getDouble("kills") : 0;
                    double deaths = section.contains("deaths") ? section.getDouble("deaths") : 0;
                    double kdr = kills == 0 || deaths == 0 ? 0 : (kills / deaths);

                    leaderboard.put(key, kdr);
                }
            }
        }

        if (type.equalsIgnoreCase("kdr")) {
            type = "KDR";
        }

        Map<String, Double> sorted = leaderboard.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        if(type.equalsIgnoreCase("kills")) type = "kill's";

        StringBuilder builder = new StringBuilder();
        builder.append("§d").append(type.substring(0, 1).toUpperCase())
                .append(type.substring(1))
                .append(" ").append("Leaderboard").append(" ")
                .append("§7(Page§d ").append(page).append("§7)")
                .append(":");

        int i = 0;
        boolean found = false;
        for (Map.Entry<String, Double> entry : sorted.entrySet()) {
            if(i < (page - 1) * 10 || i >= page * 10) {
                i++;
                continue;
            }

            if(entry.getValue() == 0) continue; // why would I even add a player with 0 kills or level 0 to the leaderboard??

            if(!found) {
                found = true;
                builder.append("\n");
            }

            OfflinePlayer offlinePlayer = Reflexed.get().getServer().getOfflinePlayer(UUID.fromString(entry.getKey()));
            if(!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) continue;

            if(builder.toString().contains(Objects.requireNonNull(offlinePlayer.getName()))) continue; // this happens when a server is switched to offline mode

            builder.append(String.format(" §7• §d%s§7. %s - §d%s§r", i + 1, sender.getName().equalsIgnoreCase(offlinePlayer.getName()) ? "§6" + offlinePlayer.getName()  : offlinePlayer.getName(), entry.getValue().intValue())).append("\n");
            i++;
        }

        if(!found) {
            sender.sendMessage(Component.text("§cNo players found for that page."));
            return;
        }

        sender.sendMessage(Component.text(builder.toString()));

        TextComponent.Builder next = Component.text()
                .append(Component.text("§7[§dNext§7]"))
                .hoverEvent(Component.text("§7Click to go to the next page"))
                .clickEvent(ClickEvent.runCommand("/leaderboard " + args[0] + " " + (page + 1)));

        TextComponent.Builder previous = Component.text()
                .append(Component.text("§7[§dPrevious§7]"))
                .hoverEvent(Component.text("§7Click to go to the previous page"))
                .clickEvent(ClickEvent.runCommand("/leaderboard " + args[0] + " " + (page - 1)));

        TextComponent.Builder combined = Component.text()
                .append(previous.build())
                .append(Component.text(" "))
                .append(next.build());

        sender.sendMessage(combined.build());
    }

    private void setLeaderboard(HashMap<String, Double> leaderboard, String key, String type) {
        UserDatabase userDatabase = Reflexed.get().getUserDatabase();

        ConfigurationSection section = userDatabase.getOfflineUser(UUID.fromString(key));
        if (section == null) return;

        OfflinePlayer offlinePlayer = Reflexed.get().getServer().getOfflinePlayer(UUID.fromString(key));

        if(!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) return;

        leaderboard.put(key, section.getDouble(type));
    }
}
