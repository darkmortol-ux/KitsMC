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
 * Pantalla visual de /kitsp listar: vista de administrador con el detalle
 * completo de cada kit (visibilidad, permisos, cooldown/precio). Click en un
 * kit abre directamente su edición.
 */
public class AdminOverviewGUI implements InventoryHolder, ClickeableGUI {

    private static final int ITEMS_POR_PAGINA = 45;
    private static final int SLOT_ANTERIOR = 45;
    private static final int SLOT_INFO = 49;
    private static final int SLOT_SIGUIENTE = 53;
    private static final int SLOT_CERRAR = 48;

    private final KitsPersonalizados plugin;
    private final Player admin;
    private final int pagina;
    private final List<Kit> kits;
    private Inventory inventory;

    public AdminOverviewGUI(KitsPersonalizados plugin, Player admin, int pagina) {
        this.plugin = plugin;
        this.admin = admin;
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

    private void construir() {
        inventory = Bukkit.createInventory(this, 54, MessageUtil.colorear("&8Todos los kits (" + kits.size() + ")"));

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
                .lore(List.of("&7Click en un kit para editarlo"))
                .build());
        inventory.setItem(SLOT_CERRAR, new ItemBuilder(Material.BARRIER).nombre("&cCerrar").build());
    }

    private ItemStack construirIcono(Kit kit) {
        Material icono = iconoDe(kit);
        boolean staff = kit.getVisibilidad() == VisibilidadKit.STAFF;
        String nombreMin = kit.getNombre().toLowerCase();

        List<String> lore = new java.util.ArrayList<>();
        if (staff) {
            lore.add("&7Visibilidad: &cStaff");
            lore.add("&7Permiso: &fkit." + nombreMin + ".staff");
            lore.add("&7Reclamo: &adirecto, sin restricciones");
        } else {
            lore.add("&7Visibilidad: &aUsuarios");
            if (kit.getCooldown().getSufijoPermiso() == null) {
                lore.add("&7Permiso: &fkit." + nombreMin);
            } else {
                lore.add("&7Permiso: &fkit." + nombreMin + "." + kit.getCooldown().getSufijoPermiso());
            }
            lore.add("&7Cooldown: &f" + kit.getCooldown().getEtiqueta());
            if (kit.esComprable()) {
                lore.add("&7Compra: &6" + plugin.getEconomia().formatear(kit.getPrecio())
                        + " &7(&fkit." + nombreMin + ".buy&7)");
            } else {
                lore.add("&7Compra: &8no disponible");
            }
        }
        lore.add("");
        lore.add("&eClick para editar");

        return new ItemBuilder(icono).nombre("&b&l" + kit.getNombre()).lore(lore).build();
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
            new AdminOverviewGUI(plugin, admin, pagina - 1).abrir();
            return;
        }
        if (slot == SLOT_SIGUIENTE && (pagina + 1) * ITEMS_POR_PAGINA < kits.size()) {
            new AdminOverviewGUI(plugin, admin, pagina + 1).abrir();
            return;
        }

        int indice = pagina * ITEMS_POR_PAGINA + slot;
        if (slot < 0 || slot >= ITEMS_POR_PAGINA || indice >= kits.size()) return;

        Kit kit = kits.get(indice);
        admin.closeInventory();
        KitCreationSession sesion = new KitCreationSession(plugin, admin, kit, true);
        GUIListener.iniciarSesion(sesion);
        new VisibilidadGUI(sesion).abrir(admin);
    }
}
