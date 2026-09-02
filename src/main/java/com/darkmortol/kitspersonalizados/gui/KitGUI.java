package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base para todas las pantallas del asistente de creación/edición de kits.
 * Cada pantalla es un Inventory de 54 slots: contenido en las filas 1-4 y
 * una barra de navegación fija en la última fila (45-53).
 */
public abstract class KitGUI implements InventoryHolder {

    /**
     * Slots de la barra de navegación (última fila del inventario), calculados
     * según el tamaño real de cada pantalla para no salirse de rango en las
     * pantallas que usan menos de 6 filas.
     */
    protected final int SLOT_CANCELAR;
    protected final int SLOT_ATRAS;
    protected final int SLOT_INFO;
    protected final int SLOT_SIGUIENTE;

    protected final KitCreationSession sesion;
    protected Inventory inventory;

    protected KitGUI(KitCreationSession sesion, String titulo, int filas) {
        this.sesion = sesion;
        int tamano = filas * 9;
        this.inventory = Bukkit.createInventory(this, tamano, MessageUtil.colorear(titulo));
        int base = tamano - 9;
        this.SLOT_CANCELAR = base;
        this.SLOT_ATRAS = base + 3;
        this.SLOT_INFO = base + 4;
        this.SLOT_SIGUIENTE = base + 5;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir(Player jugador) {
        construir();
        jugador.openInventory(inventory);
    }

    /** Reconstruye todo el contenido del inventario (se llama antes de abrir y tras cada cambio). */
    protected abstract void construir();

    /** Maneja un click dentro de esta pantalla. El evento ya viene cancelado. */
    public abstract void onClick(InventoryClickEvent evento);

    protected void refrescar() {
        inventory.clear();
        construir();
    }

    protected void ponerBarraNavegacion(boolean permiteAtras, String textoSiguiente) {
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER)
                .nombre("&cCancelar")
                .agregarLinea("&7Cancela la creación del kit.")
                .build());

        if (permiteAtras) {
            inventory.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW)
                    .nombre("&e« Atrás")
                    .build());
        }

        inventory.setItem(SLOT_INFO, new ItemBuilder(Material.PAPER)
                .nombre("&bKit: &f" + sesion.getKit().getNombre())
                .agregarLinea("&7Paso " + sesion.getPasoActual() + " de 8")
                .build());

        if (textoSiguiente != null) {
            inventory.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.EMERALD)
                    .nombre("&a" + textoSiguiente)
                    .build());
        }
    }

    protected ItemStack conLore(ItemStack base, List<String> lore) {
        return new ItemBuilder(base).lore(lore).build();
    }
}
