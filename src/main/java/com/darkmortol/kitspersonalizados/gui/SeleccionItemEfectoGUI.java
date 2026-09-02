package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.CategoriaEfecto;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Sub-pantalla de la 6/7: elegir a cuál arma o herramienta del kit se le
 * asignará el efecto personalizado (solo se puede asignar a una).
 */
public class SeleccionItemEfectoGUI extends KitGUI {

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20};

    private final TipoArmaHerramienta.Categoria categoria;
    private final List<KitItemArma> items;

    public SeleccionItemEfectoGUI(KitCreationSession sesion, TipoArmaHerramienta.Categoria categoria) {
        super(sesion, "&8Kit » Elegir " + (categoria == TipoArmaHerramienta.Categoria.ARMA ? "arma" : "herramienta"), 4);
        this.categoria = categoria;
        this.items = sesion.getKit().getArmasHerramientas().stream()
                .filter(a -> a.esCategoria(categoria)).toList();
    }

    @Override
    protected void construir() {
        for (int i = 0; i < items.size() && i < SLOTS.length; i++) {
            KitItemArma item = items.get(i);
            inventory.setItem(SLOTS[i], new ItemBuilder(item.getMaterial())
                    .nombre("&f" + item.getTipoBase().getNombre())
                    .lore(List.of("&7Efecto actual: &f" + (item.getEfectoId() != null ? item.getEfectoId() : "Ninguno"),
                            "&7Click para elegir efecto"))
                    .build());
        }
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(Material.ARROW).nombre("&e« Volver").build());
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            sesion.setPasoActual(6);
            new EfectosMenuGUI(sesion).abrir(jugador);
            return;
        }
        for (int i = 0; i < SLOTS.length && i < items.size(); i++) {
            if (SLOTS[i] == slot) {
                sesion.setArmaSeleccionadaParaEfecto(items.get(i));
                CategoriaEfecto categoriaEfecto = categoria == TipoArmaHerramienta.Categoria.ARMA
                        ? CategoriaEfecto.ARMAS : CategoriaEfecto.HERRAMIENTAS;
                new EfectoListaGUI(sesion, categoriaEfecto, items.get(i)).abrir(jugador);
                return;
            }
        }
    }
}
