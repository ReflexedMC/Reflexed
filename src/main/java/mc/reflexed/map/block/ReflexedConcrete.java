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
public class ReflexedConcrete extends ReflexedBlock {

    private int stage = 0;

    public ReflexedConcrete(Player placedBy, Location location) {
        super(placedBy, location);
    }

    @Override
    protected void init() {
        Reflexed plugin = Reflexed.get();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        int stages = 7;
        double speed = 0.5;

        for(int i = 0; i <= stages; i++) {
            double multiplier = i == 0 ? speed : i * speed;

            scheduler.runTaskLater(plugin, this::incrementStage, (int)(20L * multiplier));
        }
    }

    protected void incrementStage() {
        if(this.stage >= 7) {
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

    @Override
    public void remove(GameMap map) {
        super.remove(map);

        placedBy.getInventory().addItem(new ItemStack(Material.WHITE_CONCRETE, 1));
    }
}
