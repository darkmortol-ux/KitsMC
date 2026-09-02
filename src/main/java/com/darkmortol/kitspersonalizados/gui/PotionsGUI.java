package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla 3/7: elegir qué pociones tendrá el kit.
 */
public class PotionsGUI extends KitGUI {

    private static final PotionType[] TIPOS = {
            PotionType.STRENGTH, PotionType.LONG_STRENGTH, PotionType.STRONG_STRENGTH,
            PotionType.SWIFTNESS, PotionType.LONG_SWIFTNESS, PotionType.STRONG_SWIFTNESS,
            PotionType.HEALING, PotionType.STRONG_HEALING, PotionType.HARMING,
            PotionType.STRONG_HARMING, PotionType.POISON, PotionType.LONG_POISON,
            PotionType.REGENERATION, PotionType.LONG_REGENERATION, PotionType.STRONG_REGENERATION,
            PotionType.FIRE_RESISTANCE, PotionType.LONG_FIRE_RESISTANCE, PotionType.WATER_BREATHING,
            PotionType.LONG_WATER_BREATHING, PotionType.NIGHT_VISION, PotionType.INVISIBILITY
    };

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public PotionsGUI(KitCreationSession sesion) {
        super(sesion, "&8Kit » 3/8 Pociones", 6);
    }

    @Override
    protected void construir() {
        for (int i = 0; i < TIPOS.length; i++) {
            PotionType tipo = TIPOS[i];
            boolean incluida = sesion.getKit().getPociones().stream().anyMatch(p -> p.tipo() == tipo);
            inventory.setItem(SLOTS[i], construirIcono(tipo, incluida));
        }
        ponerBarraNavegacion(true, "Siguiente »");
    }

    private ItemStack construirIcono(PotionType tipo, boolean incluida) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionType(tipo);
        meta.setDisplayName(MessageUtil.colorear((incluida ? "&a✔ " : "&f") + nombreLegible(tipo)));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.colorear(incluida ? "&7Incluida en el kit" : "&7Click para agregar"));
        meta.setLore(lore);
        if (incluida) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    private String nombreLegible(PotionType tipo) {
        String[] partes = tipo.name().split("_");
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
            sesion.setPasoActual(2);
            new WeaponsToolsGUI(sesion).abrir(jugador);
            return;
        }
        if (slot == SLOT_SIGUIENTE) {
            sesion.setPasoActual(4);
            new FoodGUI(sesion).abrir(jugador);
            return;
        }
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i] != slot) continue;
            PotionType tipo = TIPOS[i];
            var existente = sesion.getKit().getPociones().stream().filter(p -> p.tipo() == tipo).findFirst();
            if (existente.isPresent()) {
                sesion.getKit().getPociones().remove(existente.get());
            } else {
                sesion.getKit().getPociones().add(new Kit.PocionKit(tipo, false));
            }
            refrescar();
            return;
        }
    }
}
