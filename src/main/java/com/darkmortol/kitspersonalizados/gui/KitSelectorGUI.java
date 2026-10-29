package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.MaterialArmadura;
import com.darkmortol.kitspersonalizados.model.VisibilidadKit;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Pantalla visual compartida por /kitsp editar, /kitsp borrar y /kitsp dar:
 * muestra TODOS los kits existentes (vista de administrador) para elegir
 * sobre cuál actuar.
 */
public class KitSelectorGUI implements InventoryHolder, ClickeableGUI {

    public enum Modo { EDITAR, BORRAR, DAR }

    private static final int ITEMS_POR_PAGINA = 45;
    private static final int SLOT_ANTERIOR = 45;
    private static final int SLOT_INFO = 49;
    private static final int SLOT_SIGUIENTE = 53;
    private static final int SLOT_CERRAR = 48;

    private final KitsPersonalizados plugin;
    private final Player admin;
    private final Modo modo;
    private final int pagina;
    private final List<Kit> kits;
    private Inventory inventory;

    public KitSelectorGUI(KitsPersonalizados plugin, Player admin, Modo modo, int pagina) {
        this.plugin = plugin;
        this.admin = admin;
        this.modo = modo;
        this.pagina = pagina;
        this.kits = plugin.getKitManager().getKits().values().stream()
                .sorted(Comparator.comparing(Kit::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir() {
        construir();
        admin.openInventory(inventory);
    }

    private String tituloModo() {
        return switch (modo) {
            case EDITAR -> "Editar kit";
            case BORRAR -> "Borrar kit";
            case DAR -> "Elegir kit a entregar";
        };
    }

    private void construir() {
        inventory = Bukkit.createInventory(this, 54, MessageUtil.colorear("&8" + tituloModo() + " (" + kits.size() + ")"));

        int inicio = pagina * ITEMS_POR_PAGINA;
        int fin = Math.min(inicio + ITEMS_POR_PAGINA, kits.size());
        for (int i = inicio; i < fin; i++) {
            inventory.setItem(i - inicio, construirIcono(kits.get(i)));
        }

        if (kits.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .nombre("&cNo hay kits creados todavía")
                    .lore(List.of("&7Usá /kitsp crear para hacer el primero."))
                    .build());
        }

        if (pagina > 0) {
            inventory.setItem(SLOT_ANTERIOR, new ItemBuilder(Material.ARROW).nombre("&e« Página anterior").build());
        }
        if (fin < kits.size()) {
            inventory.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.ARROW).nombre("&ePágina siguiente »").build());
        }
        inventory.setItem(SLOT_INFO, new ItemBuilder(Material.PAPER)
                .nombre("&bPágina " + (pagina + 1))
                .lore(List.of("&7Click en un kit para " + accionLore()))
                .build());
        inventory.setItem(SLOT_CERRAR, new ItemBuilder(Material.BARRIER).nombre("&cCerrar").build());
    }

    private String accionLore() {
        return switch (modo) {
            case EDITAR -> "editarlo";
            case BORRAR -> "borrarlo";
            case DAR -> "elegirlo";
        };
    }

    private ItemStack construirIcono(Kit kit) {
        Material icono = iconoDe(kit);
        boolean staff = kit.getVisibilidad() == VisibilidadKit.STAFF;
        ItemBuilder builder = new ItemBuilder(icono).nombre("&b&l" + kit.getNombre());

        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(staff ? "&7Visibilidad: &cStaff" : "&7Visibilidad: &aUsuarios");
        if (!staff) {
            lore.add("&7Cooldown: &f" + kit.getCooldown().getEtiqueta());
            if (kit.esComprable()) {
                lore.add("&7Precio: &6" + plugin.getEconomia().formatear(kit.getPrecio()));
            }
        }
        lore.add("");
        lore.add("&eClick para " + accionLore());
        builder.lore(lore);
        return builder.build();
    }

    private Material iconoDe(Kit kit) {
        if (kit.getMaterialArmadura() != MaterialArmadura.NINGUNA) {
            return kit.getMaterialArmadura().getIcono();
        }
        if (!kit.getArmasHerramientas().isEmpty()) {
            KitItemArma primero = kit.getArmasHerramientas().get(0);
            return primero.getMaterial();
        }
        return Material.CHEST;
    }

    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();

        if (slot == SLOT_CERRAR) {
            admin.closeInventory();
            return;
        }
        if (slot == SLOT_ANTERIOR && pagina > 0) {
            new KitSelectorGUI(plugin, admin, modo, pagina - 1).abrir();
            return;
        }
        if (slot == SLOT_SIGUIENTE && (pagina + 1) * ITEMS_POR_PAGINA < kits.size()) {
            new KitSelectorGUI(plugin, admin, modo, pagina + 1).abrir();
            return;
        }

        int indice = pagina * ITEMS_POR_PAGINA + slot;
        if (slot < 0 || slot >= ITEMS_POR_PAGINA || indice >= kits.size()) return;

        Kit kit = kits.get(indice);
        switch (modo) {
            case EDITAR -> {
                admin.closeInventory();
                KitCreationSession sesion = new KitCreationSession(plugin, admin, kit, true);
                GUIListener.iniciarSesion(sesion);
                new VisibilidadGUI(sesion).abrir(admin);
            }
            case BORRAR -> new ConfirmarBorradoGUI(plugin, admin, kit).abrir();
            case DAR -> new SeleccionarJugadorGUI(plugin, admin, kit, 0).abrir();
        }
    }
}
