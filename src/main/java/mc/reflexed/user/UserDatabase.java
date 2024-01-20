package mc.reflexed.user;

import lombok.Getter;
import mc.reflexed.user.data.Savable;
import mc.reflexed.user.data.Type;
import mc.reflexed.user.data.UserRank;
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

            if(!yamlConfiguration.contains(player.getUniqueId().toString())) return user;

            Field[] fields = user.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);

                if(field.isAnnotationPresent(Savable.class) && yamlConfiguration.contains(player.getUniqueId() + "." + field.getName())) {
                    Type type = field.getAnnotation(Savable.class).value();

                    switch (type) {
                        case ENUM -> field.set(user, Enum.valueOf((Class<Enum>) field.getType(), Objects.requireNonNull(yamlConfiguration.getString(player.getUniqueId() + "." + field.getName()))));
                        case BOOLEAN -> field.setBoolean(user, yamlConfiguration.getBoolean(player.getUniqueId() + "." + field.getName()));
                        case STRING -> field.set(user, yamlConfiguration.getString(player.getUniqueId() + "." + field.getName()));
                        case NUMBER -> field.set(user, yamlConfiguration.getInt(player.getUniqueId() + "." + field.getName()));
                    }
                }
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