package com.darkmortol.kitspersonalizados.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Escucha todos los clicks/arrastres dentro de las pantallas del plugin
 * (asistente de creación y las demás pantallas sueltas de /kitsp) y guarda
 * las sesiones activas de cada administrador.
 */
public class GUIListener implements Listener {

    private static final Map<UUID, KitCreationSession> SESIONES = new HashMap<>();

    public static void iniciarSesion(KitCreationSession sesion) {
        SESIONES.put(sesion.getAdmin().getUniqueId(), sesion);
    }

    public static KitCreationSession obtenerSesion(UUID uuid) {
        return SESIONES.get(uuid);
    }

    public static void cancelarSesion(Player admin) {
        SESIONES.remove(admin.getUniqueId());
        admin.closeInventory();
        admin.sendMessage("§cCreación del kit cancelada.");
    }

    public static void terminarSesion(Player admin) {
        SESIONES.remove(admin.getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent evento) {
        if (evento.getInventory().getHolder() instanceof KitGUI gui) {
            evento.setCancelled(true);
            if (evento.getClickedInventory() == null) return;
            if (!evento.getClickedInventory().equals(evento.getView().getTopInventory())) return;
            gui.onClick(evento);
            return;
        }
        if (evento.getInventory().getHolder() instanceof ClickeableGUI clickeable) {
            evento.setCancelled(true);
            if (evento.getClickedInventory() == null) return;
            if (!evento.getClickedInventory().equals(evento.getView().getTopInventory())) return;
            clickeable.onClick(evento);
            return;
        }
        if (evento.getInventory().getHolder() instanceof AnvilNameGUI anvil) {
            evento.setCancelled(true);
            if (evento.getRawSlot() != 2) return;
            if (evento.getClickedInventory() == null) return;
            if (!evento.getClickedInventory().equals(evento.getView().getTopInventory())) return;

            String nombre = null;
            if (evento.getInventory() instanceof AnvilInventory anvilInv) {
                String texto = anvilInv.getRenameText();
                if (texto != null && !texto.isEmpty()) nombre = texto;
            }
            if (nombre == null) {
                var resultado = evento.getCurrentItem();
                if (resultado != null && resultado.hasItemMeta() && resultado.getItemMeta().hasDisplayName()) {
                    nombre = resultado.getItemMeta().getDisplayName();
                }
            }
            anvil.confirmarNombre(nombre);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent evento) {
        if (evento.getInventory().getHolder() instanceof AnvilNameGUI) {
            evento.getInventory().setRepairCost(0);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent evento) {
        if (evento.getInventory().getHolder() instanceof KitGUI
                || evento.getInventory().getHolder() instanceof ClickeableGUI
                || evento.getInventory().getHolder() instanceof AnvilNameGUI) {
            evento.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evento) {
        SESIONES.remove(evento.getPlayer().getUniqueId());
    }
}
