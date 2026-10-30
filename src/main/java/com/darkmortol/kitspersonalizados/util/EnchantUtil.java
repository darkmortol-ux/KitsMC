package com.darkmortol.kitspersonalizados.util;

import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import org.bukkit.enchantments.Enchantment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnchantUtil {

    private static final Map<Enchantment, String> NOMBRES = new LinkedHashMap<>();
    static {
        NOMBRES.put(Enchantment.PROTECTION, "Protección");
        NOMBRES.put(Enchantment.FIRE_PROTECTION, "Protección contra el fuego");
        NOMBRES.put(Enchantment.BLAST_PROTECTION, "Protección contra explosiones");
        NOMBRES.put(Enchantment.PROJECTILE_PROTECTION, "Protección contra proyectiles");
        NOMBRES.put(Enchantment.THORNS, "Espinas");
        NOMBRES.put(Enchantment.FEATHER_FALLING, "Caída de pluma");
        NOMBRES.put(Enchantment.DEPTH_STRIDER, "Agilidad acuática");
        NOMBRES.put(Enchantment.FROST_WALKER, "Paso helado");
        NOMBRES.put(Enchantment.SOUL_SPEED, "Velocidad de las almas");
        NOMBRES.put(Enchantment.RESPIRATION, "Respiración");
        NOMBRES.put(Enchantment.AQUA_AFFINITY, "Afinidad acuática");
        NOMBRES.put(Enchantment.SHARPNESS, "Filo");
        NOMBRES.put(Enchantment.SMITE, "Aniquilación de no-muertos");
        NOMBRES.put(Enchantment.BANE_OF_ARTHROPODS, "Perdición de los artrópodos");
        NOMBRES.put(Enchantment.KNOCKBACK, "Empuje");
        NOMBRES.put(Enchantment.FIRE_ASPECT, "Aspecto ígneo");
        NOMBRES.put(Enchantment.LOOTING, "Botín");
        NOMBRES.put(Enchantment.SWEEPING_EDGE, "Filo arrollador");
        NOMBRES.put(Enchantment.POWER, "Poder");
        NOMBRES.put(Enchantment.PUNCH, "Empuje (arco)");
        NOMBRES.put(Enchantment.FLAME, "Llama");
        NOMBRES.put(Enchantment.INFINITY, "Infinidad");
        NOMBRES.put(Enchantment.MULTISHOT, "Multidisparo");
        NOMBRES.put(Enchantment.PIERCING, "Perforación");
        NOMBRES.put(Enchantment.QUICK_CHARGE, "Carga rápida");
        NOMBRES.put(Enchantment.LOYALTY, "Lealtad");
        NOMBRES.put(Enchantment.IMPALING, "Empalamiento");
        NOMBRES.put(Enchantment.RIPTIDE, "Propulsión acuática");
        NOMBRES.put(Enchantment.CHANNELING, "Canalización");
        NOMBRES.put(Enchantment.EFFICIENCY, "Eficiencia");
        NOMBRES.put(Enchantment.FORTUNE, "Fortuna");
        NOMBRES.put(Enchantment.SILK_TOUCH, "Toque de seda");
        NOMBRES.put(Enchantment.LUCK_OF_THE_SEA, "Suerte marina");
        NOMBRES.put(Enchantment.LURE, "Señuelo");
        NOMBRES.put(Enchantment.UNBREAKING, "Irrompibilidad");
        NOMBRES.put(Enchantment.MENDING, "Reparación");
        NOMBRES.put(Enchantment.VANISHING_CURSE, "Maldición de desaparición");
        NOMBRES.put(Enchantment.BINDING_CURSE, "Maldición de atadura");
    }

    private static final List<Enchantment> GENERALES = List.of(Enchantment.UNBREAKING, Enchantment.MENDING, Enchantment.VANISHING_CURSE);

    public static String nombre(Enchantment enchantment) {
        return NOMBRES.getOrDefault(enchantment, enchantment.getKey().getKey());
    }

    public static List<Enchantment> aplicablesArmadura(Kit.PiezaArmadura pieza) {
        return switch (pieza) {
            case CASCO -> unir(List.of(Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.BLAST_PROTECTION,
                    Enchantment.PROJECTILE_PROTECTION, Enchantment.THORNS, Enchantment.RESPIRATION, Enchantment.AQUA_AFFINITY,
                    Enchantment.BINDING_CURSE));
            case PECHERA -> unir(List.of(Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.BLAST_PROTECTION,
                    Enchantment.PROJECTILE_PROTECTION, Enchantment.THORNS, Enchantment.BINDING_CURSE));
            case PANTALON -> unir(List.of(Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.BLAST_PROTECTION,
                    Enchantment.PROJECTILE_PROTECTION, Enchantment.THORNS, Enchantment.BINDING_CURSE));
            case BOTAS -> unir(List.of(Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.BLAST_PROTECTION,
                    Enchantment.PROJECTILE_PROTECTION, Enchantment.THORNS, Enchantment.FEATHER_FALLING,
                    Enchantment.DEPTH_STRIDER, Enchantment.FROST_WALKER, Enchantment.SOUL_SPEED,
                    Enchantment.BINDING_CURSE));
        };
    }

    public static List<Enchantment> aplicablesArma(TipoArmaHerramienta tipo) {
        return switch (tipo) {
            case ESPADA -> unir(List.of(Enchantment.SHARPNESS, Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS,
                    Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOTING, Enchantment.SWEEPING_EDGE));
            case HACHA -> unir(List.of(Enchantment.SHARPNESS, Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS,
                    Enchantment.EFFICIENCY, Enchantment.FORTUNE, Enchantment.SILK_TOUCH));
            case PICO, PALA -> unir(List.of(Enchantment.EFFICIENCY, Enchantment.FORTUNE, Enchantment.SILK_TOUCH));
            case AZADA -> unir(List.of(Enchantment.EFFICIENCY));
            case TIJERAS -> unir(List.of(Enchantment.EFFICIENCY, Enchantment.SILK_TOUCH));
            case ARCO -> unir(List.of(Enchantment.POWER, Enchantment.PUNCH, Enchantment.FLAME, Enchantment.INFINITY));
            case BALLESTA -> unir(List.of(Enchantment.MULTISHOT, Enchantment.PIERCING, Enchantment.QUICK_CHARGE));
            case TRIDENTE -> unir(List.of(Enchantment.LOYALTY, Enchantment.IMPALING, Enchantment.RIPTIDE, Enchantment.CHANNELING));
        };
    }

    private static List<Enchantment> unir(List<Enchantment> especificos) {
        List<Enchantment> lista = new java.util.ArrayList<>(especificos);
        lista.addAll(GENERALES);
        return lista;
    }
}
