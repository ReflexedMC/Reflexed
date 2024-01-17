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
//        Bukkit.getScheduler().runTaskLater(Reflexed.get(), () -> this.remove(Reflexed.get().getGameMap()), 20L * 10);
        // create a breaking animation
        for(int i = 0; i < 10; i++) {
            Bukkit.getScheduler().runTaskLater(Reflexed.get(), () -> this.setBlock(Material.COBWEB), i);
            Bukkit.getScheduler().runTaskLater(Reflexed.get(), () -> this.setBlock(Material.AIR), i + 1);
        }
    }

    @Override
    public void remove(GameMap map) {
        map.getBlocks().remove(this);
        this.setBlock(Material.AIR);
    }
}
