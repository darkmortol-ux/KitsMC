package com.darkmortol.kitspersonalizados.command;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KitAdminCommand implements CommandExecutor {

    private final KitsPersonalizados plugin;

    public KitAdminCommand(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(sender, "sin-permiso");
            return true;
        }

        if (label.equalsIgnoreCase("listakits")) {
            plugin.getMensajes().enviar(sender, "lista-kits-titulo");
            if (plugin.getKitManager().getKits().isEmpty()) {
                plugin.getMensajes().enviar(sender, "no-hay-kits");
                return true;
            }
            for (Kit kit : plugin.getKitManager().getKits().values()) {
                String precio = kit.esComprable() ? ", comprable por " + plugin.getEconomia().formatear(kit.getPrecio()) : "";
                sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + kit.getNombre()
                        + ChatColor.GRAY + " (" + kit.getCooldown().getEtiqueta() + precio + ")");
            }
            return true;
        }

        if (label.equalsIgnoreCase("borrarkit")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Uso: /borrarkit <nombre>");
                return true;
            }
            if (!plugin.getKitManager().existe(args[0])) {
                plugin.getMensajes().enviar(sender, "kit-no-existe", "%kit%", args[0]);
                return true;
            }
            plugin.getKitManager().borrar(args[0]);
            plugin.getMensajes().enviar(sender, "kit-borrado", "%kit%", args[0]);
            return true;
        }

        return true;
    }
}
