package mc.reflexed.map.block;

import lombok.Getter;
import lombok.Setter;
import mc.reflexed.Reflexed;
import mc.reflexed.map.GameMap;
import mc.reflexed.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

@Getter @Setter
public abstract class ReflexedBlock {

    protected final Player placedBy;
    protected final Location location;

    public ReflexedBlock(Player placedBy, Location location) {
        this.placedBy = placedBy;
        this.location = location;
        this.init();
    }

    protected abstract void init();

    protected void setBlock(Material material) {
        this.location.getBlock().setType(material);
        this.location.getBlock().getState().update();
    }

    public void remove(GameMap map) {
        this.setBlock(Material.AIR);
        map.getBlocks().remove(this);
    }

}
