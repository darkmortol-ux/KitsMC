package com.darkmortol.kitspersonalizados.gui;

import com.darkmortol.kitspersonalizados.KitsPersonalizados;
import com.darkmortol.kitspersonalizados.model.Kit;
import com.darkmortol.kitspersonalizados.util.ItemBuilder;
import com.darkmortol.kitspersonalizados.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Pantalla visual (yunque) para escribir el nombre de un kit nuevo sin tener
 * que usar el chat. El jugador escribe el nombre en el campo de texto del
 * yunque y hace click en el resultado (slot 2) para confirmarlo.
 */
public class AnvilNameGUI implements InventoryHolder {

    /** Subcomandos de /kitsp: un kit con uno de estos nombres quedaría inalcanzable. */
    private static final java.util.Set<String> NOMBRES_RESERVADOS = java.util.Set.of(
            "crear", "editar", "borrar", "listar", "dar", "lista", "staff");

    private final KitsPersonalizados plugin;
    private final Player admin;
    private Inventory inventory;

    public AnvilNameGUI(KitsPersonalizados plugin, Player admin) {
        this(plugin, admin, "");
    }

    public AnvilNameGUI(KitsPersonalizados plugin, Player admin, String nombreInicial) {
        this.plugin = plugin;
        this.admin = admin;
        this.inventory = Bukkit.createInventory(this, InventoryType.ANVIL, MessageUtil.colorear("&8Nombre del kit"));
        ItemStack base = new ItemBuilder(Material.PAPER)
                .nombre(nombreInicial.isEmpty() ? "NuevoKit" : nombreInicial)
                .build();
        inventory.setItem(0, base);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void abrir() {
        admin.openInventory(inventory);
    }

    /** Se llama cuando el jugador hace click en el resultado del yunque (slot 2). */
    public void confirmarNombre(String nombreCrudo) {
        String nombre = nombreCrudo == null ? "" : nombreCrudo.trim();
        if (!nombre.matches("[A-Za-z0-9_-]{1,32}")) {
            admin.closeInventory();
            plugin.getMensajes().enviar(admin, "nombre-invalido");
            return;
        }
        if (NOMBRES_RESERVADOS.contains(nombre.toLowerCase())) {
            admin.closeInventory();
            plugin.getMensajes().enviar(admin, "nombre-reservado", "%nombre%", nombre);
            return;
        }
        if (plugin.getKitManager().existe(nombre)) {
            admin.closeInventory();
            plugin.getMensajes().enviar(admin, "kit-ya-existe");
            return;
        }
        admin.closeInventory();
        Kit kit = new Kit(nombre);
        KitCreationSession sesion = new KitCreationSession(plugin, admin, kit, false);
        GUIListener.iniciarSesion(sesion);
        new VisibilidadGUI(sesion).abrir(admin);
    }
}
