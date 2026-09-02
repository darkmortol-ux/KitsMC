package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Pantalla 8/8: define el precio en la economía del servidor (Vault) para
 * poder comprar el kit con /kit &lt;nombre&gt;. $0 = no se puede comprar.
 * Permiso de compra: kit.&lt;nombre&gt;.buy
 */
public class PrecioGUI extends KitGUI {

    private static final int SLOT_MENOS_500 = 10;
    private static final int SLOT_MENOS_100 = 11;
    private static final int SLOT_MENOS_50 = 12;
    private static final int SLOT_PRECIO_ACTUAL = 13;
    private static final int SLOT_MAS_50 = 14;
    private static final int SLOT_MAS_100 = 15;
    private static final int SLOT_MAS_500 = 16;

    public PrecioGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 8/8 Precio (economía)", 4);
    }

    @Override
    protected void construir() {
        double precio = sesion.getKit().getPrecio();

        inventory.setItem(SLOT_MENOS_500, boton(Material.RED_STAINED_GLASS_PANE, "&c-$500"));
        inventory.setItem(SLOT_MENOS_100, boton(Material.RED_STAINED_GLASS_PANE, "&c-$100"));
        inventory.setItem(SLOT_MENOS_50, boton(Material.RED_STAINED_GLASS_PANE, "&c-$50"));

        inventory.setItem(SLOT_PRECIO_ACTUAL, new ItemBuilder(precio > 0 ? Material.EMERALD : Material.BARRIER)
                .nombre(precio > 0 ? "&6Precio: &f$" + formatearPrecio(precio) : "&7Gratis (no comprable)")
                .lore(List.of(
                        precio > 0
                                ? "&7Los jugadores con &fkit." + sesion.getKit().getNombre().toLowerCase() + ".buy"
                                : "&7Con $0 el kit NO se puede comprar,",
                        precio > 0
                                ? "&7podrán comprar este kit por &f$" + formatearPrecio(precio)
                                : "&7solo se reclama por permiso normal.",
                        "&7Requiere el plugin Vault + un plugin de economía."))
                .build());

        inventory.setItem(SLOT_MAS_50, boton(Material.LIME_STAINED_GLASS_PANE, "&a+$50"));
        inventory.setItem(SLOT_MAS_100, boton(Material.LIME_STAINED_GLASS_PANE, "&a+$100"));
        inventory.setItem(SLOT_MAS_500, boton(Material.LIME_STAINED_GLASS_PANE, "&a+$500"));

        ponerBarraNavegacion(true, "✔ Finalizar y guardar");
    }

    private String formatearPrecio(double precio) {
        if (precio == Math.floor(precio)) {
            return String.format("%,.0f", precio);
        }
        return String.format("%,.2f", precio);
    }

    private org.bukkit.inventory.ItemStack boton(Material material, String nombre) {
        return new ItemBuilder(material).nombre(nombre).build();
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            GUIListener.cancelarSesion(jugador);
            return;
        }
        if (slot == SLOT_ATRAS) {
            sesion.setPasoActual(7);
            new CooldownGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.getPlugin().getKitManager().guardar(sesion.getKit());
            GUIListener.terminarSesion(jugador);
            jugador.closeInventory();
            jugador.sendMessage(MessageUtil.colorear("&aEl kit '&f" + sesion.getKit().getNombre() + "&a' fue guardado correctamente."));
            return;
        }

        double precio = sesion.getKit().getPrecio();
        double delta = 0;
        if (slot == SLOT_MAS_50) delta = 50;
        else if (slot == SLOT_MAS_100) delta = 100;
        else if (slot == SLOT_MAS_500) delta = 500;
        else if (slot == SLOT_MENOS_50) delta = -50;
        else if (slot == SLOT_MENOS_100) delta = -100;
        else if (slot == SLOT_MENOS_500) delta = -500;
        else return;

        sesion.getKit().setPrecio(precio + delta);
        refrescar();
    }
}
