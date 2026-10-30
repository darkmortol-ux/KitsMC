package com.darkmortol.kitspersonalizados.model;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

public enum TipoArmaHerramienta {

    ESPADA("Espada", Categoria.ARMA, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    HACHA("Hacha", Categoria.HERRAMIENTA, Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),
    PICO("Pico", Categoria.HERRAMIENTA, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE),
    PALA("Pala", Categoria.HERRAMIENTA, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL),
    AZADA("Azada", Categoria.HERRAMIENTA, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE),
    ARCO("Arco", Categoria.ARMA, null, null, null, null, null, null),
    BALLESTA("Ballesta", Categoria.ARMA, null, null, null, null, null, null),
    TRIDENTE("Tridente", Categoria.ARMA, null, null, null, null, null, null),
    TIJERAS("Tijeras", Categoria.HERRAMIENTA, null, Material.SHEARS, Material.SHEARS, Material.SHEARS, Material.SHEARS, Material.SHEARS);

    public enum Categoria { ARMA, HERRAMIENTA }
    public enum Tier { MADERA, PIEDRA, HIERRO, ORO, DIAMANTE, NETHERITE }

    private final String nombre;
    private final Categoria categoria;
    private final Map<Tier, Material> materialesPorTier = new EnumMap<>(Tier.class);

    TipoArmaHerramienta(String nombre, Categoria categoria, Material madera, Material piedra, Material hierro,
                         Material oro, Material diamante, Material netherite) {
        this.nombre = nombre;
        this.categoria = categoria;
        materialesPorTier.put(Tier.MADERA, madera);
        materialesPorTier.put(Tier.PIEDRA, piedra);
        materialesPorTier.put(Tier.HIERRO, hierro);
        materialesPorTier.put(Tier.ORO, oro);
        materialesPorTier.put(Tier.DIAMANTE, diamante);
        materialesPorTier.put(Tier.NETHERITE, netherite);
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Devuelve el material para ese tier, o el único material fijo (arco/ballesta/tridente) si el tipo no tiene tiers.
     */
    public Material getMaterialParaTier(Tier tier) {
        if (this == ARCO) return Material.BOW;
        if (this == BALLESTA) return Material.CROSSBOW;
        if (this == TRIDENTE) return Material.TRIDENT;
        Material m = materialesPorTier.get(tier);
        return m != null ? m : materialesPorTier.get(Tier.HIERRO);
    }

    public boolean tieneTiers() {
        return this != ARCO && this != BALLESTA && this != TRIDENTE && this != TIJERAS;
    }
}
