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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Pantalla visual de /kitsp dar: elegir a qué jugador conectado entregarle
 * el kit ya seleccionado.
 */
public class SeleccionarJugadorGUI implements InventoryHolder, ClickeableGUI {

    private static final int ITEMS_POR_PAGINA = 45;
    private static final int SLOT_ANTERIOR = 45;
    private static final int SLOT_INFO = 49;
    private static final int SLOT_SIGUIENTE = 53;
    private static final int SLOT_CERRAR = 48;

    private final KitsPersonalizados plugin;
    private final Player admin;
    private final Kit kit;
    private final int pagina;
    private final List<Player> jugadoresOnline;
    private Inventory inventory;

    public SeleccionarJugadorGUI(KitsPersonalizados plugin, Player admin, Kit kit, int pagina) {
        this.plugin = plugin;
        this.admin = admin;
        this.kit = kit;
        this.pagina = pagina;
        List<Player> conectados = new java.util.ArrayList<Player>(Bukkit.getOnlinePlayers());
        conectados.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        this.jugadoresOnline = conectados;
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
        inventory = Bukkit.createInventory(this, 54, MessageUtil.colorear("&8Entregar '" + kit.getNombre() + "' a..."));

        int inicio = pagina * ITEMS_POR_PAGINA;
        int fin = Math.min(inicio + ITEMS_POR_PAGINA, jugadoresOnline.size());
        for (int i = inicio; i < fin; i++) {
            inventory.setItem(i - inicio, construirCabeza(jugadoresOnline.get(i)));
        }

        if (jugadoresOnline.isEmpty()) {
            inventory.setItem(22, new ItemBuilder(Material.BARRIER)
                    .nombre("&cNo hay jugadores conectados")
                    .build());
        }

        if (pagina > 0) {
            inventory.setItem(SLOT_ANTERIOR, new ItemBuilder(Material.ARROW).nombre("&e« Página anterior").build());
        }
        if (fin < jugadoresOnline.size()) {
            inventory.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.ARROW).nombre("&ePágina siguiente »").build());
        }
        inventory.setItem(SLOT_INFO, new ItemBuilder(Material.PAPER)
                .nombre("&bKit: &f" + kit.getNombre())
                .lore(List.of("&7Click en un jugador para entregárselo"))
                .build());
        inventory.setItem(SLOT_CERRAR, new ItemBuilder(Material.BARRIER).nombre("&cCerrar").build());
    }

    private ItemStack construirCabeza(Player jugador) {
        ItemStack cabeza = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) cabeza.getItemMeta();
        meta.setOwningPlayer(jugador);
        meta.setDisplayName(MessageUtil.colorear("&b" + jugador.getName()));
        meta.setLore(List.of(MessageUtil.colorear("&eClick para entregarle '" + kit.getNombre() + "'")));
        cabeza.setItemMeta(meta);
        return cabeza;
    }

    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();

        if (slot == SLOT_CERRAR) {
            admin.closeInventory();
            return;
        }
        if (slot == SLOT_ANTERIOR && pagina > 0) {
            new SeleccionarJugadorGUI(plugin, admin, kit, pagina - 1).abrir();
            return;
        }
        if (slot == SLOT_SIGUIENTE && (pagina + 1) * ITEMS_POR_PAGINA < jugadoresOnline.size()) {
            new SeleccionarJugadorGUI(plugin, admin, kit, pagina + 1).abrir();
            return;
        }

        int indice = pagina * ITEMS_POR_PAGINA + slot;
        if (slot < 0 || slot >= ITEMS_POR_PAGINA || indice >= jugadoresOnline.size()) return;

        Player destino = jugadoresOnline.get(indice);
        admin.closeInventory();
        plugin.getClaimService().entregarItems(destino, kit);
        plugin.getMensajes().enviar(admin, "kit-entregado-admin", "%kit%", kit.getNombre(), "%jugador%", destino.getName());
        if (!destino.equals(admin)) {
            plugin.getMensajes().enviar(destino, "kit-recibido-admin", "%kit%", kit.getNombre());
        }
    }
}
