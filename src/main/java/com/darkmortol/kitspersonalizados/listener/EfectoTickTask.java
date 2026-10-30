package com.darkmortol.kitspersonalizados.listener;

import com.darkmortol.kitspersonalizados.manager.CustomEffectManager;
import com.darkmortol.kitspersonalizados.model.EfectoPersonalizado;
import com.darkmortol.kitspersonalizados.util.Claves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Revisa periódicamente a cada jugador conectado y aplica los efectos
 * personalizados según lo que tenga equipado:
 * - ARMADURA: si tiene las 4 piezas del mismo efecto puestas.
 * - ARMAS / HERRAMIENTAS: si el ítem con el efecto está en la hotbar (slots 0-8).
 * - VARIOS: si el ítem con el efecto está en la mano secundaria.
 */
public class EfectoTickTask extends BukkitRunnable {

    private final CustomEffectManager efectos;
    private final int duracionTicks;

    public EfectoTickTask(CustomEffectManager efectos, int duracionTicks) {
        this.efectos = efectos;
        this.duracionTicks = duracionTicks;
    }

    @Override
    public void run() {
        for (Player jugador : Bukkit.getOnlinePlayers()) {
            Set<String> idsActivos = new HashSet<>();

            String idArmadura = revisarArmaduraCompleta(jugador);
            if (idArmadura != null) idsActivos.add(idArmadura);

            PlayerInventory inv = jugador.getInventory();
            for (int slot = 0; slot < 9; slot++) {
                String id = leerEfectoId(inv.getItem(slot));
                if (id != null) idsActivos.add(id);
            }

            String idOffhand = leerEfectoId(inv.getItemInOffHand());
            if (idOffhand != null) idsActivos.add(idOffhand);

            for (String id : idsActivos) {
                EfectoPersonalizado efecto = efectos.obtener(id);
                if (efecto == null) continue;
                for (EfectoPersonalizado.Componente componente : efecto.getComponentes()) {
                    jugador.addPotionEffect(new PotionEffect(componente.tipo(), duracionTicks, componente.amplificador(), true, false, true));
                }
            }
        }
    }

    private String revisarArmaduraCompleta(Player jugador) {
        ItemStack casco = jugador.getInventory().getHelmet();
        ItemStack pechera = jugador.getInventory().getChestplate();
        ItemStack pantalon = jugador.getInventory().getLeggings();
        ItemStack botas = jugador.getInventory().getBoots();
        if (casco == null || pechera == null || pantalon == null || botas == null) return null;

        String id1 = leerIdSetArmadura(casco);
        String id2 = leerIdSetArmadura(pechera);
        String id3 = leerIdSetArmadura(pantalon);
        String id4 = leerIdSetArmadura(botas);
        if (id1 == null || id2 == null || id3 == null || id4 == null) return null;
        if (id1.equals(id2) && id2.equals(id3) && id3.equals(id4)) {
            return id1;
        }
        return null;
    }

    private String leerIdSetArmadura(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        String esSet = pdc.get(Claves.ES_ARMADURA_SET, PersistentDataType.STRING);
        if (esSet == null) return null;
        return pdc.get(Claves.EFECTO_ID, PersistentDataType.STRING);
    }

    private String leerEfectoId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        // Evitamos que las piezas de armadura (categoría ARMADURA) se cuenten también
        // como si estuvieran en la hotbar/mano secundaria.
        if (pdc.has(Claves.ES_ARMADURA_SET, PersistentDataType.STRING)) return null;
        return pdc.get(Claves.EFECTO_ID, PersistentDataType.STRING);
    }
}
