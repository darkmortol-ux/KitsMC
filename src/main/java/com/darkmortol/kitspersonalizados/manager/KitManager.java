package com.darkmortol.kitspersonalizados.manager;

import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitCooldownType;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.MaterialArmadura;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * Guarda y carga los kits en /plugins/KitsPersonalizados/kits/<nombre>.yml
 */
public class KitManager {

    private final Plugin plugin;
    private final File carpetaKits;
    private final Map<String, Kit> kits = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public KitManager(Plugin plugin) {
        this.plugin = plugin;
        this.carpetaKits = new File(plugin.getDataFolder(), "kits");
        if (!carpetaKits.exists()) {
            carpetaKits.mkdirs();
        }
        cargarTodos();
    }

    public void cargarTodos() {
        kits.clear();
        File[] archivos = carpetaKits.listFiles((dir, nombre) -> nombre.endsWith(".yml"));
        if (archivos == null) return;
        for (File archivo : archivos) {
            try {
                Kit kit = cargarDesdeArchivo(archivo);
                if (kit != null) {
                    kits.put(kit.getNombre().toLowerCase(Locale.ROOT), kit);
                }
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "No se pudo cargar el kit desde " + archivo.getName(), ex);
            }
        }
    }

    private Kit cargarDesdeArchivo(File archivo) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(archivo);
        String nombre = yml.getString("nombre");
        if (nombre == null) return null;
        Kit kit = new Kit(nombre);

        kit.setMaterialArmadura(MaterialArmadura.valueOf(yml.getString("material-armadura", "CUERO")));

        ConfigurationSection encArm = yml.getConfigurationSection("encantamientos-armadura");
        if (encArm != null) {
            for (Kit.PiezaArmadura pieza : Kit.PiezaArmadura.values()) {
                ConfigurationSection sec = encArm.getConfigurationSection(pieza.name());
                if (sec == null) continue;
                Map<Enchantment, Integer> mapa = kit.getEncantamientosArmadura().get(pieza);
                for (String claveEnch : sec.getKeys(false)) {
                    Enchantment ench = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(claveEnch));
                    if (ench != null) {
                        mapa.put(ench, sec.getInt(claveEnch));
                    }
                }
            }
        }

        kit.setEfectoArmaduraId(yml.getString("efecto-armadura", null));
        kit.getEfectosVariosIds().addAll(yml.getStringList("efectos-varios"));
        kit.setCooldown(KitCooldownType.valueOf(yml.getString("cooldown", "SIN_HORARIO")));
        kit.setPrecio(yml.getDouble("precio", 0.0));

        for (Map<?, ?> mapaCrudo : yml.getMapList("armas-herramientas")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) mapaCrudo;
            TipoArmaHerramienta tipo = TipoArmaHerramienta.valueOf((String) m.get("tipo"));
            Material material = Material.valueOf((String) m.get("material"));
            KitItemArma item = new KitItemArma(tipo, material);
            item.setEfectoId((String) m.get("efecto"));
            @SuppressWarnings("unchecked")
            Map<String, Object> ench = (Map<String, Object>) m.get("encantamientos");
            if (ench != null) {
                for (Map.Entry<String, Object> e : ench.entrySet()) {
                    Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(e.getKey()));
                    if (enchantment != null) {
                        item.getEncantamientos().put(enchantment, (Integer) e.getValue());
                    }
                }
            }
            kit.getArmasHerramientas().add(item);
        }

        for (Map<?, ?> mapaCrudo : yml.getMapList("pociones")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) mapaCrudo;
            PotionType tipo = PotionType.valueOf((String) m.get("tipo"));
            boolean splash = Boolean.TRUE.equals(m.get("splash"));
            kit.getPociones().add(new Kit.PocionKit(tipo, splash));
        }

        for (String materialStr : yml.getStringList("comida")) {
            Material m = Material.matchMaterial(materialStr);
            if (m != null) kit.getComida().add(m);
        }

        return kit;
    }

    public void guardar(Kit kit) {
        kits.put(kit.getNombre().toLowerCase(Locale.ROOT), kit);
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("nombre", kit.getNombre());
        yml.set("material-armadura", kit.getMaterialArmadura().name());
        yml.set("efecto-armadura", kit.getEfectoArmaduraId());
        yml.set("efectos-varios", kit.getEfectosVariosIds());
        yml.set("cooldown", kit.getCooldown().name());
        yml.set("precio", kit.getPrecio());

        for (Kit.PiezaArmadura pieza : Kit.PiezaArmadura.values()) {
            Map<Enchantment, Integer> mapa = kit.getEncantamientosArmadura().get(pieza);
            for (Map.Entry<Enchantment, Integer> e : mapa.entrySet()) {
                yml.set("encantamientos-armadura." + pieza.name() + "." + e.getKey().getKey().getKey(), e.getValue());
            }
        }

        java.util.List<Map<String, Object>> listaArmas = new java.util.ArrayList<>();
        for (KitItemArma item : kit.getArmasHerramientas()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", item.getTipoBase().name());
            m.put("material", item.getMaterial().name());
            m.put("efecto", item.getEfectoId());
            Map<String, Object> ench = new LinkedHashMap<>();
            for (Map.Entry<Enchantment, Integer> e : item.getEncantamientos().entrySet()) {
                ench.put(e.getKey().getKey().getKey(), e.getValue());
            }
            m.put("encantamientos", ench);
            listaArmas.add(m);
        }
        yml.set("armas-herramientas", listaArmas);

        java.util.List<Map<String, Object>> listaPociones = new java.util.ArrayList<>();
        for (Kit.PocionKit p : kit.getPociones()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", p.tipo().name());
            m.put("splash", p.splash());
            listaPociones.add(m);
        }
        yml.set("pociones", listaPociones);

        java.util.List<String> comida = new java.util.ArrayList<>();
        for (Material m : kit.getComida()) comida.add(m.name());
        yml.set("comida", comida);

        try {
            String contenido = construirEncabezadoPermisos(kit) + yml.saveToString();
            File archivo = new File(carpetaKits, kit.getNombre().toLowerCase(Locale.ROOT) + ".yml");
            try (java.io.Writer writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(archivo), java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(contenido);
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "No se pudo guardar el kit " + kit.getNombre(), ex);
        }
    }

    /**
     * Genera un bloque de comentarios (ignorado por el parser YAML) con los
     * permisos exactos que hay que cargar en el gestor de permisos/rangos
     * (LuckPerms, PermissionsEx, etc.) para que los jugadores puedan reclamar
     * o comprar este kit.
     */
    private String construirEncabezadoPermisos(Kit kit) {
        String nombre = kit.getNombre().toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        sb.append("# ================================================================\n");
        sb.append("# Permisos de este kit — cárgalos en tu gestor de permisos/rangos\n");
        sb.append("# ================================================================\n");
        if (kit.getCooldown() == KitCooldownType.SIN_HORARIO) {
            sb.append("# Reclamo (sin horario): kit.").append(nombre).append("\n");
        } else {
            sb.append("# Reclamo (").append(kit.getCooldown().getEtiqueta()).append("): kit.")
                    .append(nombre).append(".").append(kit.getCooldown().getSufijoPermiso()).append("\n");
        }
        if (kit.esComprable()) {
            sb.append("# Compra (precio $").append(String.format(java.util.Locale.US, "%,.2f", kit.getPrecio()))
                    .append("): kit.").append(nombre).append(".buy\n");
        } else {
            sb.append("# Compra: no disponible (precio en $0)\n");
        }
        sb.append("# Entrega por admin (sin cooldown, cualquier kit): kitspersonalizados.admin.dar\n");
        sb.append("# ================================================================\n");
        return sb.toString();
    }

    public boolean existe(String nombre) {
        return kits.containsKey(nombre.toLowerCase(Locale.ROOT));
    }

    public Kit obtener(String nombre) {
        return kits.get(nombre.toLowerCase(Locale.ROOT));
    }

    public boolean borrar(String nombre) {
        Kit kit = kits.remove(nombre.toLowerCase(Locale.ROOT));
        if (kit == null) return false;
        File archivo = new File(carpetaKits, nombre.toLowerCase(Locale.ROOT) + ".yml");
        if (archivo.exists()) archivo.delete();
        return true;
    }

    public Map<String, Kit> getKits() {
        return kits;
    }
}
