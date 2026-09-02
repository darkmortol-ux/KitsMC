package com.darkmortol.kitspersonalizados.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Claves NamespacedKey usadas para etiquetar ítems con PersistentDataContainer.
 */
public class Claves {

    public static NamespacedKey EFECTO_ID;
    public static NamespacedKey KIT_NOMBRE;
    public static NamespacedKey ES_ARMADURA_SET;

    public static void inicializar(Plugin plugin) {
        EFECTO_ID = new NamespacedKey(plugin, "kp_efecto_id");
        KIT_NOMBRE = new NamespacedKey(plugin, "kp_kit_nombre");
        ES_ARMADURA_SET = new NamespacedKey(plugin, "kp_armadura_set");
    }
}
