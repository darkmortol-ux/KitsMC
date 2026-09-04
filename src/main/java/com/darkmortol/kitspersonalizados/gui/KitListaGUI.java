package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.manager.CooldownManager;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitCooldownType;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Pantalla abierta con "/kit lista": muestra únicamente los kits a los que
 * el jugador tiene acceso (reclamo normal o compra). Si el jugador es admin,
 * ve todos los kits para poder revisar la configuración.
 * Al hacer click sobre un kit disponible, se intenta reclamar/comprar al
 * instante (misma lógica que /kit &lt;nombre&gt;).
 */
public class KitListaGUI implements InventoryHolder {

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

    public KitListaGUI(KitsPersonalizados plugin, Player jugador, int pagina) {
        this.plugin = plugin;
        this.jugador = jugador;
        this.pagina = pagina;
        this.kitsVisibles = plugin.getKitManager().getKits().values().stream()
                .filter(kit -> plugin.getClaimService().tieneAcceso(jugador, kit))
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
        String titulo = MessageUtil.colorear("&8Kits disponibles (" + kitsVisibles.size() + ")");
        inventory = Bukkit.createInventory(this, 54, titulo);

        int inicio = pagina * ITEMS_POR_PAGINA;
        int fin = Math.min(inicio + ITEMS_POR_PAGINA, kitsVisibles.size());

        for (int i = inicio; i < fin; i++) {
            inventory.setItem(i - inicio, construirIcono(kitsVisibles.get(i)));
        }

        if (kitsVisibles.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .nombre("&cNo tenés acceso a ningún kit todavía")
                    .lore(List.of("&7Pídele a un admin que te dé permiso,", "&7o revisá si podés comprar alguno."))
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
                .lore(List.of("&7Click en un kit para reclamarlo/comprarlo"))
                .build());
        inventory.setItem(SLOT_CERRAR, new ItemBuilder(Material.BARRIER).nombre("&cCerrar").build());
    }

    private org.bukkit.inventory.ItemStack construirIcono(Kit kit) {
        Material icono = iconoDe(kit);
        ItemBuilder builder = new ItemBuilder(icono).nombre("&b&l" + kit.getNombre());

        java.util.List<String> lore = new java.util.ArrayList<>();
        String permisoReclamo = plugin.getClaimService().permisoReclamo(kit);
        boolean puedeReclamar = jugador.hasPermission(permisoReclamo);

        if (puedeReclamar) {
            if (kit.getCooldown() == KitCooldownType.SIN_HORARIO) {
                lore.add("&7Reclamo: &aDisponible ahora");
            } else {
                long restante = plugin.getCooldownManager().milisegundosRestantes(jugador.getUniqueId(), kit.getNombre(), kit.getCooldown().getMilisegundos());
                if (restante > 0) {
                    lore.add("&7Reclamo: &cEn cooldown (&f" + CooldownManager.formatear(restante) + "&c)");
                } else {
                    lore.add("&7Reclamo: &aDisponible ahora");
                }
                lore.add("&7Frecuencia: &f" + kit.getCooldown().getEtiqueta());
            }
            if (kit.esComprable()) {
                lore.add("&7También comprable por &6" + plugin.getEconomia().formatear(kit.getPrecio()));
            }
        } else if (kit.esComprable() && jugador.hasPermission(plugin.getClaimService().permisoCompra(kit))) {
            lore.add("&7Compra: &6" + plugin.getEconomia().formatear(kit.getPrecio()));
        } else if (jugador.hasPermission("kitspersonalizados.admin")) {
            lore.add("&7(Vista de admin: sin acceso normal)");
        }

        lore.add("");
        lore.add("&eClick para reclamar/comprar");
        builder.lore(lore);
        return builder.build();
    }

    private Material iconoDe(Kit kit) {
        if (kit.getMaterialArmadura() != com.darkmortol.kitspersonalizados.model.MaterialArmadura.NINGUNA) {
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
            new KitListaGUI(plugin, jugador, pagina - 1).abrir();
            return;
        }
        if (slot == SLOT_SIGUIENTE && (pagina + 1) * ITEMS_POR_PAGINA < kitsVisibles.size()) {
            new KitListaGUI(plugin, jugador, pagina + 1).abrir();
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
