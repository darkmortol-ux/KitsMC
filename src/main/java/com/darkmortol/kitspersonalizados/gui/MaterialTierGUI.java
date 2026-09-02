package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Sub-pantalla de la 2/7: elegir el material (tier) del arma/herramienta seleccionada.
 */
public class MaterialTierGUI extends KitGUI {

    private static final int[] SLOTS = {11, 12, 13, 14, 15, 16};

    public MaterialTierGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » Elegir material", 3);
    }

    @Override
    protected void construir() {
        TipoArmaHerramienta tipo = sesion.getTipoTemporalArma();
        TipoArmaHerramienta.Tier[] tiers = TipoArmaHerramienta.Tier.values();
        for (int i = 0; i < tiers.length; i++) {
            var material = tipo.getMaterialParaTier(tiers[i]);
            inventory.setItem(SLOTS[i], new ItemBuilder(material)
                    .nombre("&f" + nombreTier(tiers[i]))
                    .lore(List.of("&7Click para elegir este material"))
                    .build());
        }
        inventory.setItem(SLOT_CANCELAR, new ItemBuilder(org.bukkit.Material.ARROW)
                .nombre("&e« Volver").build());
    }

    private String nombreTier(TipoArmaHerramienta.Tier tier) {
        return switch (tier) {
            case MADERA -> "Madera";
            case PIEDRA -> "Piedra";
            case HIERRO -> "Hierro";
            case ORO -> "Oro";
            case DIAMANTE -> "Diamante";
            case NETHERITE -> "Netherite";
        };
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();

        if (slot == SLOT_CANCELAR) {
            new WeaponsToolsGUI(sesion).abrir(jugador);
            return;
        }
        TipoArmaHerramienta.Tier[] tiers = TipoArmaHerramienta.Tier.values();
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] == slot) {
                TipoArmaHerramienta tipo = sesion.getTipoTemporalArma();
                var material = tipo.getMaterialParaTier(tiers[i]);
                sesion.getKit().getArmasHerramientas().add(new KitItemArma(tipo, material));
                new WeaponsToolsGUI(sesion).abrir(jugador);
                return;
            }
        }
    }
}
