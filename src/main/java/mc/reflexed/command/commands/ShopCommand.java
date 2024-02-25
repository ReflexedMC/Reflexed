package mc.reflexed.command.commands;

import mc.reflexed.command.ICommandExecutor;
import mc.reflexed.command.data.CommandInfo;
import mc.reflexed.event.data.EventInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

@CommandInfo(name = "shop", description = "Opens the shop GUI")
public class ShopCommand implements ICommandExecutor {

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to execute this command.");
            return false;
        }

        Inventory inventory = createShop();
        player.openInventory(inventory);
        return false;
    }

    @EventInfo
    public void onGuiClick(InventoryClickEvent event) {
        if(event.getView().title().equals(Component.text("§d§lShop"))) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            String displayName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.getCurrentItem().getItemMeta().displayName()));

            if(displayName.equalsIgnoreCase("Cosmetic Shop")) {
                event.getWhoClicked().sendMessage("§cThis feature is not yet implemented. (cosmetic shop)");
            } if(displayName.equalsIgnoreCase("Upgrade Shop")) {
                event.getWhoClicked().sendMessage("§cThis feature is not yet implemented. (upgrade shop)");
            }
        }
    }

    private Inventory createShop() {
        Inventory inventory = Bukkit.createInventory(null, 9, Component.text("§d§lShop"));

        ItemStack item = new ItemStack(Material.END_STONE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§d§lCosmetic Shop"));

        List<Component> lore = List.of(
                Component.text(""),
                Component.text("§7Click to open the cosmetic shop")
        );

        meta.lore(lore);
        item.setItemMeta(meta);

        inventory.setItem(0, item);

        ItemStack item2 = new ItemStack(Material.DIAMOND);
        ItemMeta meta2 = item2.getItemMeta();
        meta2.displayName(Component.text("§d§lUpgrade Shop"));

        List<Component> lore2 = List.of(
                Component.text(""),
                Component.text("§7Click to open the upgrade shop")
        );

        meta2.lore(lore2);
        item2.setItemMeta(meta2);

        inventory.setItem(1, item2);

        return inventory;
    }

}
