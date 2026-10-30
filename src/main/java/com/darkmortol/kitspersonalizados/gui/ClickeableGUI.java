package com.darkmortol.kitspersonalizados.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Pantallas (que no son parte del asistente de creación, es decir no
 * extienden KitGUI) cuyos clicks maneja GUIListener de forma genérica.
 */
public interface ClickeableGUI {
    void onClick(InventoryClickEvent evento);
}
