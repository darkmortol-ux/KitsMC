package com.darkmortol.kitspersonalizados.command;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.gui.AdminOverviewGUI;
import com.darkmortol.kitspersonalizados.gui.AnvilNameGUI;
import com.darkmortol.kitspersonalizados.gui.ConfirmarBorradoGUI;
import com.darkmortol.kitspersonalizados.gui.GUIListener;
import com.darkmortol.kitspersonalizados.gui.KitCreationSession;
import com.darkmortol.kitspersonalizados.gui.KitListaGUI;
import com.darkmortol.kitspersonalizados.gui.KitSelectorGUI;
import com.darkmortol.kitspersonalizados.gui.KitStaffListaGUI;
import com.darkmortol.kitspersonalizados.gui.MenuPrincipalGUI;
import com.darkmortol.kitspersonalizados.gui.SeleccionarJugadorGUI;
import com.darkmortol.kitspersonalizados.gui.VisibilidadGUI;
import com.darkmortol.kitspersonalizados.model.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando general del plugin: /kitsp. Reemplaza a /crearkit, /editarkit,
 * /borrarkit, /listakits y /kit. Cada función abre su propia pantalla
 * visual; cuando se pasan argumentos de más (nombre de kit, jugador) se usan
 * como atajo para saltar directamente a la pantalla correspondiente.
 */
public class KitSpCommand implements CommandExecutor {

    private final KitsPersonalizados plugin;

    public KitSpCommand(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            abrirMenu(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "crear" -> crear(sender, args);
            case "editar" -> editar(sender, args);
            case "borrar" -> borrar(sender, args);
            case "listar" -> listar(sender);
            case "dar" -> dar(sender, args);
            case "lista" -> lista(sender);
            case "staff" -> staff(sender);
            default -> {
                if (args.length == 2) {
                    entregarComoAdmin(sender, args[0], args[1]);
                } else {
                    reclamarParaSiMismo(sender, args[0]);
                }
            }
        }
        return true;
    }

    private boolean requierePlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        sender.sendMessage("Este comando solo puede usarse en el juego.");
        return false;
    }

    private void abrirMenu(CommandSender sender) {
        if (!requierePlayer(sender)) return;
        new MenuPrincipalGUI(plugin, (Player) sender).abrir();
    }

    private void crear(CommandSender sender, String[] args) {
        if (!requierePlayer(sender)) return;
        Player jugador = (Player) sender;
        if (!jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return;
        }
        String prellenado = args.length >= 2 ? args[1] : "";
        new AnvilNameGUI(plugin, jugador, prellenado).abrir();
    }

    private void editar(CommandSender sender, String[] args) {
        if (!requierePlayer(sender)) return;
        Player jugador = (Player) sender;
        if (!jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return;
        }
        if (args.length >= 2) {
            Kit kit = plugin.getKitManager().obtener(args[1]);
            if (kit == null) {
                plugin.getMensajes().enviar(jugador, "kit-no-existe", "%kit%", args[1]);
                return;
            }
            KitCreationSession sesion = new KitCreationSession(plugin, jugador, kit, true);
            GUIListener.iniciarSesion(sesion);
            new VisibilidadGUI(sesion).abrir(jugador);
            return;
        }
        new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.EDITAR, 0).abrir();
    }

    private void borrar(CommandSender sender, String[] args) {
        if (!requierePlayer(sender)) return;
        Player jugador = (Player) sender;
        if (!jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return;
        }
        if (args.length >= 2) {
            Kit kit = plugin.getKitManager().obtener(args[1]);
            if (kit == null) {
                plugin.getMensajes().enviar(jugador, "kit-no-existe", "%kit%", args[1]);
                return;
            }
            new ConfirmarBorradoGUI(plugin, jugador, kit).abrir();
            return;
        }
        new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.BORRAR, 0).abrir();
    }

    private void listar(CommandSender sender) {
        if (!requierePlayer(sender)) return;
        Player jugador = (Player) sender;
        if (!jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return;
        }
        new AdminOverviewGUI(plugin, jugador, 0).abrir();
    }

    private void dar(CommandSender sender, String[] args) {
        // Con kit y jugador ya especificados no hace falta ninguna pantalla,
        // así que esta vía funciona también desde la consola (igual que antes).
        if (args.length >= 3) {
            entregarComoAdmin(sender, args[1], args[2]);
            return;
        }
        if (!requierePlayer(sender)) return;
        Player jugador = (Player) sender;
        if (!jugador.hasPermission("kitspersonalizados.admin.dar")) {
            plugin.getMensajes().enviar(jugador, "sin-permiso");
            return;
        }
        if (args.length == 2) {
            Kit kit = plugin.getKitManager().obtener(args[1]);
            if (kit == null) {
                plugin.getMensajes().enviar(jugador, "kit-no-existe", "%kit%", args[1]);
                return;
            }
            new SeleccionarJugadorGUI(plugin, jugador, kit, 0).abrir();
            return;
        }
        new KitSelectorGUI(plugin, jugador, KitSelectorGUI.Modo.DAR, 0).abrir();
    }

    private void lista(CommandSender sender) {
        if (!requierePlayer(sender)) return;
        new KitListaGUI(plugin, (Player) sender, 0).abrir();
    }

    private void staff(CommandSender sender) {
        if (!requierePlayer(sender)) return;
        new KitStaffListaGUI(plugin, (Player) sender, 0).abrir();
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
