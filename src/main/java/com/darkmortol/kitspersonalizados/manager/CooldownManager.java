package com.darkmortol.kitspersonalizados.manager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Guarda la última vez que cada jugador reclamó cada kit, en
 * /plugins/KitsPersonalizados/playerdata/<uuid>.yml
 */
public class CooldownManager {

    private final Plugin plugin;
    private final File carpetaDatos;
    private final Map<UUID, Map<String, Long>> cache = new HashMap<>();

    public CooldownManager(Plugin plugin) {
        this.plugin = plugin;
        this.carpetaDatos = new File(plugin.getDataFolder(), "playerdata");
        if (!carpetaDatos.exists()) {
            carpetaDatos.mkdirs();
        }
    }

    private File archivoDe(UUID uuid) {
        return new File(carpetaDatos, uuid.toString() + ".yml");
    }

    private Map<String, Long> datosDe(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> {
            Map<String, Long> mapa = new HashMap<>();
            File archivo = archivoDe(id);
            if (archivo.exists()) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(archivo);
                for (String clave : yml.getKeys(false)) {
                    mapa.put(clave.toLowerCase(), yml.getLong(clave));
                }
            }
            return mapa;
        });
    }

    /**
     * Devuelve los milisegundos restantes de cooldown, o 0 si ya puede reclamarlo.
     */
    public long milisegundosRestantes(UUID uuid, String kitNombre, long cooldownMs) {
        if (cooldownMs <= 0) return 0;
        Long ultimo = datosDe(uuid).get(kitNombre.toLowerCase());
        if (ultimo == null) return 0;
        long transcurrido = System.currentTimeMillis() - ultimo;
        long restante = cooldownMs - transcurrido;
        return Math.max(0, restante);
    }

    public void registrarReclamo(UUID uuid, String kitNombre) {
        datosDe(uuid).put(kitNombre.toLowerCase(), System.currentTimeMillis());
        guardar(uuid);
    }

    private void guardar(UUID uuid) {
        YamlConfiguration yml = new YamlConfiguration();
        for (Map.Entry<String, Long> e : datosDe(uuid).entrySet()) {
            yml.set(e.getKey(), e.getValue());
        }
        try {
            yml.save(archivoDe(uuid));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "No se pudo guardar el cooldown de " + uuid, ex);
        }
    }

    public static String formatear(long ms) {
        long segundosTotales = ms / 1000;
        long dias = segundosTotales / 86400;
        long horas = (segundosTotales % 86400) / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;

        if (dias > 0) return dias + "d " + horas + "h";
        if (horas > 0) return horas + "h " + minutos + "m";
        if (minutos > 0) return minutos + "m " + segundos + "s";
        return segundos + "s";
    }
}
