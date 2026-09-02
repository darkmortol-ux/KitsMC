package com.darkmortol.kitspersonalizados.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtil {

    private final FileConfiguration config;

    public MessageUtil(FileConfiguration config) {
        this.config = config;
    }

    public String obtener(String clave) {
        String texto = config.getString("mensajes." + clave, clave);
        return colorear(texto);
    }

    public static String colorear(String texto) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', texto);
    }

    public void enviar(CommandSender destino, String clave) {
        destino.sendMessage(obtener(clave));
    }

    public void enviar(CommandSender destino, String clave, String... reemplazos) {
        String texto = obtener(clave);
        for (int i = 0; i + 1 < reemplazos.length; i += 2) {
            texto = texto.replace(reemplazos[i], reemplazos[i + 1]);
        }
        destino.sendMessage(texto);
    }
}
