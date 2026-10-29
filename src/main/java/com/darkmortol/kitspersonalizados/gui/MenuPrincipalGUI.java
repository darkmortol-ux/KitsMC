package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
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
 * Pantalla principal de /kitsp: punto de entrada visual a todas las
 * funciones del plugin. Los botones de administración solo aparecen si el
 * jugador tiene el permiso correspondiente.
 */
public class MenuPrincipalGUI implements InventoryHolder, ClickeableGUI {

    private static final int SLOT_CREAR = 10;
    private static final int SLOT_EDITAR = 11;
    private static final int SLOT_BORRAR = 12;
    private static final int SLOT_LISTAR = 13;
    private static final int SLOT_DAR = 14;
    private static final int SLOT_LISTA = 16;
    private static final int SLOT_STAFF = 15;

    private final KitsPersonalizados plugin;
    private final Player jugador;
    private Inventory inventory;

    public MenuPrincipalGUI(KitsPersonalizados plugin, Player jugador) {
        this.plugin = plugin;
        this.jugador = jugador;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.colorear("&8&lKitsPersonalizados"));
        boolean esAdmin = jugador.hasPermission("kitspersonalizados.admin");
        boolean puedeDar = jugador.hasPermission("kitspersonalizados.admin.dar");

        if (esAdmin) {
            inventory.setItem(SLOT_CREAR, new ItemBuilder(Material.EMERALD)
                    .nombre("&a&lCrear kit")
                    .lore(List.of("&7Abre el yunque para escribir", "&7el nombre y arrancar el asistente."))
                    .build());
            inventory.setItem(SLOT_EDITAR, new ItemBuilder(Material.ANVIL)
                    .nombre("&e&lEditar kit")
                    .lore(List.of("&7Elegí un kit existente", "&7para reabrir el asistente."))
                    .build());
            inventory.setItem(SLOT_BORRAR, new ItemBuilder(Material.BARRIER)
                    .nombre("&c&lBorrar kit")
                    .lore(List.of("&7Elegí un kit para eliminarlo", "&7(con confirmación)."))
                    .build());
            inventory.setItem(SLOT_LISTAR, new ItemBuilder(Material.BOOKSHELF)
                    .nombre("&b&lListar kits")
                    .lore(List.of("&7Vista completa de todos los kits", "&7con sus permisos y detalles."))
                    .build());
        }
        if (puedeDar) {
            inventory.setItem(SLOT_DAR, new ItemBuilder(Material.PLAYER_HEAD)
                    .nombre("&d&lDar kit")
                    .lore(List.of("&7Elegí un kit y un jugador", "&7conectado para entregárselo."))
                    .build());
        }

        inventory.setItem(SLOT_LISTA, new ItemBuilder(Material.CHEST)
                .nombre("&2&lMis kits")
                .lore(List.of("&7Kits de Usuarios a los que", "&7tenés acceso (reclamo o compra)."))
                .build());
        inventory.setItem(SLOT_STAFF, new ItemBuilder(Material.COMMAND_BLOCK)
                .nombre("&4&lKits de Staff")
                .lore(List.of("&7Kits de Staff a los que", "&7tenés acceso. Reclamo directo."))
                .build());

        jugador.openInventory(inventory);
    }

    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        boolean esAdmin = jugador.hasPermission("kitspersonalizados.admin");
        boolean puedeDar = jugador.hasPermission("kitspersonalizados.admin.dar");

        if (slot == SLOT_CREAR && esAdmin) {
            jugador.closeInventory();
            new AnvilNameGUI(plugin, jugador).abrir();
        } else if (slot == SLOT_EDITAR && esAdmin) {
            new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.EDITAR, 0).abrir();
        } else if (slot == SLOT_BORRAR && esAdmin) {
            new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.BORRAR, 0).abrir();
        } else if (slot == SLOT_LISTAR && esAdmin) {
            new AdminOverviewGUI(plugin, jugador, 0).abrir();
        } else if (slot == SLOT_DAR && puedeDar) {
            new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.DAR, 0).abrir();
        } else if (slot == SLOT_LISTA) {
            new KitListaGUI(plugin, jugador, 0).abrir();
        } else if (slot == SLOT_STAFF) {
            new KitStaffListaGUI(plugin, jugador, 0).abrir();
        }
    }
}
