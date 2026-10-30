package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.VisibilidadKit;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Pantalla 1/N (nueva, primera del asistente): elegir si el kit es para
 * usuarios en general (Normal + VIP, vía /kit lista) o solo para staff
 * (vía /kit staff, permiso único kit.&lt;nombre&gt;.staff, sin cooldown ni precio).
 */
public class VisibilidadGUI extends KitGUI {

    private static final int SLOT_USUARIOS = 12;
    private static final int SLOT_STAFF = 14;

    public VisibilidadGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 1/" + sesion.getKit().totalPasos() + " Visibilidad", 4);
    }

    @Override
    protected void construir() {
        VisibilidadKit actual = sesion.getKit().getVisibilidad();
        boolean esUsuarios = actual == VisibilidadKit.NORMAL;
        boolean esStaff = actual == VisibilidadKit.STAFF;

        inventory.setItem(SLOT_USUARIOS, new ItemBuilder(Material.PLAYER_HEAD)
                .nombre((esUsuarios ? "&a✔ " : "&f") + "Usuarios (Normal + VIP)")
                .lore(List.of(
                        "&7Aparece en &f/kit lista&7.",
                        "&7Se reclama por permiso normal",
                        "&7o se compra con la economía del servidor.",
                        "&7Tiene cooldown y precio configurables.",
                        "",
                        esUsuarios ? "&aSeleccionado" : "&eClick para seleccionar"))
                .build());

        inventory.setItem(SLOT_STAFF, new ItemBuilder(Material.COMMAND_BLOCK)
                .nombre((esStaff ? "&a✔ " : "&f") + "Solo Staff")
                .lore(List.of(
                        "&7Aparece en &f/kit staff&7.",
                        "&7Permiso único: &fkit.<nombre>.staff",
                        "&7Sin cooldown ni precio: reclamo directo",
                        "&7si tiene el permiso, sin restricciones.",
                        "",
                        esStaff ? "&aSeleccionado" : "&eClick para seleccionar"))
                .build());

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
            new ArmorMaterialGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_USUARIOS) {
            sesion.getKit().setVisibilidad(VisibilidadKit.NORMAL);
            refrescar();
            return;
        }
        if (slot == SLOT_STAFF) {
            sesion.getKit().setVisibilidad(VisibilidadKit.STAFF);
            refrescar();
        }
    }
}
