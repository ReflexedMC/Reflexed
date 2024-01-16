package mc.reflexed.combat;

import lombok.Getter;
import lombok.Setter;
import mc.reflexed.Reflexed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CombatTag {

    @Getter
    private final static List<CombatTag> combatTags = new ArrayList<>();

    private Player player, damager;

    public CombatTag(Player player, Player damager) {
        this.player = player;
        this.damager = damager;

        Bukkit.getScheduler().runTaskLater(Reflexed.get(), () -> combatTags.remove(this), 20 * 15);
    }

    public static void tag(Player player, Player damager) {
        combatTags.add(new CombatTag(player, damager));
    }

    public static boolean isTagged(Player player) {
        return combatTags.stream().anyMatch(tag -> tag.getDamager().equals(player));
    }

    public static CombatTag getTag(Player player) {
        return combatTags.stream().filter(tag -> tag.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public void unregister() {
        combatTags.remove(this);
    }

}
