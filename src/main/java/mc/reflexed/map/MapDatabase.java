package mc.reflexed.map;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class MapDatabase {

    private final YamlConfiguration yamlConfiguration;

    private final File file;

    public MapDatabase(File file) {
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create map.yml!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file = file);
    }

    public void setSpawn(Location location) {
        yamlConfiguration.set("spawn", location);

        saveConfig();
        reloadConfig();
    }

    public Location getSpawn() {
        if(!yamlConfiguration.contains("spawn")) return null;

        return yamlConfiguration.getLocation("spawn");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
