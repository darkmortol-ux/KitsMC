package com.darkmortol.kitspersonalizados;

import com.darkmortol.kitspersonalizados.command.CrearKitCommand;
import com.darkmortol.kitspersonalizados.command.KitAdminCommand;
import com.darkmortol.kitspersonalizados.command.KitCommand;
import com.darkmortol.kitspersonalizados.gui.GUIListener;
import com.darkmortol.kitspersonalizados.listener.EfectoTickTask;
import com.darkmortol.kitspersonalizados.manager.CooldownManager;
import com.darkmortol.kitspersonalizados.manager.CustomEffectManager;
import com.darkmortol.kitspersonalizados.manager.EconomyManager;
import com.darkmortol.kitspersonalizados.manager.KitClaimService;
import com.darkmortol.kitspersonalizados.manager.KitItemFactory;
import com.darkmortol.kitspersonalizados.manager.KitManager;
import com.darkmortol.kitspersonalizados.util.Claves;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitsPersonalizados extends JavaPlugin {

    private KitManager kitManager;
    private CooldownManager cooldownManager;
    private CustomEffectManager efectos;
    private KitItemFactory itemFactory;
    private MessageUtil mensajes;
    private EconomyManager economia;
    private KitClaimService claimService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Claves.inicializar(this);

        this.mensajes = new MessageUtil(getConfig());
        this.efectos = new CustomEffectManager();
        this.kitManager = new KitManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.itemFactory = new KitItemFactory(efectos);
        this.economia = new EconomyManager();
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault no está instalado: la compra de kits con economía estará desactivada.");
        }
        this.claimService = new KitClaimService(this);

        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("crearkit").setExecutor(new CrearKitCommand(this));
        getCommand("editarkit").setExecutor(new CrearKitCommand(this));
        getCommand("borrarkit").setExecutor(new KitAdminCommand(this));
        getCommand("listakits").setExecutor(new KitAdminCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));

        int intervalo = getConfig().getInt("intervalo-revision-efectos", 20);
        int duracion = getConfig().getInt("duracion-efecto-aplicado", 60);
        new EfectoTickTask(efectos, duracion).runTaskTimer(this, 20L, intervalo);

        getLogger().info("KitsPersonalizados habilitado. " + efectos.total() + " efectos personalizados registrados.");
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CustomEffectManager getEfectos() {
        return efectos;
    }

    public KitItemFactory getItemFactory() {
        return itemFactory;
    }

    public MessageUtil getMensajes() {
        return mensajes;
    }

    public EconomyManager getEconomia() {
        return economia;
    }

    public KitClaimService getClaimService() {
        return claimService;
    }
}
