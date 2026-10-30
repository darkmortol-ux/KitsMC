package com.darkmortol.kitspersonalizados.model;

import org.bukkit.Material;

public enum MaterialArmadura {

    CUERO("Cuero", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS),
    CADENA("Cota de malla", Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
    HIERRO("Hierro", Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
    ORO("Oro", Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
    DIAMANTE("Diamante", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
    NETHERITE("Netherite", Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    NINGUNA("Sin armadura", null, null, null, null);

    private final String nombre;
    private final Material casco, pechera, pantalon, botas;

    MaterialArmadura(String nombre, Material casco, Material pechera, Material pantalon, Material botas) {
        this.nombre = nombre;
        this.casco = casco;
        this.pechera = pechera;
        this.pantalon = pantalon;
        this.botas = botas;
    }

    public String getNombre() {
        return nombre;
    }

    public Material getCasco() {
        return casco;
    }

    public Material getPechera() {
        return pechera;
    }

    public Material getPantalon() {
        return pantalon;
    }

    public Material getBotas() {
        return botas;
    }

    public Material getPieza(Kit.PiezaArmadura pieza) {
        return switch (pieza) {
            case CASCO -> casco;
            case PECHERA -> pechera;
            case PANTALON -> pantalon;
            case BOTAS -> botas;
        };
    }

    /** Ícono representativo para mostrar en GUIs (usa la pechera, o lana si no hay armadura). */
    public Material getIcono() {
        return pechera != null ? pechera : Material.BARRIER;
    }
}
