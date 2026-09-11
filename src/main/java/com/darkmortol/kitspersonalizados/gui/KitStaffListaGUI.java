package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.MaterialArmadura;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pantalla abierta con "/kit staff": muestra únicamente los kits de STAFF a
 * los que el jugador tiene el permiso kit.&lt;nombre&gt;.staff (o es admin).
 * Se reclaman directo al hacer click, sin cooldown ni economía de por medio.
 */
public class KitStaffListaGUI implements InventoryHolder {

    private static final int ITEMS_POR_PAGINA = 45;
    private static final int SLOT_ANTERIOR = 45;
    private static final int SLOT_INFO = 49;
    private static final int SLOT_SIGUIENTE = 53;
    private static final int SLOT_CERRAR = 48;

    private final KitsPersonalizados plugin;
    private final Player jugador;
    private final int pagina;
    private final List<Kit> kitsVisibles;
    private Inventory inventory;

    public KitStaffListaGUI(KitsPersonalizados plugin, Player jugador, int pagina) {
        this.plugin = plugin;
        this.jugador = jugador;
        this.pagina = pagina;
        this.kitsVisibles = plugin.getKitManager().getKits().values().stream()
                .filter(kit -> plugin.getClaimService().tieneAccesoStaff(jugador, kit))
                .sorted(Comparator.comparing(Kit::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir() {
        construir();
        jugador.openInventory(inventory);
    }

    private void construir() {
        String titulo = MessageUtil.colorear("&8Kits de Staff (" + kitsVisibles.size() + ")");
        inventory = Bukkit.createInventory(this, 54, titulo);

        int inicio = pagina * ITEMS_POR_PAGINA;
        int fin = Math.min(inicio + ITEMS_POR_PAGINA, kitsVisibles.size());

        for (int i = inicio; i < fin; i++) {
            inventory.setItem(i - inicio, construirIcono(kitsVisibles.get(i)));
        }

        if (kitsVisibles.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .nombre("&cNo tenés acceso a ningún kit de staff")
                    .lore(List.of("&7Pídele a un admin el permiso kit.<nombre>.staff"))
                    .build());
        }

        if (pagina > 0) {
            inventory.setItem(SLOT_ANTERIOR, new ItemBuilder(Material.ARROW).nombre("&e« Página anterior").build());
        }
        if (fin < kitsVisibles.size()) {
            inventory.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.ARROW).nombre("&ePágina siguiente »").build());
        }
        inventory.setItem(SLOT_INFO, new ItemBuilder(Material.PAPER)
                .nombre("&bPágina " + (pagina + 1))
                .lore(List.of("&7Click en un kit para reclamarlo", "&7Sin cooldown ni costo"))
                .build());
        inventory.setItem(SLOT_CERRAR, new ItemBuilder(Material.BARRIER).nombre("&cCerrar").build());
    }

    private ItemStack construirIcono(Kit kit) {
        ItemBuilder builder = new ItemBuilder(iconoDe(kit)).nombre("&c&l" + kit.getNombre());
        List<String> lore = new ArrayList<>();
        lore.add("&7Reclamo: &aDisponible ahora");
        lore.add("&7Sin cooldown ni costo (kit de staff)");
        lore.add("");
        lore.add("&eClick para reclamar");
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
            jugador.closeInventory();
            return;
        }
        if (slot == SLOT_ANTERIOR && pagina > 0) {
            new KitStaffListaGUI(plugin, jugador, pagina - 1).abrir();
            return;
        }
        if (slot == SLOT_SIGUIENTE && (pagina + 1) * ITEMS_POR_PAGINA < kitsVisibles.size()) {
            new KitStaffListaGUI(plugin, jugador, pagina + 1).abrir();
            return;
        }

        int indice = pagina * ITEMS_POR_PAGINA + slot;
        if (slot >= 0 && slot < ITEMS_POR_PAGINA && indice < kitsVisibles.size()) {
            Kit kit = kitsVisibles.get(indice);
            jugador.closeInventory();
            plugin.getClaimService().reclamar(jugador, kit);
        }
    }
}
