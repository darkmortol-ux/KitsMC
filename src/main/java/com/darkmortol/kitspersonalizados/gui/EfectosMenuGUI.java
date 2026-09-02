package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.CategoriaEfecto;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.MaterialArmadura;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Pantalla 6/7: menú para elegir la categoría de efecto personalizado a configurar.
 */
public class EfectosMenuGUI extends KitGUI {

    private static final int SLOT_ARMADURA = 11;
    private static final int SLOT_ARMAS = 13;
    private static final int SLOT_HERRAMIENTAS = 15;
    private static final int SLOT_VARIOS = 21;

    public EfectosMenuGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 6/8 Efectos personalizados", 4);
    }

    @Override
    protected void construir() {
        Kit kit = sesion.getKit();

        boolean tieneArmadura = kit.getMaterialArmadura() != MaterialArmadura.NINGUNA;
        inventory.setItem(SLOT_ARMADURA, new ItemBuilder(tieneArmadura ? Material.DIAMOND_CHESTPLATE : Material.BARRIER)
                .nombre("&dArmadura")
                .lore(List.of(
                        tieneArmadura ? "&7Efecto actual: &f" + (kit.getEfectoArmaduraId() != null ? kit.getEfectoArmaduraId() : "Ninguno") : "&cEste kit no tiene armadura",
                        "&7Se activa con las 4 piezas equipadas"))
                .build());

        boolean tieneArmas = kit.getArmasHerramientas().stream().anyMatch(a -> a.esCategoria(TipoArmaHerramienta.Categoria.ARMA));
        inventory.setItem(SLOT_ARMAS, new ItemBuilder(tieneArmas ? Material.NETHERITE_SWORD : Material.BARRIER)
                .nombre("&dArmas")
                .lore(List.of(
                        tieneArmas ? "&7Elige un arma y asígnale un efecto" : "&cEste kit no tiene armas",
                        "&7Se activa si el arma está en tu hotbar"))
                .build());

        boolean tieneHerramientas = kit.getArmasHerramientas().stream().anyMatch(a -> a.esCategoria(TipoArmaHerramienta.Categoria.HERRAMIENTA));
        inventory.setItem(SLOT_HERRAMIENTAS, new ItemBuilder(tieneHerramientas ? Material.NETHERITE_PICKAXE : Material.BARRIER)
                .nombre("&dHerramientas")
                .lore(List.of(
                        tieneHerramientas ? "&7Elige una herramienta y asígnale un efecto" : "&cEste kit no tiene herramientas",
                        "&7Se activa si la herramienta está en tu hotbar"))
                .build());

        inventory.setItem(SLOT_VARIOS, new ItemBuilder(Material.NETHER_STAR)
                .nombre("&dVarios (talismanes)")
                .lore(List.of(
                        "&7Ítems: &f" + kit.getEfectosVariosIds().size(),
                        "&7Se activan en la mano secundaria",
                        "&7Click para elegir"))
                .build());

        ponerBarraNavegacion(true, "Siguiente »");
    }

    @Override
    public void onClick(InventoryClickEvent evento) {
        int slot = evento.getRawSlot();
        Player jugador = (Player) evento.getWhoClicked();
        Kit kit = sesion.getKit();

        if (slot == SLOT_CANCELAR) {
            GUIListener.cancelarSesion(jugador);
            return;
        }
        if (slot == SLOT_ATRAS) {
            sesion.setPasoActual(5);
            new EnchantSeccionesGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(7);
            new CooldownGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_ARMADURA) {
            if (kit.getMaterialArmadura() == MaterialArmadura.NINGUNA) return;
            new EfectoListaGUI(sesion, CategoriaEfecto.ARMADURA, null).abrir(jugador);
            return;
        }
        if (slot == SLOT_ARMAS) {
            if (kit.getArmasHerramientas().stream().noneMatch(a -> a.esCategoria(TipoArmaHerramienta.Categoria.ARMA))) return;
            new SeleccionItemEfectoGUI(sesion, TipoArmaHerramienta.Categoria.ARMA).abrir(jugador);
            return;
        }
        if (slot == SLOT_HERRAMIENTAS) {
            if (kit.getArmasHerramientas().stream().noneMatch(a -> a.esCategoria(TipoArmaHerramienta.Categoria.HERRAMIENTA))) return;
            new SeleccionItemEfectoGUI(sesion, TipoArmaHerramienta.Categoria.HERRAMIENTA).abrir(jugador);
            return;
        }
        if (slot == SLOT_VARIOS) {
            new EfectoListaGUI(sesion, CategoriaEfecto.VARIOS, null).abrir(jugador);
            return;
        }
    }
}
