package com.darkmortol.kitspersonalizados.manager;

import com.darkmortol.kitspersonalizados.model.EfectoPersonalizado;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.util.Claves;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Construye los ItemStack reales que se entregan al jugador a partir de la
 * definición (plantilla) de un Kit.
 */
public class KitItemFactory {

    private final CustomEffectManager efectos;

    public KitItemFactory(CustomEffectManager efectos) {
        this.efectos = efectos;
    }

    public List<ItemStack> construir(Kit kit) {
        List<ItemStack> items = new ArrayList<>();

        // Armadura (pantalla 1 + encantamientos de pantalla 5 + efecto de pantalla 6)
        for (Kit.PiezaArmadura pieza : Kit.PiezaArmadura.values()) {
            Material material = kit.getMaterialArmadura().getPieza(pieza);
            if (material == null) continue;
            ItemBuilder builder = new ItemBuilder(material);
            for (Map.Entry<Enchantment, Integer> e : kit.getEncantamientosArmadura().get(pieza).entrySet()) {
                builder.encantamiento(e.getKey(), e.getValue());
            }
            builder.etiquetaPDC(Claves.KIT_NOMBRE, kit.getNombre());
            if (kit.getEfectoArmaduraId() != null) {
                EfectoPersonalizado efecto = efectos.obtener(kit.getEfectoArmaduraId());
                if (efecto != null) {
                    builder.etiquetaPDC(Claves.EFECTO_ID, efecto.getId());
                    builder.etiquetaPDC(Claves.ES_ARMADURA_SET, "1");
                    builder.nombre("&b" + material.name().replace("_", " ") + " &7[&d" + efecto.getNombre() + "&7]");
                    builder.agregarLinea("&7Efecto de set: &d" + efecto.getNombre());
                    for (String linea : efecto.getDescripcion()) {
                        builder.agregarLinea("&8" + linea);
                    }
                    builder.agregarLinea("&8Requiere las 4 piezas equipadas.");
                }
            }
            items.add(builder.build());
        }

        // Armas y herramientas (pantalla 2 + encantamientos de pantalla 5 + efecto de pantalla 6)
        for (KitItemArma arma : kit.getArmasHerramientas()) {
            ItemBuilder builder = new ItemBuilder(arma.getMaterial());
            for (Map.Entry<Enchantment, Integer> e : arma.getEncantamientos().entrySet()) {
                builder.encantamiento(e.getKey(), e.getValue());
            }
            builder.etiquetaPDC(Claves.KIT_NOMBRE, kit.getNombre());
            if (arma.getEfectoId() != null) {
                EfectoPersonalizado efecto = efectos.obtener(arma.getEfectoId());
                if (efecto != null) {
                    builder.etiquetaPDC(Claves.EFECTO_ID, efecto.getId());
                    builder.nombre("&b" + arma.getTipoBase().getNombre() + " &7[&d" + efecto.getNombre() + "&7]");
                    builder.agregarLinea("&7Efecto: &d" + efecto.getNombre());
                    for (String linea : efecto.getDescripcion()) {
                        builder.agregarLinea("&8" + linea);
                    }
                    builder.agregarLinea("&8Requiere estar en tu hotbar.");
                }
            }
            items.add(builder.build());
        }

        // Pociones (pantalla 3)
        for (Kit.PocionKit pocionKit : kit.getPociones()) {
            Material base = pocionKit.splash() ? Material.SPLASH_POTION : Material.POTION;
            ItemStack item = new ItemStack(base, 1);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setBasePotionType(pocionKit.tipo());
            meta.setDisplayName(MessageUtil.colorear("&f" + nombreLegiblePocion(pocionKit.tipo())));
            item.setItemMeta(meta);
            items.add(item);
        }

        // Comida (pantalla 4)
        for (Material comida : kit.getComida()) {
            items.add(new ItemStack(comida, 16));
        }

        // Ítems "varios" (pantalla 6, categoría VARIOS - se usan en la mano secundaria)
        for (String efectoId : kit.getEfectosVariosIds()) {
            EfectoPersonalizado efecto = efectos.obtener(efectoId);
            if (efecto == null) continue;
            ItemBuilder builder = new ItemBuilder(efecto.getMaterial());
            builder.nombre("&d&l" + efecto.getNombre());
            for (String linea : efecto.getDescripcion()) {
                builder.agregarLinea("&8" + linea);
            }
            builder.agregarLinea("&8Equípalo en tu mano secundaria.");
            builder.brillo(true);
            builder.etiquetaPDC(Claves.EFECTO_ID, efecto.getId());
            builder.etiquetaPDC(Claves.KIT_NOMBRE, kit.getNombre());
            items.add(builder.build());
        }

        return items;
    }

    private String nombreLegiblePocion(PotionType tipo) {
        String[] partes = tipo.name().split("_");
        StringBuilder sb = new StringBuilder("Poción de ");
        for (String parte : partes) {
            sb.append(parte.substring(0, 1)).append(parte.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
}
