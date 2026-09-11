package com.darkmortol.kitspersonalizados.model;

/**
 * A quién va dirigido el kit:
 * - NORMAL: usuarios normales y VIP, se ve en /kit lista, respeta cooldown y
 *   puede tener precio de compra.
 * - STAFF: solo staff, se ve en /kit staff, un único permiso
 *   (kit.&lt;nombre&gt;.staff) y sin cooldown ni precio: reclamo directo.
 */
public enum VisibilidadKit {
    NORMAL,
    STAFF
}
