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
public class ReflexedBlock {

    private final Player placedBy;
    private final Location location;
    private int stage, maxStage;

    public ReflexedBlock(Player placedBy, Location location) {
        this.placedBy = placedBy;
        this.location = location;
        this.stage = 0;
        this.maxStage = 7;
        this.init();
    }

    protected void init() {
        Reflexed plugin = Reflexed.get();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        int stages = maxStage;
        double speed = 0.5;
        for(int i = 0; i <= stages; i++) {
            double multiplier = i == 0 ? speed : i * speed;

            scheduler.runTaskLater(plugin, this::incrementStage, (int)(20L * multiplier));
        }
    }

    protected void incrementStage() {
        if(this.stage >= maxStage) {
            remove(Reflexed.get().getGameMap());
            return;
        }

        Material[] materials = {
                Material.WHITE_CONCRETE,
                Material.PINK_CONCRETE, Material.ORANGE_CONCRETE,
                Material.YELLOW_CONCRETE, Material.LIME_CONCRETE,
                Material.LIGHT_BLUE_CONCRETE, Material.PURPLE_CONCRETE
        };

        this.setBlock(materials[this.stage]);
        this.stage++;
    }

    protected void setBlock(Material material) {
        this.location.getBlock().setType(material);
        this.location.getBlock().getState().update();
    }

    public void remove(GameMap map) {
        this.setBlock(Material.AIR);
        map.getBlocks().remove(this);

        placedBy.getInventory().addItem(new ItemStack(Material.WHITE_CONCRETE, 1));
    }

    public static ReflexedBlock fromLocation(Player placedBy, Location location) {
        return new ReflexedBlock(placedBy, location);
    }

}
