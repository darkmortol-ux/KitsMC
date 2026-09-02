package com.darkmortol.kitspersonalizados.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Envoltorio sobre la Economy de Vault. Si Vault no está instalado (o no hay
 * ningún plugin de economía registrado), todas las operaciones fallan de
 * forma segura y se avisa al jugador/admin en vez de lanzar errores.
 */
public class EconomyManager {

    // No se cachea la instancia de Economy: el plugin de economía (Essentials,
    // CMI, etc.) puede registrarse en Vault después de que este plugin se
    // habilite, así que se busca de nuevo en cada operación.
    private Economy obtenerEconomia() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        return rsp != null ? rsp.getProvider() : null;
    }

    public boolean disponible() {
        return obtenerEconomia() != null;
    }

    public double balance(Player jugador) {
        Economy economia = obtenerEconomia();
        return economia != null ? economia.getBalance(jugador) : 0;
    }

    public boolean tieneFondos(Player jugador, double monto) {
        Economy economia = obtenerEconomia();
        return economia != null && economia.has(jugador, monto);
    }

    /**
     * Intenta retirar el monto de la cuenta del jugador. Devuelve true si se
     * pudo cobrar correctamente.
     */
    public boolean retirar(Player jugador, double monto) {
        Economy economia = obtenerEconomia();
        if (economia == null) return false;
        return economia.withdrawPlayer(jugador, monto).transactionSuccess();
    }

    public String formatear(double monto) {
        Economy economia = obtenerEconomia();
        if (economia != null) {
            return economia.format(monto);
        }
        return "$" + String.format("%,.2f", monto);
    }
}
