package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.CategoriaEfecto;
import com.darkmortol.kitspersonalizados.model.EfectoPersonalizado;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Sub-pantalla de la 6/7: muestra los 16 efectos de una categoría.
 * - ARMADURA: selección única, se guarda en kit.efectoArmaduraId.
 * - ARMAS/HERRAMIENTAS: selección única, se guarda en el KitItemArma objetivo.
 * - VARIOS: multi-selección, se guarda en kit.efectosVariosIds.
 */
public class EfectoListaGUI extends KitGUI {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16, 17,
            19, 20, 21, 22, 23, 24, 25, 26
    };

    private final CategoriaEfecto categoria;
    private final KitItemArma objetivo; // null para ARMADURA y VARIOS

    public EfectoListaGUI(KitCreationSession sesion, CategoriaEfecto categoria, KitItemArma objetivo) {
        super(sesion, "&8Kit » Efectos: " + tituloCategoria(categoria), 4);
        this.categoria = categoria;
        this.objetivo = objetivo;
    }

    private static String tituloCategoria(CategoriaEfecto categoria) {
        return switch (categoria) {
            case ARMADURA -> "Armadura";
            case ARMAS -> "Armas";
            case HERRAMIENTAS -> "Herramientas";
            case VARIOS -> "Varios";
        };
    }

    private boolean estaSeleccionado(String id) {
        return switch (categoria) {
            case ARMADURA -> id.equals(sesion.getKit().getEfectoArmaduraId());
            case ARMAS, HERRAMIENTAS -> objetivo != null && id.equals(objetivo.getEfectoId());
            case VARIOS -> sesion.getKit().getEfectosVariosIds().contains(id);
        };
    }

    @Override
    protected void construir() {
        List<EfectoPersonalizado> efectos = sesion.getPlugin().getEfectos().obtenerPorCategoria(categoria);
        for (int i = 0; i < efectos.size() && i < SLOTS.length; i++) {
            EfectoPersonalizado efecto = efectos.get(i);
            boolean seleccionado = estaSeleccionado(efecto.getId());
            ItemBuilder builder = new ItemBuilder(efecto.getMaterial())
                    .nombre((seleccionado ? "&a✔ " : "&d") + efecto.getNombre())
                    .lore(efecto.getDescripcion());
            if (seleccionado) builder.brillo(true);
            inventory.setItem(SLOTS[i], builder.build());
        }
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(Material.ARROW).nombre("&e« Volver").build());
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            volver(jugador);
            return;
        }

        List<EfectoPersonalizado> efectos = sesion.getPlugin().getEfectos().obtenerPorCategoria(categoria);
        for (int i = 0; i < SLOTS.length && i < efectos.size(); i++) {
            if (SLOTS[i] != slot) continue;
            String id = efectos.get(i).getId();
            aplicarSeleccion(id);
            refrescar();
            return;
        }
    }

    private void aplicarSeleccion(String id) {
        switch (categoria) {
            case ARMADURA -> {
                if (id.equals(sesion.getKit().getEfectoArmaduraId())) {
                    sesion.getKit().setEfectoArmaduraId(null);
                } else {
                    sesion.getKit().setEfectoArmaduraId(id);
                }
            }
            case ARMAS, HERRAMIENTAS -> {
                if (objetivo == null) return;
                if (id.equals(objetivo.getEfectoId())) {
                    objetivo.setEfectoId(null);
                } else {
                    objetivo.setEfectoId(id);
                }
            }
            case VARIOS -> {
                List<String> lista = sesion.getKit().getEfectosVariosIds();
                if (lista.contains(id)) {
                    lista.remove(id);
                } else {
                    lista.add(id);
                }
            }
        }
    }

    private void volver(Player jugador) {
        if (categoria == CategoriaEfecto.ARMAS) {
            new SeleccionItemEfectoGUI(sesion, TipoArmaHerramienta.Categoria.ARMA).abrir(jugador);
        } else if (categoria == CategoriaEfecto.HERRAMIENTAS) {
            new SeleccionItemEfectoGUI(sesion, TipoArmaHerramienta.Categoria.HERRAMIENTA).abrir(jugador);
        } else {
            sesion.setPasoActual(6);
            new EfectosMenuGUI(sesion).abrir(jugador);
        }
    }
}
