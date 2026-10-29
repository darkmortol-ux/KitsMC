package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Pantalla de confirmación antes de borrar un kit (para evitar borrados
 * accidentales con un solo click).
 */
public class ConfirmarBorradoGUI implements InventoryHolder, ClickeableGUI {

    private static final int SLOT_CONFIRMAR = 11;
    private static final int SLOT_CANCELAR = 15;

    private final KitsPersonalizados plugin;
    private final Player admin;
    private final Kit kit;
    private Inventory inventory;

    public ConfirmarBorradoGUI(KitsPersonalizados plugin, Player admin, Kit kit) {
        this.plugin = plugin;
        this.admin = admin;
        this.kit = kit;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.colorear("&8¿Borrar '" + kit.getNombre() + "'?"));
        inventory.setItem(SLOT_CONFIRMAR, new ItemBuilder(Material.RED_WOOL)
                .nombre("&c&l✔ Sí, borrar")
                .lore(List.of("&7Esta acción no se puede deshacer."))
                .build());
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(Material.LIME_WOOL)
                .nombre("&a&l✘ Cancelar")
                .build());
        admin.openInventory(inventory);
    }

    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        if (slot == SLOT_CONFIRMAR) {
            plugin.getKitManager().borrar(kit.getNombre());
            admin.closeInventory();
            plugin.getMensajes().enviar(admin, "kit-borrado", "%kit%", kit.getNombre());
            return;
        }
        if (slot == SLOT_CANCELAR) {
            new KitSelectorGUI(plugin, admin, KitSelectorGUI.Modo.BORRAR, 0).abrir();
        }
    }
}
