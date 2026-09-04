package com.darkmortol.kitspersonalizados.command;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.gui.KitListaGUI;
import com.darkmortol.kitspersonalizados.model.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final KitsPersonalizados plugin;

    public KitCommand(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("lista")) {
            abrirLista(sender);
            return true;
        }
        if (args.length == 2) {
            entregarComoAdmin(sender, args[0], args[1]);
            return true;
        }
        if (args.length == 1) {
            reclamarParaSiMismo(sender, args[0]);
            return true;
        }
        plugin.getMensajes().enviar(sender, "uso-comando-kit");
        return true;
    }

    private void abrirLista(CommandSender sender) {
        if (!(sender instanceof Player jugador)) {
            sender.sendMessage("Este comando solo puede usarse en el juego.");
            return;
        }
        new KitListaGUI(plugin, jugador, 0).abrir();
    }

    private void entregarComoAdmin(CommandSender sender, String nombreKit, String nombreJugador) {
        if (!sender.hasPermission("kitspersonalizados.admin.dar")) {
            plugin.getMensajes().enviar(sender, "sin-permiso");
            return;
        }
        Kit kit = plugin.getKitManager().obtener(nombreKit);
        if (kit == null) {
            plugin.getMensajes().enviar(sender, "kit-no-existe", "%kit%", nombreKit);
            return;
        }
        Player destino = Bukkit.getPlayerExact(nombreJugador);
        if (destino == null) {
            plugin.getMensajes().enviar(sender, "jugador-no-encontrado", "%jugador%", nombreJugador);
            return;
        }
        plugin.getClaimService().entregarItems(destino, kit);
        plugin.getMensajes().enviar(sender, "kit-entregado-admin", "%kit%", kit.getNombre(), "%jugador%", destino.getName());
        if (!destino.equals(sender)) {
            plugin.getMensajes().enviar(destino, "kit-recibido-admin", "%kit%", kit.getNombre());
        }
    }

    private void reclamarParaSiMismo(CommandSender sender, String nombreKit) {
        if (!(sender instanceof Player jugador)) {
            sender.sendMessage("Este comando solo puede usarse en el juego (o agrega un jugador de destino).");
            return;
        }
        Kit kit = plugin.getKitManager().obtener(nombreKit);
        if (kit == null) {
            plugin.getMensajes().enviar(jugador, "kit-no-existe", "%kit%", nombreKit);
            return;
        }
        plugin.getClaimService().reclamar(jugador, kit);
    }
}
