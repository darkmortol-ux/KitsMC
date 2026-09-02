package com.darkmortol.kitspersonalizados.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack base) {
        this.item = base.clone();
        this.meta = item.getItemMeta();
    }

    public ItemBuilder nombre(String nombre) {
        meta.setDisplayName(MessageUtil.colorear(nombre));
        return this;
    }

    public ItemBuilder lore(List<String> lineas) {
        List<String> coloreadas = new ArrayList<>();
        for (String linea : lineas) {
            coloreadas.add(MessageUtil.colorear(linea));
        }
        meta.setLore(coloreadas);
        return this;
    }

    public ItemBuilder agregarLinea(String linea) {
        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        lore.add(MessageUtil.colorear(linea));
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder cantidad(int cantidad) {
        item.setAmount(Math.max(1, cantidad));
        return this;
    }

    public ItemBuilder brillo(boolean brilla) {
        if (brilla) {
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder encantamiento(Enchantment enchantment, int nivel) {
        meta.addEnchant(enchantment, nivel, true);
        return this;
    }

    public ItemBuilder ocultarAtributos() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder etiquetaPDC(NamespacedKey clave, String valor) {
        meta.getPersistentDataContainer().set(clave, PersistentDataType.STRING, valor);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
