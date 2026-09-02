package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.KitCooldownType;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Pantalla 7/8: elegir cada cuánto se puede reclamar el kit.
 */
public class CooldownGUI extends KitGUI {

    private static final int[] SLOTS = {11, 12, 13, 14, 15};
    private static final Material[] ICONOS = {
            Material.NETHER_STAR, Material.CLOCK, Material.SUNFLOWER, Material.BOOKSHELF, Material.CALIBRATED_SCULK_SENSOR
    };

    public CooldownGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 7/8 Cooldown", 4);
    }

    @Override
    protected void construir() {
        KitCooldownType[] valores = KitCooldownType.values();
        for (int i = 0; i < valores.length; i++) {
            boolean seleccionado = sesion.getKit().getCooldown() == valores[i];
            ItemBuilder builder = new ItemBuilder(ICONOS[i])
                    .nombre((seleccionado ? "&a✔ " : "&f") + valores[i].getEtiqueta())
                    .lore(List.of(valores[i].getSufijoPermiso() == null
                            ? "&7Permiso: kit.<nombre>"
                            : "&7Permiso: kit.<nombre>." + valores[i].getSufijoPermiso()));
            if (seleccionado) builder.brillo(true);
            inventory.setItem(SLOTS[i], builder.build());
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
            sesion.setPasoActual(6);
            new EfectosMenuGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(8);
            new PrecioGUI(sesion).abrir(jugador);
            return;
        }
        KitCooldownType[] valores = KitCooldownType.values();
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] == slot) {
                sesion.getKit().setCooldown(valores[i]);
                refrescar();
                return;
            }
        }
    }
}
