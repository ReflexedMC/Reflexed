package mc.reflexed.map.block;

import mc.reflexed.Reflexed;
import mc.reflexed.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReflexedCobweb extends ReflexedBlock {

    public ReflexedCobweb(Player placedBy, Location location) {
        super(placedBy, location);
    }

    @Override
    protected void init() {
        Bukkit.getScheduler().runTaskLater(Reflexed.get(), () -> this.remove(Reflexed.get().getGameMap()), 20L * 10);
    }

    @Override
    public void remove(GameMap map) {
        super.remove(map);
    }
}
