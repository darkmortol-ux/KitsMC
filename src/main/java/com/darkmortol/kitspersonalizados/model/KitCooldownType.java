package com.darkmortol.kitspersonalizados.model;

/**
 * Define cada cuánto se puede reclamar un kit.
 * El sufijo se usa para construir el permiso: kit.<nombre>.<sufijo>
 */
public enum KitCooldownType {

    SIN_HORARIO("Sin horario (siempre disponible)", null, 0L),
    UNA_HORA("Cada 1 hora", "1h", 60L * 60L * 1000L),
    UN_DIA("Cada 1 día", "1d", 24L * 60L * 60L * 1000L),
    UNA_SEMANA("Cada 1 semana", "1s", 7L * 24L * 60L * 60L * 1000L),
    UN_MES("Cada 1 mes", "1m", 30L * 24L * 60L * 60L * 1000L);

    private final String etiqueta;
    private final String sufijoPermiso;
    private final long milisegundos;

    KitCooldownType(String etiqueta, String sufijoPermiso, long milisegundos) {
        this.etiqueta = etiqueta;
        this.sufijoPermiso = sufijoPermiso;
        this.milisegundos = milisegundos;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Devuelve el sufijo del permiso (1h, 1d, 1s, 1m) o null si es SIN_HORARIO.
     */
    public String getSufijoPermiso() {
        return sufijoPermiso;
    }

    public long getMilisegundos() {
        return milisegundos;
    }

    public KitCooldownType siguiente() {
        KitCooldownType[] valores = values();
        return valores[(this.ordinal() + 1) % valores.length];
    }
}
