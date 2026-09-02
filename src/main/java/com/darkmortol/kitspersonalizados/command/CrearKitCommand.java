package com.darkmortol.kitspersonalizados.command;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.gui.ArmorMaterialGUI;
import com.darkmortol.kitspersonalizados.gui.GUIListener;
import com.darkmortol.kitspersonalizados.gui.KitCreationSession;
import com.darkmortol.kitspersonalizados.model.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrearKitCommand implements CommandExecutor {

    private final KitsPersonalizados plugin;

    public CrearKitCommand(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player jugador)) {
            sender.sendMessage("Este comando solo puede usarse en el juego.");
            return true;
        }
        if (!jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return true;
        }
        boolean editando = label.equalsIgnoreCase("editarkit");
        if (args.length != 1) {
            plugin.getMensajes().enviar(jugador, editando ? "uso-comando-editarkit" : "uso-comando-crearkit");
            return true;
        }

        String nombre = args[0];
        Kit kit;
        if (editando) {
            kit = plugin.getKitManager().obtener(nombre);
            if (kit == null) {
                plugin.getMensajes().enviar(jugador, "kit-no-existe", "%kit%", nombre);
                return true;
            }
        } else {
            if (plugin.getKitManager().existe(nombre)) {
                plugin.getMensajes().enviar(jugador, "kit-ya-existe");
                return true;
            }
            kit = new Kit(nombre);
        }

        KitCreationSession sesion = new KitCreationSession(plugin, jugador, kit, editando);
        GUIListener.iniciarSesion(sesion);
        new ArmorMaterialGUI(sesion).abrir(jugador);
        return true;
    }
}
