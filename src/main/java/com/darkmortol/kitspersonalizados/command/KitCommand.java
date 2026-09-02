package com.darkmortol.kitspersonalizados.command;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.manager.CooldownManager;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitCooldownType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class KitCommand implements CommandExecutor {

    private final KitsPersonalizados plugin;

    public KitCommand(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        entregarItems(destino, kit);
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

        KitCooldownType cooldown = kit.getCooldown();
        String permisoReclamo = cooldown.getSufijoPermiso() == null
                ? "kit." + kit.getNombre().toLowerCase()
                : "kit." + kit.getNombre().toLowerCase() + "." + cooldown.getSufijoPermiso();

        if (jugador.hasPermission(permisoReclamo)) {
            if (cooldown != KitCooldownType.SIN_HORARIO) {
                long restante = plugin.getCooldownManager().milisegundosRestantes(jugador.getUniqueId(), kit.getNombre(), cooldown.getMilisegundos());
                if (restante > 0) {
                    plugin.getMensajes().enviar(jugador, "kit-en-cooldown", "%tiempo%", CooldownManager.formatear(restante));
                    return;
                }
                plugin.getCooldownManager().registrarReclamo(jugador.getUniqueId(), kit.getNombre());
            }
            entregarItems(jugador, kit);
            plugin.getMensajes().enviar(jugador, "kit-reclamado", "%kit%", kit.getNombre());
            return;
        }

        // No tiene el permiso de reclamo normal: revisamos si puede comprarlo.
        String permisoCompra = "kit." + kit.getNombre().toLowerCase() + ".buy";
        if (kit.esComprable() && jugador.hasPermission(permisoCompra)) {
            comprarKit(jugador, kit);
            return;
        }

        plugin.getMensajes().enviar(jugador, "kit-sin-permiso-reclamar", "%kit%", kit.getNombre());
    }

    private void comprarKit(Player jugador, Kit kit) {
        if (!plugin.getEconomia().disponible()) {
            plugin.getMensajes().enviar(jugador, "economia-no-disponible");
            return;
        }
        double precio = kit.getPrecio();
        if (!plugin.getEconomia().tieneFondos(jugador, precio)) {
            double faltante = precio - plugin.getEconomia().balance(jugador);
            plugin.getMensajes().enviar(jugador, "fondos-insuficientes", "%faltante%", plugin.getEconomia().formatear(faltante));
            return;
        }
        if (!plugin.getEconomia().retirar(jugador, precio)) {
            plugin.getMensajes().enviar(jugador, "economia-no-disponible");
            return;
        }
        entregarItems(jugador, kit);
        plugin.getMensajes().enviar(jugador, "kit-comprado", "%kit%", kit.getNombre(), "%precio%", plugin.getEconomia().formatear(precio));
    }

    private void entregarItems(Player jugador, Kit kit) {
        List<ItemStack> items = plugin.getItemFactory().construir(kit);
        for (ItemStack item : items) {
            Map<Integer, ItemStack> sobrantes = jugador.getInventory().addItem(item);
            for (ItemStack sobrante : sobrantes.values()) {
                jugador.getWorld().dropItemNaturally(jugador.getLocation(), sobrante);
            }
        }
    }
}
