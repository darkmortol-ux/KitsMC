package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla 5/7: secciones para configurar los encantamientos (nivel 1-100) de
 * cada pieza de armadura y de cada arma/herramienta del kit.
 */
public class EnchantSeccionesGUI extends KitGUI {

    private static final int[] SLOTS_ARMADURA = {10, 11, 12, 13};
    private static final int[] SLOTS_ARMAS = {19, 20, 21, 22, 23, 24, 25, 28, 29};

    public EnchantSeccionesGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 5/8 Encantamientos", 6);
    }

    @Override
    protected void construir() {
        Kit kit = sesion.getKit();
        Kit.PiezaArmadura[] piezas = Kit.PiezaArmadura.values();
        for (int i = 0; i < piezas.length; i++) {
            Material material = kit.getMaterialArmadura().getPieza(piezas[i]);
            if (material == null) {
                inventory.setItem(SLOTS_ARMADURA[i], new ItemBuilder(Material.BARRIER)
                        .nombre("&7Sin armadura").build());
                continue;
            }
            int cantidad = kit.getEncantamientosArmadura().get(piezas[i]).size();
            inventory.setItem(SLOTS_ARMADURA[i], new ItemBuilder(material)
                    .nombre("&f" + piezas[i].name())
                    .lore(List.of("&7Encantamientos: &e" + cantidad, "&7Click para editar"))
                    .build());
        }

        List<KitItemArma> armas = kit.getArmasHerramientas();
        for (int i = 0; i < armas.size() && i < SLOTS_ARMAS.length; i++) {
            KitItemArma arma = armas.get(i);
            inventory.setItem(SLOTS_ARMAS[i], new ItemBuilder(arma.getMaterial())
                    .nombre("&f" + arma.getTipoBase().getNombre())
                    .lore(List.of("&7Encantamientos: &e" + arma.getEncantamientos().size(), "&7Click para editar"))
                    .build());
        }

        ponerBarraNavegacion(true, "Siguiente »");
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
            sesion.setPasoActual(4);
            new FoodGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(6);
            new EfectosMenuGUI(sesion).abrir(jugador);
            return;
        }
        Kit.PiezaArmadura[] piezas = Kit.PiezaArmadura.values();
        for (int i = 0; i < SLOTS_ARMADURA.length; i++) {
            if (SLOTS_ARMADURA[i] == slot) {
                if (sesion.getKit().getMaterialArmadura().getPieza(piezas[i]) == null) return;
                sesion.setPiezaSeleccionadaEncantar(piezas[i]);
                new EnchantDetalleGUI(sesion).abrir(jugador);
                return;
            }
        }
        List<KitItemArma> armas = sesion.getKit().getArmasHerramientas();
        for (int i = 0; i < SLOTS_ARMAS.length && i < armas.size(); i++) {
            if (SLOTS_ARMAS[i] == slot) {
                sesion.setArmaSeleccionadaEncantar(armas.get(i));
                new EnchantDetalleGUI(sesion).abrir(jugador);
                return;
            }
        }
    }
}
