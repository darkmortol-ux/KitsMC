package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.CategoriaEfecto;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.model.KitItemArma;
import com.darkmortol.kitspersonalizados.model.TipoArmaHerramienta;
import org.bukkit.entity.Player;

/**
 * Estado temporal (en memoria) del asistente de creación/edición de un kit
 * para un administrador en particular.
 */
public class KitCreationSession {

    private final KitsPersonalizados plugin;
    private final Player admin;
    private final Kit kit;
    private final boolean edicion;
    private int pasoActual = 1;

    // Estado temporal usado entre pantallas
    private TipoArmaHerramienta tipoTemporalArma;
    private Kit.PiezaArmadura piezaSeleccionadaEncantar;
    private KitItemArma armaSeleccionadaEncantar;
    private CategoriaEfecto categoriaEfectoActual;
    private KitItemArma armaSeleccionadaParaEfecto;

    public KitCreationSession(KitsPersonalizados plugin, Player admin, Kit kit, boolean edicion) {
        this.plugin = plugin;
        this.admin = admin;
        this.kit = kit;
        this.edicion = edicion;
    }

    public KitsPersonalizados getPlugin() {
        return plugin;
    }

    public Player getAdmin() {
        return admin;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isEdicion() {
        return edicion;
    }

    public int getPasoActual() {
        return pasoActual;
    }

    public void setPasoActual(int pasoActual) {
        this.pasoActual = pasoActual;
    }

    public TipoArmaHerramienta getTipoTemporalArma() {
        return tipoTemporalArma;
    }

    public void setTipoTemporalArma(TipoArmaHerramienta tipoTemporalArma) {
        this.tipoTemporalArma = tipoTemporalArma;
    }

    public Kit.PiezaArmadura getPiezaSeleccionadaEncantar() {
        return piezaSeleccionadaEncantar;
    }

    public void setPiezaSeleccionadaEncantar(Kit.PiezaArmadura piezaSeleccionadaEncantar) {
        this.piezaSeleccionadaEncantar = piezaSeleccionadaEncantar;
        this.armaSeleccionadaEncantar = null;
    }

    public KitItemArma getArmaSeleccionadaEncantar() {
        return armaSeleccionadaEncantar;
    }

    public void setArmaSeleccionadaEncantar(KitItemArma armaSeleccionadaEncantar) {
        this.armaSeleccionadaEncantar = armaSeleccionadaEncantar;
        this.piezaSeleccionadaEncantar = null;
    }

    public CategoriaEfecto getCategoriaEfectoActual() {
        return categoriaEfectoActual;
    }

    public void setCategoriaEfectoActual(CategoriaEfecto categoriaEfectoActual) {
        this.categoriaEfectoActual = categoriaEfectoActual;
    }

    public KitItemArma getArmaSeleccionadaParaEfecto() {
        return armaSeleccionadaParaEfecto;
    }

    public void setArmaSeleccionadaParaEfecto(KitItemArma armaSeleccionadaParaEfecto) {
        this.armaSeleccionadaParaEfecto = armaSeleccionadaParaEfecto;
    }
}
