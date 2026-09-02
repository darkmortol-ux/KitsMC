package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Optional;

/**
 * Pantalla 2/7: elegir qué armas y herramientas tendrá el kit (y con qué material).
 */
public class WeaponsToolsGUI extends KitGUI {

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20};

    public WeaponsToolsGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 2/8 Armas y herramientas", 6);
    }

    @Override
    protected void construir() {
        TipoArmaHerramienta[] tipos = TipoArmaHerramienta.values();
        for (int i = 0; i < tipos.length; i++) {
            TipoArmaHerramienta tipo = tipos[i];
            Optional<KitItemArma> existente = sesion.getKit().getArmasHerramientas().stream()
                    .filter(a -> a.getTipoBase() == tipo).findFirst();

            ItemBuilder builder;
            if (existente.isPresent()) {
                builder = new ItemBuilder(existente.get().getMaterial())
                        .nombre("&a✔ " + tipo.getNombre())
                        .lore(List.of("&7Material: &f" + existente.get().getMaterial().name(),
                                "&cClick para quitar del kit"))
                        .brillo(true);
            } else {
                builder = new ItemBuilder(tipo.getMaterialParaTier(TipoArmaHerramienta.Tier.HIERRO))
                        .nombre("&f" + tipo.getNombre())
                        .lore(List.of("&7Click para agregar al kit"));
            }
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
            sesion.setPasoActual(1);
            new ArmorMaterialGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(3);
            new PotionsGUI(sesion).abrir(jugador);
            return;
        }
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] != slot) continue;
            TipoArmaHerramienta tipo = TipoArmaHerramienta.values()[i];
            Optional<KitItemArma> existente = sesion.getKit().getArmasHerramientas().stream()
                    .filter(a -> a.getTipoBase() == tipo).findFirst();
            if (existente.isPresent()) {
                sesion.getKit().getArmasHerramientas().remove(existente.get());
                refrescar();
            } else if (tipo.tieneTiers()) {
                sesion.setTipoTemporalArma(tipo);
                new MaterialTierGUI(sesion).abrir(jugador);
            } else {
                sesion.getKit().getArmasHerramientas().add(new KitItemArma(tipo, tipo.getMaterialParaTier(TipoArmaHerramienta.Tier.HIERRO)));
                refrescar();
            }
            return;
        }
    }
}
