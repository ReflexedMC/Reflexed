package mc.reflexed.user;

import lombok.Getter;
import mc.reflexed.Reflexed;
import mc.reflexed.user.data.Savable;
import mc.reflexed.user.data.Type;
import mc.reflexed.user.data.UserRank;
import mc.reflexed.util.ChatUtil;
import mc.reflexed.util.MathUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class UserDatabase {

    @Getter
    private final YamlConfiguration yamlConfiguration;

    private final File file;

    public UserDatabase(File file) {
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create users.yml!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file = file);
    }

    public void saveUser(User user) {
        reloadConfig();

        UUID uuid = user.getPlayer().getUniqueId();

        try {
            Field[] fields = user.getClass().getDeclaredFields();

            for(Field field : fields) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Savable.class)) {
                    Type type = field.getAnnotation(Savable.class).value();

                    switch (type) {
                        case ENUM -> yamlConfiguration.set(uuid + "." + field.getName(), ((Enum<?>) field.get(user)).name());
                        case BOOLEAN -> yamlConfiguration.set(uuid + "." + field.getName(), field.getBoolean(user));
                        case STRING, NUMBER -> yamlConfiguration.set(uuid + "." + field.getName(), field.get(user));
                    }
                }

                yamlConfiguration.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public User getUser(Player player) {
        reloadConfig();

        try {
            User user = new User(player, UserRank.DEFAULT);

            Field[] fields = user.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);

                if(field.isAnnotationPresent(Savable.class) && yamlConfiguration.contains(player.getUniqueId() + "." + field.getName())) {
                    Type type = field.getAnnotation(Savable.class).value();

                    switch (type) {
                        case ENUM -> field.set(user, Enum.valueOf((Class<Enum>) field.getType(), Objects.requireNonNull(yamlConfiguration.getString(player.getUniqueId() + "." + field.getName()))));
                        case BOOLEAN -> field.setBoolean(user, yamlConfiguration.getBoolean(player.getUniqueId() + "." + field.getName()));
                        case STRING -> field.set(user, yamlConfiguration.getString(player.getUniqueId() + "." + field.getName()));
                        case NUMBER -> {
                            switch (field.getAnnotation(Savable.class).numberType().getSimpleName()) {
                                case "Integer" -> field.set(user, yamlConfiguration.getInt(player.getUniqueId() + "." + field.getName()));
                                case "Double" -> field.set(user, yamlConfiguration.getDouble(player.getUniqueId() + "." + field.getName()));
                                case "Long" -> field.set(user, yamlConfiguration.getLong(player.getUniqueId() + "." + field.getName()));
                                default -> {
                                    ChatUtil.message("§cSomething went wrong while loading your stats. Please contact an administrator.", player);
                                    return null;
                                }
                            }
                        }
                    }
                }
            }

            for(String key : yamlConfiguration.getKeys(false)) {
                if(!yamlConfiguration.isConfigurationSection(key)) continue;

                ConfigurationSection section = Reflexed.get()
                        .getUserDatabase()
                        .getOfflineUser(player);

                UUID uuid = UUID.fromString(key);
                if(uuid.equals(player.getUniqueId())) continue;

                if(section == null) continue;

                double kills = section.contains("kills") ? section.getDouble("kills") : 0;
                double deaths = section.contains("deaths") ? section.getDouble("deaths") : 0;
                double level = section.contains("level") ? section.getDouble("level") : 1;
                long playTime = section.contains("playTime") ? section.getLong("playTime") : 0;

                String hotbarHashedData = section.contains("hotbarHashedData") ? section.getString("hotbarHashedData") : "142300000";
                UserRank rank = UserRank.forName(section.contains("rank") ? section.getString("rank") : "DEFAULT");

                if(user.getKills() + user.getDeaths() == 0) continue;

                user.setKills(kills);
                user.setDeaths(deaths);
                user.setPlayTime(playTime);

                if(user.getLevel() < level) user.setLevel(level);
                if(user.getRank().getLevel() < rank.getLevel()) user.setRank(rank);

                user.setHotbarHashedData(hotbarHashedData);

                for(String value : new String[] { "kills", "deaths", "level", "playTime", "hotbarHashedData", "rank", "xp" }) section.set(value, null);
                yamlConfiguration.set(uuid.toString(), null);

                saveConfig();
                reloadConfig();
            }

            user.getSidebar().update();
            return user;
        } catch (YAMLException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ConfigurationSection getOfflineUser(OfflinePlayer player) {
        reloadConfig();

        if(!yamlConfiguration.contains(player.getUniqueId().toString())) return null;

        return yamlConfiguration.getConfigurationSection(player.getUniqueId().toString());
    }

    public ConfigurationSection getOfflineUser(UUID uuid) {
        reloadConfig();

        if(!yamlConfiguration.contains(uuid.toString())) return null;

        return yamlConfiguration.getConfigurationSection(uuid.toString());
    }

    public void reloadConfig() {
        try {
            yamlConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            yamlConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}