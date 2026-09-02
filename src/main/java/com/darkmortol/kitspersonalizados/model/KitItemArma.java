package com.darkmortol.kitspersonalizados.model;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Un arma o herramienta configurada dentro de un kit.
 * tipoBase: la pieza base (ESPADA, HACHA, PICO, PALA, AZADA, ARCO, BALLESTA, TRIDENTE, TIJERAS)
 * material: el material Bukkit final ya resuelto (ej. DIAMOND_SWORD)
 */
public class KitItemArma {

    private final TipoArmaHerramienta tipoBase;
    private Material material;
    private final Map<Enchantment, Integer> encantamientos = new LinkedHashMap<>();
    private String efectoId; // null si no tiene efecto personalizado asignado

    public KitItemArma(TipoArmaHerramienta tipoBase, Material material) {
        this.tipoBase = tipoBase;
        this.material = material;
    }

    public TipoArmaHerramienta getTipoBase() {
        return tipoBase;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Map<Enchantment, Integer> getEncantamientos() {
        return encantamientos;
    }

    public String getEfectoId() {
        return efectoId;
    }

    public void setEfectoId(String efectoId) {
        this.efectoId = efectoId;
    }

    public boolean esCategoria(TipoArmaHerramienta.Categoria categoria) {
        return tipoBase.getCategoria() == categoria;
    }
}
