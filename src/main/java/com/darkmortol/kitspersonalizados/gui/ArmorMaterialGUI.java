package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.MaterialArmadura;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Pantalla 1/7: elegir el material de la armadura (o ninguna).
 */
public class ArmorMaterialGUI extends KitGUI {

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16};

    public ArmorMaterialGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 1/8 Material de armadura", 6);
    }

    @Override
    protected void construir() {
        MaterialArmadura[] valores = MaterialArmadura.values();
        for (int i = 0; i < valores.length; i++) {
            MaterialArmadura ma = valores[i];
            Material icono = ma.getIcono();
            boolean seleccionado = sesion.getKit().getMaterialArmadura() == ma;
            ItemBuilder builder = new ItemBuilder(icono)
                    .nombre((seleccionado ? "&a✔ " : "&f") + ma.getNombre())
                    .lore(List.of(seleccionado ? "&7Seleccionado" : "&7Click para seleccionar"));
            if (seleccionado) builder.brillo(true);
            inventory.setItem(SLOTS[i], builder.build());
        }
        ponerBarraNavegacion(false, "Siguiente »");
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            GUIListener.cancelarSesion(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(2);
            new WeaponsToolsGUI(sesion).abrir(jugador);
            return;
        }
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] == slot) {
                sesion.getKit().setMaterialArmadura(MaterialArmadura.values()[i]);
                refrescar();
                return;
            }
        }
    }
}
