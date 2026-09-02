package com.darkmortol.kitspersonalizados.manager;

import com.darkmortol.kitspersonalizados.model.CategoriaEfecto;
import com.darkmortol.kitspersonalizados.model.EfectoPersonalizado;
import com.darkmortol.kitspersonalizados.model.EfectoPersonalizado.Componente;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro central de los 64 efectos personalizados del plugin (16 por categoría).
 */
public class CustomEffectManager {

    private final Map<String, EfectoPersonalizado> efectosPorId = new LinkedHashMap<>();
    private final Map<CategoriaEfecto, List<EfectoPersonalizado>> efectosPorCategoria = new LinkedHashMap<>();

    public CustomEffectManager() {
        for (CategoriaEfecto categoria : CategoriaEfecto.values()) {
            efectosPorCategoria.put(categoria, new ArrayList<>());
        }
        registrarArmadura();
        registrarArmas();
        registrarHerramientas();
        registrarVarios();
    }

    private void registrar(String id, String nombre, CategoriaEfecto categoria, Material icono,
                            String descripcion, Componente... componentes) {
        EfectoPersonalizado efecto = new EfectoPersonalizado(id, nombre, categoria, icono,
                List.of(descripcion), List.of(componentes));
        efectosPorId.put(id, efecto);
        efectosPorCategoria.get(categoria).add(efecto);
    }

    private Componente c(PotionEffectType tipo, int amplificador) {
        return new Componente(tipo, amplificador);
    }

    private void registrarArmadura() {
        CategoriaEfecto cat = CategoriaEfecto.ARMADURA;
        registrar("arm_piel_roca", "Piel de Roca", cat, Material.IRON_CHESTPLATE,
                "Reduce el daño recibido mientras llevas el set completo.", c(PotionEffectType.RESISTANCE, 0));
        registrar("arm_corazon_titan", "Corazón de Titán", cat, Material.GOLDEN_CHESTPLATE,
                "Aumenta tu vida máxima con el set completo.", c(PotionEffectType.HEALTH_BOOST, 1));
        registrar("arm_paso_firme", "Paso Firme", cat, Material.LEATHER_BOOTS,
                "Caes lentamente y sin daño con el set completo.", c(PotionEffectType.SLOW_FALLING, 0));
        registrar("arm_vigor_guerrero", "Vigor de Guerrero", cat, Material.CHAINMAIL_CHESTPLATE,
                "Regeneras vida constantemente con el set completo.", c(PotionEffectType.REGENERATION, 0));
        registrar("arm_escudo_viviente", "Escudo Viviente", cat, Material.DIAMOND_CHESTPLATE,
                "Ganas corazones de absorción con el set completo.", c(PotionEffectType.ABSORPTION, 1));
        registrar("arm_sangre_fria", "Sangre Fría", cat, Material.NETHERITE_CHESTPLATE,
                "Eres inmune al fuego con el set completo.", c(PotionEffectType.FIRE_RESISTANCE, 0));
        registrar("arm_pulmon_acero", "Pulmón de Acero", cat, Material.TURTLE_HELMET,
                "Respiras bajo el agua con el set completo.", c(PotionEffectType.WATER_BREATHING, 0));
        registrar("arm_ojo_halcon", "Ojo de Halcón", cat, Material.GOLDEN_HELMET,
                "Ves en la oscuridad con el set completo.", c(PotionEffectType.NIGHT_VISION, 0));
        registrar("arm_furia_titan", "Furia del Titán", cat, Material.NETHERITE_LEGGINGS,
                "Aumenta tu fuerza de ataque con el set completo.", c(PotionEffectType.STRENGTH, 1));
        registrar("arm_paso_fantasma", "Paso del Fantasma", cat, Material.LEATHER_LEGGINGS,
                "Te mueves más rápido con el set completo.", c(PotionEffectType.SPEED, 1));
        registrar("arm_coraza_bendita", "Coraza Bendita", cat, Material.DIAMOND_LEGGINGS,
                "Reducción de daño mayor con el set completo.", c(PotionEffectType.RESISTANCE, 1));
        registrar("arm_aura_vida", "Aura de Vida", cat, Material.IRON_LEGGINGS,
                "Regeneración mejorada con el set completo.", c(PotionEffectType.REGENERATION, 1));
        registrar("arm_peso_pluma", "Peso Pluma", cat, Material.CHAINMAIL_BOOTS,
                "Saltas más alto con el set completo.", c(PotionEffectType.JUMP_BOOST, 1));
        registrar("arm_bendicion_abismo", "Bendición del Abismo", cat, Material.PRISMARINE_SHARD,
                "Efectos de guardián con el set completo.", c(PotionEffectType.CONDUIT_POWER, 0));
        registrar("arm_voluntad_hierro", "Voluntad de Hierro", cat, Material.IRON_BOOTS,
                "Resistencia y más vida con el set completo.", c(PotionEffectType.RESISTANCE, 0), c(PotionEffectType.HEALTH_BOOST, 0));
        registrar("arm_manto_campeon", "Manto del Campeón", cat, Material.NETHERITE_BOOTS,
                "Fuerza y velocidad con el set completo.", c(PotionEffectType.STRENGTH, 0), c(PotionEffectType.SPEED, 0));
    }

    private void registrarArmas() {
        CategoriaEfecto cat = CategoriaEfecto.ARMAS;
        registrar("arma_filo_berserker", "Filo del Berserker", cat, Material.NETHERITE_SWORD,
                "Gran aumento de fuerza mientras el arma está en tu hotbar.", c(PotionEffectType.STRENGTH, 2));
        registrar("arma_golpe_trueno", "Golpe del Trueno", cat, Material.DIAMOND_SWORD,
                "Aumenta tu velocidad mientras el arma está en tu hotbar.", c(PotionEffectType.SPEED, 2));
        registrar("arma_sed_sangre", "Sed de Sangre", cat, Material.IRON_SWORD,
                "Fuerza y velocidad combinadas mientras el arma está en tu hotbar.", c(PotionEffectType.STRENGTH, 1), c(PotionEffectType.SPEED, 1));
        registrar("arma_filo_vampirico", "Filo Vampírico", cat, Material.GOLDEN_SWORD,
                "Regeneras vida mientras el arma está en tu hotbar.", c(PotionEffectType.REGENERATION, 1));
        registrar("arma_corte_veloz", "Corte Veloz", cat, Material.WOODEN_SWORD,
                "Atacas más rápido mientras el arma está en tu hotbar.", c(PotionEffectType.HASTE, 1));
        registrar("arma_ira_caido", "Ira del Caído", cat, Material.NETHERITE_AXE,
                "Fuerza extrema mientras el arma está en tu hotbar.", c(PotionEffectType.STRENGTH, 3));
        registrar("arma_instinto_asesino", "Instinto Asesino", cat, Material.DIAMOND_AXE,
                "Velocidad y visión nocturna mientras el arma está en tu hotbar.", c(PotionEffectType.SPEED, 1), c(PotionEffectType.NIGHT_VISION, 0));
        registrar("arma_golpe_certero", "Golpe Certero", cat, Material.BOW,
                "Aumenta tu suerte mientras el arma está en tu hotbar.", c(PotionEffectType.LUCK, 1));
        registrar("arma_furia_descontrolada", "Furia Descontrolada", cat, Material.IRON_AXE,
                "Gran fuerza y velocidad mientras el arma está en tu hotbar.", c(PotionEffectType.STRENGTH, 2), c(PotionEffectType.SPEED, 1));
        registrar("arma_filo_espectral", "Filo Espectral", cat, Material.CROSSBOW,
                "Te vuelves invisible mientras el arma está en tu hotbar.", c(PotionEffectType.INVISIBILITY, 0));
        registrar("arma_guardian_filo", "Guardián del Filo", cat, Material.GOLDEN_AXE,
                "Resistencia al daño mientras el arma está en tu hotbar.", c(PotionEffectType.RESISTANCE, 1));
        registrar("arma_tajo_vacio", "Tajo del Vacío", cat, Material.TRIDENT,
                "Salto y fuerza mientras el arma está en tu hotbar.", c(PotionEffectType.JUMP_BOOST, 1), c(PotionEffectType.STRENGTH, 1));
        registrar("arma_danza_acero", "Danza de Acero", cat, Material.WOODEN_AXE,
                "Gran velocidad de movimiento mientras el arma está en tu hotbar.", c(PotionEffectType.SPEED, 2));
        registrar("arma_golpe_sismico", "Golpe Sísmico", cat, Material.STONE_SWORD,
                "Salto elevado mientras el arma está en tu hotbar.", c(PotionEffectType.JUMP_BOOST, 2));
        registrar("arma_furia_ancestral", "Furia Ancestral", cat, Material.STONE_AXE,
                "Fuerza y regeneración mientras el arma está en tu hotbar.", c(PotionEffectType.STRENGTH, 1), c(PotionEffectType.REGENERATION, 1));
        registrar("arma_voluntad_cazador", "Voluntad del Cazador", cat, Material.SPECTRAL_ARROW,
                "Ataca y recoge recursos más rápido mientras el arma está en tu hotbar.", c(PotionEffectType.HASTE, 2));
    }

    private void registrarHerramientas() {
        CategoriaEfecto cat = CategoriaEfecto.HERRAMIENTAS;
        registrar("herr_manos_minero", "Manos de Minero", cat, Material.NETHERITE_PICKAXE,
                "Minas más rápido mientras la herramienta está en tu hotbar.", c(PotionEffectType.HASTE, 2));
        registrar("herr_resistencia_obrero", "Resistencia del Obrero", cat, Material.IRON_PICKAXE,
                "Reduce el daño recibido mientras la herramienta está en tu hotbar.", c(PotionEffectType.RESISTANCE, 1));
        registrar("herr_instinto_explorador", "Instinto de Explorador", cat, Material.DIAMOND_PICKAXE,
                "Ves en la oscuridad mientras la herramienta está en tu hotbar.", c(PotionEffectType.NIGHT_VISION, 0));
        registrar("herr_vigor_laboral", "Vigor Laboral", cat, Material.GOLDEN_PICKAXE,
                "Regeneras vida mientras la herramienta está en tu hotbar.", c(PotionEffectType.REGENERATION, 0));
        registrar("herr_pulso_firme", "Pulso Firme", cat, Material.WOODEN_PICKAXE,
                "Minas rápido y resistes daño mientras la herramienta está en tu hotbar.", c(PotionEffectType.HASTE, 1), c(PotionEffectType.RESISTANCE, 0));
        registrar("herr_aliento_profundo", "Aliento Profundo", cat, Material.STONE_PICKAXE,
                "Respiras bajo el agua mientras la herramienta está en tu hotbar.", c(PotionEffectType.WATER_BREATHING, 0));
        registrar("herr_pie_ligero", "Pie Ligero", cat, Material.NETHERITE_SHOVEL,
                "Te mueves más rápido mientras la herramienta está en tu hotbar.", c(PotionEffectType.SPEED, 1));
        registrar("herr_fuerza_bruta", "Fuerza Bruta", cat, Material.DIAMOND_SHOVEL,
                "Aumenta tu fuerza mientras la herramienta está en tu hotbar.", c(PotionEffectType.STRENGTH, 1));
        registrar("herr_ojo_aguila", "Ojo de Águila", cat, Material.IRON_SHOVEL,
                "Visión nocturna y minado rápido mientras la herramienta está en tu hotbar.", c(PotionEffectType.NIGHT_VISION, 0), c(PotionEffectType.HASTE, 1));
        registrar("herr_toque_midas", "Toque de Midas", cat, Material.GOLDEN_SHOVEL,
                "Aumenta tu suerte mientras la herramienta está en tu hotbar.", c(PotionEffectType.LUCK, 1));
        registrar("herr_mano_hierro", "Mano de Hierro", cat, Material.NETHERITE_HOE,
                "Gran velocidad de minado mientras la herramienta está en tu hotbar.", c(PotionEffectType.HASTE, 3));
        registrar("herr_perseverancia", "Perseverancia", cat, Material.DIAMOND_HOE,
                "No pasas hambre mientras la herramienta está en tu hotbar.", c(PotionEffectType.SATURATION, 0));
        registrar("herr_paso_seguro", "Paso Seguro", cat, Material.IRON_HOE,
                "Saltas más alto mientras la herramienta está en tu hotbar.", c(PotionEffectType.JUMP_BOOST, 1));
        registrar("herr_sudor_trabajo", "Sudor del Trabajo", cat, Material.GOLDEN_HOE,
                "Minado y movimiento rápido mientras la herramienta está en tu hotbar.", c(PotionEffectType.HASTE, 1), c(PotionEffectType.SPEED, 1));
        registrar("herr_espiritu_minero", "Espíritu Minero", cat, Material.WOODEN_HOE,
                "Minado veloz y visión nocturna mientras la herramienta está en tu hotbar.", c(PotionEffectType.HASTE, 2), c(PotionEffectType.NIGHT_VISION, 0));
        registrar("herr_voluntad_granjero", "Voluntad del Granjero", cat, Material.SHEARS,
                "No pasas hambre y regeneras vida mientras la herramienta está en tu hotbar.", c(PotionEffectType.SATURATION, 0), c(PotionEffectType.REGENERATION, 0));
    }

    private void registrarVarios() {
        CategoriaEfecto cat = CategoriaEfecto.VARIOS;
        registrar("var_amuleto_fenix", "Amuleto de la Fénix", cat, Material.FEATHER,
                "Regeneración fuerte mientras el amuleto está en tu mano secundaria.", c(PotionEffectType.REGENERATION, 2));
        registrar("var_talisman_viento", "Talismán del Viento", cat, Material.PHANTOM_MEMBRANE,
                "Gran velocidad mientras el talismán está en tu mano secundaria.", c(PotionEffectType.SPEED, 2));
        registrar("var_reliquia_sombras", "Reliquia de las Sombras", cat, Material.WITHER_ROSE,
                "Te vuelves invisible mientras la reliquia está en tu mano secundaria.", c(PotionEffectType.INVISIBILITY, 0));
        registrar("var_piedra_titan", "Piedra del Titán", cat, Material.DIAMOND,
                "Aumenta tu vida máxima mientras la piedra está en tu mano secundaria.", c(PotionEffectType.HEALTH_BOOST, 2));
        registrar("var_fragmento_celestial", "Fragmento Celestial", cat, Material.AMETHYST_SHARD,
                "Gran resistencia al daño mientras el fragmento está en tu mano secundaria.", c(PotionEffectType.RESISTANCE, 2));
        registrar("var_amuleto_cazador", "Amuleto del Cazador", cat, Material.RABBIT_FOOT,
                "Visión nocturna y velocidad mientras el amuleto está en tu mano secundaria.", c(PotionEffectType.NIGHT_VISION, 0), c(PotionEffectType.SPEED, 1));
        registrar("var_corazon_dragon", "Corazón de Dragón", cat, Material.DRAGON_BREATH,
                "Inmunidad al fuego y fuerza mientras el corazón está en tu mano secundaria.", c(PotionEffectType.FIRE_RESISTANCE, 0), c(PotionEffectType.STRENGTH, 1));
        registrar("var_ojo_vacio", "Ojo del Vacío", cat, Material.ENDER_EYE,
                "Saltas muy alto mientras el ojo está en tu mano secundaria.", c(PotionEffectType.JUMP_BOOST, 2));
        registrar("var_esencia_vida", "Esencia de Vida", cat, Material.GHAST_TEAR,
                "No pasas hambre y regeneras vida mientras la esencia está en tu mano secundaria.", c(PotionEffectType.SATURATION, 0), c(PotionEffectType.REGENERATION, 1));
        registrar("var_talisman_suerte", "Talismán de la Suerte", cat, Material.EMERALD,
                "Gran aumento de suerte mientras el talismán está en tu mano secundaria.", c(PotionEffectType.LUCK, 2));
        registrar("var_anillo_nomada", "Anillo del Nómada", cat, Material.GOLD_NUGGET,
                "Velocidad y salto mientras el anillo está en tu mano secundaria.", c(PotionEffectType.SPEED, 1), c(PotionEffectType.JUMP_BOOST, 1));
        registrar("var_perla_abisal", "Perla Abisal", cat, Material.HEART_OF_THE_SEA,
                "Respiración acuática y poder de guardián mientras la perla está en tu mano secundaria.", c(PotionEffectType.WATER_BREATHING, 0), c(PotionEffectType.CONDUIT_POWER, 0));
        registrar("var_fragmento_estelar", "Fragmento Estelar", cat, Material.NETHER_STAR,
                "Brillas y resistes más daño mientras el fragmento está en tu mano secundaria.", c(PotionEffectType.GLOWING, 0), c(PotionEffectType.RESISTANCE, 1));
        registrar("var_amuleto_guardian", "Amuleto del Guardián", cat, Material.SHULKER_SHELL,
                "Gran absorción de daño mientras el amuleto está en tu mano secundaria.", c(PotionEffectType.ABSORPTION, 2));
        registrar("var_corazon_ardiente", "Corazón Ardiente", cat, Material.BLAZE_POWDER,
                "Inmunidad al fuego y más vida mientras el corazón está en tu mano secundaria.", c(PotionEffectType.FIRE_RESISTANCE, 0), c(PotionEffectType.HEALTH_BOOST, 1));
        registrar("var_reliquia_ancestral", "Reliquia Ancestral", cat, Material.NAUTILUS_SHELL,
                "Fuerza, resistencia y velocidad mientras la reliquia está en tu mano secundaria.", c(PotionEffectType.STRENGTH, 1), c(PotionEffectType.RESISTANCE, 1), c(PotionEffectType.SPEED, 1));
    }

    public EfectoPersonalizado obtener(String id) {
        return efectosPorId.get(id);
    }

    public List<EfectoPersonalizado> obtenerPorCategoria(CategoriaEfecto categoria) {
        return efectosPorCategoria.get(categoria);
    }

    public List<EfectoPersonalizado> obtenerTodos() {
        return new ArrayList<>(efectosPorId.values());
    }

    public int total() {
        return efectosPorId.size();
    }
}
