package com.darkmortol.kitspersonalizados.model;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Representa uno de los 64 efectos personalizados fijos del plugin.
 * id: identificador único usado como PDC tag en los ítems (ej. "arm_furia_titan")
 * material: ícono representativo en los GUIs, y en el caso de VARIOS, también
 *           el material real del ítem talismán que se entrega con el kit.
 */
public class EfectoPersonalizado {

    public record Componente(PotionEffectType tipo, int amplificador) {}

    private final String id;
    private final String nombre;
    private final CategoriaEfecto categoria;
    private final Material material;
    private final List<Componente> componentes;
    private final List<String> descripcion;

    public EfectoPersonalizado(String id, String nombre, CategoriaEfecto categoria, Material material,
                                List<String> descripcion, List<Componente> componentes) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.material = material;
        this.descripcion = descripcion;
        this.componentes = componentes;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public CategoriaEfecto getCategoria() {
        return categoria;
    }

    public Material getMaterial() {
        return material;
    }

    public List<Componente> getComponentes() {
        return componentes;
    }

    public List<String> getDescripcion() {
        return descripcion;
    }
}
