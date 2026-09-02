package com.darkmortol.kitspersonalizados.model;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    public enum PiezaArmadura { CASCO, PECHERA, PANTALON, BOTAS }

    public record PocionKit(PotionType tipo, boolean splash) {}

    private String nombre;

    // Pantalla 1
    private MaterialArmadura materialArmadura = MaterialArmadura.CUERO;

    // Pantalla 2
    private final List<KitItemArma> armasHerramientas = new ArrayList<>();

    // Pantalla 3
    private final List<PocionKit> pociones = new ArrayList<>();

    // Pantalla 4
    private final List<Material> comida = new ArrayList<>();

    // Pantalla 5
    private final Map<PiezaArmadura, Map<Enchantment, Integer>> encantamientosArmadura = new LinkedHashMap<>();

    // Pantalla 6
    private String efectoArmaduraId; // un solo efecto para el set completo (categoría ARMADURA)
    private final List<String> efectosVariosIds = new ArrayList<>(); // ítems talismán extra (categoría VARIOS)

    // Pantalla 7
    private KitCooldownType cooldown = KitCooldownType.SIN_HORARIO;

    // Pantalla 8
    private double precio = 0.0; // 0 = no se puede comprar

    public Kit(String nombre) {
        this.nombre = nombre;
        for (PiezaArmadura p : PiezaArmadura.values()) {
            encantamientosArmadura.put(p, new LinkedHashMap<>());
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public MaterialArmadura getMaterialArmadura() {
        return materialArmadura;
    }

    public void setMaterialArmadura(MaterialArmadura materialArmadura) {
        this.materialArmadura = materialArmadura;
    }

    public List<KitItemArma> getArmasHerramientas() {
        return armasHerramientas;
    }

    public List<PocionKit> getPociones() {
        return pociones;
    }

    public List<Material> getComida() {
        return comida;
    }

    public Map<PiezaArmadura, Map<Enchantment, Integer>> getEncantamientosArmadura() {
        return encantamientosArmadura;
    }

    public String getEfectoArmaduraId() {
        return efectoArmaduraId;
    }

    public void setEfectoArmaduraId(String efectoArmaduraId) {
        this.efectoArmaduraId = efectoArmaduraId;
    }

    public List<String> getEfectosVariosIds() {
        return efectosVariosIds;
    }

    public KitCooldownType getCooldown() {
        return cooldown;
    }

    public void setCooldown(KitCooldownType cooldown) {
        this.cooldown = cooldown;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = Math.max(0, precio);
    }

    public boolean esComprable() {
        return precio > 0;
    }

    public EquipmentSlot slotDe(PiezaArmadura pieza) {
        return switch (pieza) {
            case CASCO -> EquipmentSlot.HEAD;
            case PECHERA -> EquipmentSlot.CHEST;
            case PANTALON -> EquipmentSlot.LEGS;
            case BOTAS -> EquipmentSlot.FEET;
        };
    }
}
