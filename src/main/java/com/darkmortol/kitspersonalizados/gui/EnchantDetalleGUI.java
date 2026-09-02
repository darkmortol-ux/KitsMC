package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.util.EnchantUtil;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;

/**
 * Sub-pantalla de la 5/7: click izquierdo +1 nivel, click derecho -1 nivel,
 * shift+click izquierdo +10, shift+click derecho -10. Rango 0-100 (0 = sin encantar).
 */
public class EnchantDetalleGUI extends KitGUI {

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

    public EnchantDetalleGUI(KitCreationSession sesion) {
        super(sesion, tituloDe(sesion), 4);
    }

    private static String tituloDe(KitCreationSession sesion) {
        if (sesion.getPiezaSeleccionadaEncantar() != null) {
            return "&8Kit » Encantar " + sesion.getPiezaSeleccionadaEncantar().name();
        }
        return "&8Kit » Encantar " + sesion.getArmaSeleccionadaEncantar().getTipoBase().getNombre();
    }

    private List<Enchantment> aplicables() {
        if (sesion.getPiezaSeleccionadaEncantar() != null) {
            return EnchantUtil.aplicablesArmadura(sesion.getPiezaSeleccionadaEncantar());
        }
        return EnchantUtil.aplicablesArma(sesion.getArmaSeleccionadaEncantar().getTipoBase());
    }

    private Map<Enchantment, Integer> mapaActual() {
        if (sesion.getPiezaSeleccionadaEncantar() != null) {
            return sesion.getKit().getEncantamientosArmadura().get(sesion.getPiezaSeleccionadaEncantar());
        }
        return sesion.getArmaSeleccionadaEncantar().getEncantamientos();
    }

    @Override
    protected void construir() {
        List<Enchantment> lista = aplicables();
        Map<Enchantment, Integer> mapa = mapaActual();
        for (int i = 0; i < lista.size() && i < SLOTS.length; i++) {
            Enchantment ench = lista.get(i);
            int nivel = mapa.getOrDefault(ench, 0);
            inventory.setItem(SLOTS[i], new ItemBuilder(Material.ENCHANTED_BOOK)
                    .nombre((nivel > 0 ? "&a" : "&f") + EnchantUtil.nombre(ench) + " &7(nivel " + nivel + ")")
                    .lore(List.of(
                            "&7Click izq: &f+1  &7Click der: &f-1",
                            "&7Shift+izq: &f+10  &7Shift+der: &f-10",
                            "&7Rango: 0-100"))
                    .build());
        }
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(Material.ARROW)
                .nombre("&e« Volver").build());
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            sesion.setPasoActual(5);
            new EnchantSeccionesGUI(sesion).abrir(jugador);
            return;
        }

        List<Enchantment> lista = aplicables();
        for (int i = 0; i < SLOTS.length && i < lista.size(); i++) {
            if (SLOTS[i] != slot) continue;
            Enchantment ench = lista.get(i);
            Map<Enchantment, Integer> mapa = mapaActual();
            int nivelActual = mapa.getOrDefault(ench, 0);
            int delta = evento.isShiftClick() ? 10 : 1;
            if (evento.getClick() == ClickType.RIGHT || evento.getClick() == ClickType.SHIFT_RIGHT) delta = -delta;
            int nuevoNivel = Math.max(0, Math.min(100, nivelActual + delta));
            if (nuevoNivel == 0) {
                mapa.remove(ench);
            } else {
                mapa.put(ench, nuevoNivel);
            }
            refrescar();
            return;
        }
    }
}
