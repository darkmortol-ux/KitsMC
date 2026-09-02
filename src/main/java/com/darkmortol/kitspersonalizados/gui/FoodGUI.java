package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Pantalla 4/7: elegir qué comida tendrá el kit.
 */
public class FoodGUI extends KitGUI {

    private static final Material[] COMIDAS = {
            Material.BREAD, Material.APPLE, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
            Material.COOKED_BEEF, Material.COOKED_PORKCHOP, Material.COOKED_CHICKEN,
            Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.COOKED_COD,
            Material.COOKED_SALMON, Material.CARROT, Material.GOLDEN_CARROT, Material.POTATO,
            Material.BAKED_POTATO, Material.MELON_SLICE, Material.PUMPKIN_PIE, Material.CAKE,
            Material.COOKIE, Material.MUSHROOM_STEW, Material.RABBIT_STEW
    };

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public FoodGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 4/8 Comida", 6);
    }

    @Override
    protected void construir() {
        for (int i = 0; i < COMIDAS.length; i++) {
            Material material = COMIDAS[i];
            boolean incluida = sesion.getKit().getComida().contains(material);
            ItemBuilder builder = new ItemBuilder(material)
                    .nombre((incluida ? "&a✔ " : "&f") + nombreLegible(material))
                    .lore(List.of(incluida ? "&7Incluida en el kit (x16)" : "&7Click para agregar"));
            if (incluida) builder.brillo(true);
            inventory.setItem(SLOTS[i], builder.build());
        }
        ponerBarraNavegacion(true, "Siguiente »");
    }

    private String nombreLegible(Material material) {
        String[] partes = material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String parte : partes) {
            sb.append(parte.substring(0, 1)).append(parte.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
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
            sesion.setPasoActual(3);
            new PotionsGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(5);
            new EnchantSeccionesGUI(sesion).abrir(jugador);
            return;
        }
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] != slot) continue;
            Material material = COMIDAS[i];
            if (sesion.getKit().getComida().contains(material)) {
                sesion.getKit().getComida().remove(material);
            } else {
                sesion.getKit().getComida().add(material);
            }
            refrescar();
            return;
        }
    }
}
