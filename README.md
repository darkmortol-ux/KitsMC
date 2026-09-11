# KitsPersonalizados

Plugin para Paper 1.21.11 (Java 21) que permite crear kits totalmente
personalizables mediante un asistente por GUI, con efectos especiales,
encantamientos hasta nivel 100, cooldowns/precio configurables, y kits
exclusivos de staff con permiso simplificado.

## Instalación

1. Compila con `mvn clean package` (requiere acceso al repositorio de PaperMC:
   `https://repo.papermc.io/repository/maven-public/`).
2. Copia `target/KitsPersonalizados.jar` a la carpeta `plugins` del servidor.
3. Reinicia el servidor. Se generará `plugins/KitsPersonalizados/config.yml`.

## Comandos

| Comando | Descripción | Permiso |
|---|---|---|
| `/crearkit <nombre>` | Abre el asistente para crear un kit nuevo. | `kitspersonalizados.admin` |
| `/editarkit <nombre>` | Reabre el asistente sobre un kit existente. | `kitspersonalizados.admin` |
| `/borrarkit <nombre>` | Elimina un kit. | `kitspersonalizados.admin` |
| `/listakits` | Lista todos los kits creados (indica si son de Usuarios o Staff). | `kitspersonalizados.admin` |
| `/kit lista` | GUI con los kits **de Usuarios** a los que el jugador tiene acceso (reclamo o compra); click para reclamar/comprar al instante. | Ninguno (filtra solo lo que ya puede usar) |
| `/kit staff` | GUI con los kits **de Staff** a los que el jugador tiene acceso; click para reclamar directo, sin cooldown ni costo. | Ninguno (filtra solo lo que ya puede usar) |
| `/kit <nombre>` | El jugador reclama (o compra) el kit para sí mismo. | Depende del tipo de kit (ver abajo) |
| `/kit <nombre> <jugador>` | Un admin entrega el kit a otro jugador, sin cooldown. | `kitspersonalizados.admin.dar` |

## El asistente

La primera pantalla del asistente pregunta la **visibilidad** del kit, y eso
cambia el resto del flujo:

- **Usuarios (Normal + VIP)**: flujo completo de 9 pantallas, incluyendo
  Cooldown y Precio. Se ve en `/kit lista`.
- **Solo Staff**: flujo reducido de 7 pantallas (se saltea Cooldown y Precio,
  ya que no aplican). Se ve en `/kit staff`.

1. **Visibilidad**: Usuarios (Normal + VIP) o Solo Staff.
2. **Material de la armadura**: cuero, cota de malla, hierro, oro, diamante,
   netherite o sin armadura.
3. **Armas y herramientas**: espada, hacha, pico, pala, azada, arco, ballesta,
   tridente y tijeras, cada una con su material/tier.
4. **Pociones**: fuerza, velocidad, curación, daño, veneno, regeneración,
   resistencia al fuego, respiración acuática, visión nocturna e invisibilidad
   (y sus variantes larga/fuerte).
5. **Comida**: 21 alimentos comunes, se entregan en stacks de 16.
6. **Encantamientos (nivel 1-100)**: una sección por cada pieza de armadura y
   por cada arma/herramienta agregada. Click izquierdo +1 / derecho -1,
   shift+click +10/-10. El nivel se aplica de forma "insegura" (bypassa el
   límite vanilla) para poder llegar hasta 100.
7. **Efectos personalizados** (64 en total, 16 por categoría):
   - **Armadura**: se activa solo con las 4 piezas del set equipadas.
   - **Armas**: se elige un arma específica del kit; el efecto se activa si
     esa arma está en la hotbar.
   - **Herramientas**: igual que armas, pero con herramientas.
   - **Varios**: ítems "talismán" adicionales (con brillo y nombre propio) que
     se activan al llevarlos en la mano secundaria. Se pueden elegir varios.
   - En un kit de Staff, este es el último paso: "Siguiente" guarda directo.
8. **Cooldown** *(solo kits de Usuarios)*: sin horario, cada 1 hora, cada 1
   día, cada 1 semana o cada 1 mes.
9. **Precio (economía / Vault)** *(solo kits de Usuarios)*: botones de
   +$50/+$100/+$500 y -$50/-$100/-$500 para armar el precio que quieras. Con
   $0 el kit no se puede comprar (solo por permiso). Con precio > $0, los
   jugadores con el permiso de compra pueden comprarlo aunque no tengan el
   permiso normal de reclamo. Requiere Vault + un plugin de economía — si no
   están instalados, la compra se desactiva con un aviso, sin romper el resto
   del plugin.

## Permisos por tipo de kit

**Kits de Usuarios** (Normal + VIP):
- Sin horario: `kit.<nombre>`
- Con cooldown: `kit.<nombre>.1h`, `kit.<nombre>.1d`, `kit.<nombre>.1s`
  (semana) o `kit.<nombre>.1m` (mes), según lo configurado.
- Compra (si tiene precio > $0): `kit.<nombre>.buy` — paga con la economía
  del servidor, **sin cooldown** y sin necesitar el permiso normal.

**Kits de Staff**:
- Un único permiso: `kit.<nombre>.staff` — reclamo directo, **sin cooldown
  ni costo**, sin ninguna otra restricción.

Cada vez que guardas un kit desde el asistente, el propio archivo
`kits/<nombre>.yml` incluye un bloque de comentarios al principio con el o
los permisos exactos de ese kit, listos para copiar a tu gestor de
permisos/rangos (LuckPerms, PermissionsEx, etc.).

## Notas técnicas

- Los kits se guardan en `plugins/KitsPersonalizados/kits/<nombre>.yml`.
- El cooldown de cada jugador se guarda en
  `plugins/KitsPersonalizados/playerdata/<uuid>.yml` (los kits de Staff no
  usan cooldown, así que no generan entradas ahí).
- Los efectos personalizados se revisan cada `intervalo-revision-efectos`
  ticks (20 por defecto = 1 segundo) y se identifican mediante
  PersistentDataContainer en los propios ítems, no por inventario del jugador
  en general — así funcionan aunque el jugador tenga varios kits mezclados.
- Vault es un **softdepend** (opcional): el plugin funciona igual sin él,
  solo se desactiva la opción de comprar kits de Usuarios (los kits de Staff
  nunca dependen de la economía).
- `/kit <nombre>` detecta automáticamente si el kit es de Usuarios o de
  Staff y aplica la lógica correspondiente (permiso normal → compra, o
  permiso `.staff` directo).
- `/kit lista` y `/kit staff` son GUIs separadas: cada una solo muestra los
  kits de su propio tipo de visibilidad, aunque el jugador tenga acceso a
  ambos tipos.
