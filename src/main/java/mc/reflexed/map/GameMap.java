package mc.reflexed.map;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
public class GameMap {

    private final World world;

    public void transfer(Player player) {
        player.teleport(this.world.getSpawnLocation());
    }

    public void transfer(Player player, Location location) {
        player.teleport(location);
    }

    public void transfer(Player... players) {
        Arrays.stream(players).forEach(this::transfer);
    }

}
