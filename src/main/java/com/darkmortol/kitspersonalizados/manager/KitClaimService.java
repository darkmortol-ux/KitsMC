package com.darkmortol.kitspersonalizados.manager;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitCooldownType;
import com.darkmortol.kitspersonalizados.model.VisibilidadKit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Centraliza la lógica de "un jugador reclama/compra un kit para sí mismo",
 * para no duplicarla entre el comando /kit y las pantallas /kit lista y
 * /kit staff.
 */
public class KitClaimService {

    private final KitsPersonalizados plugin;

    public KitClaimService(KitsPersonalizados plugin) {
        this.plugin = plugin;
    }

    /** Permiso normal (por cooldown) requerido para reclamar este kit (solo kits NORMAL). */
    public String permisoReclamo(Kit kit) {
        KitCooldownType cooldown = kit.getCooldown();
        return cooldown.getSufijoPermiso() == null
                ? "kit." + kit.getNombre().toLowerCase()
                : "kit." + kit.getNombre().toLowerCase() + "." + cooldown.getSufijoPermiso();
    }

    /** Permiso de compra (independiente del cooldown) para este kit (solo kits NORMAL). */
    public String permisoCompra(Kit kit) {
        return "kit." + kit.getNombre().toLowerCase() + ".buy";
    }

    /** Permiso único para reclamar un kit de STAFF, sin cooldown ni precio. */
    public String permisoStaff(Kit kit) {
        return "kit." + kit.getNombre().toLowerCase() + ".staff";
    }

    /**
     * True si el jugador puede ver/usar este kit NORMAL (para /kit lista):
     * reclamo normal, compra, o si es admin (para poder revisar la configuración).
     * Los kits de STAFF nunca aparecen acá — usan tieneAccesoStaff().
     */
    public boolean tieneAcceso(Player jugador, Kit kit) {
        if (kit.getVisibilidad() != VisibilidadKit.NORMAL) return false;
        if (jugador.hasPermission("kitspersonalizados.admin")) return true;
        if (jugador.hasPermission(permisoReclamo(kit))) return true;
        return kit.esComprable() && jugador.hasPermission(permisoCompra(kit));
    }

    /**
     * True si el jugador puede ver/reclamar este kit de STAFF (para /kit staff).
     * Los kits NORMAL nunca aparecen acá — usan tieneAcceso().
     */
    public boolean tieneAccesoStaff(Player jugador, Kit kit) {
        if (kit.getVisibilidad() != VisibilidadKit.STAFF) return false;
        if (jugador.hasPermission("kitspersonalizados.admin")) return true;
        return jugador.hasPermission(permisoStaff(kit));
    }

    /**
     * Intenta reclamar (o, si no tiene el permiso normal, comprar) el kit
     * para el jugador indicado. Envía todos los mensajes correspondientes.
     * Los kits de STAFF se reclaman directo con un único permiso, sin
     * cooldown ni economía de por medio.
     */
    public void reclamar(Player jugador, Kit kit) {
        if (kit.esStaff()) {
            reclamarStaff(jugador, kit);
            return;
        }

        KitCooldownType cooldown = kit.getCooldown();
        String permisoReclamo = permisoReclamo(kit);

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

        if (kit.esComprable() && jugador.hasPermission(permisoCompra(kit))) {
            comprar(jugador, kit);
            return;
        }

        plugin.getMensajes().enviar(jugador, "kit-sin-permiso-reclamar", "%kit%", kit.getNombre());
    }

    private void reclamarStaff(Player jugador, Kit kit) {
        if (!jugador.hasPermission(permisoStaff(kit)) && !jugador.hasPermission("kitspersonalizados.admin")) {
            plugin.getMensajes().enviar(jugador, "kit-sin-permiso-staff", "%kit%", kit.getNombre());
            return;
        }
        entregarItems(jugador, kit);
        plugin.getMensajes().enviar(jugador, "kit-reclamado-staff", "%kit%", kit.getNombre());
    }

    private void comprar(Player jugador, Kit kit) {
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

    public void entregarItems(Player jugador, Kit kit) {
        List<ItemStack> items = plugin.getItemFactory().construir(kit);
        for (ItemStack item : items) {
            Map<Integer, ItemStack> sobrantes = jugador.getInventory().addItem(item);
            for (ItemStack sobrante : sobrantes.values()) {
                jugador.getWorld().dropItemNaturally(jugador.getLocation(), sobrante);
            }
        }
    }
}

