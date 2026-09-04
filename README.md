# KitsPersonalizados

Plugin para Paper 1.21.11 (Java 21) que permite crear kits totalmente
personalizables mediante un asistente por GUI de 7 pantallas, con efectos
especiales, encantamientos hasta nivel 100 y cooldowns configurables por kit.

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
| `/listakits` | Lista todos los kits creados. | `kitspersonalizados.admin` |
| `/kit lista` | Muestra en un GUI únicamente los kits a los que el jugador tiene acceso (reclamo o compra); click para reclamar/comprar al instante. | Ninguno (filtra solo lo que ya puede usar) |
| `/kit <nombre>` | El jugador reclama el kit para sí mismo (respeta permiso y cooldown). | `kit.<nombre>` o `kit.<nombre>.<horario>` |
| `/kit <nombre> <jugador>` | Un admin entrega el kit a otro jugador, sin cooldown. | `kitspersonalizados.admin.dar` |

## El asistente (8 pantallas)

1. **Material de la armadura**: cuero, cota de malla, hierro, oro, diamante,
   netherite o sin armadura.
2. **Armas y herramientas**: espada, hacha, pico, pala, azada, arco, ballesta,
   tridente y tijeras, cada una con su material/tier.
3. **Pociones**: fuerza, velocidad, curación, daño, veneno, regeneración,
   resistencia al fuego, respiración acuática, visión nocturna e invisibilidad
   (y sus variantes larga/fuerte).
4. **Comida**: 21 alimentos comunes, se entregan en stacks de 16.
5. **Encantamientos (nivel 1-100)**: una sección por cada pieza de armadura y
   por cada arma/herramienta agregada. Click izquierdo +1 / derecho -1,
   shift+click +10/-10. El nivel se aplica de forma "insegura" (bypassa el
   límite vanilla) para poder llegar hasta 100.
6. **Efectos personalizados** (64 en total, 16 por categoría):
   - **Armadura**: se activa solo con las 4 piezas del set equipadas.
   - **Armas**: se elige un arma específica del kit; el efecto se activa si
     esa arma está en la hotbar.
   - **Herramientas**: igual que armas, pero con herramientas.
   - **Varios**: ítems "talismán" adicionales (con brillo y nombre propio) que
     se activan al llevarlos en la mano secundaria. Se pueden elegir varios.
7. **Cooldown**: sin horario, cada 1 hora, cada 1 día, cada 1 semana o cada 1
   mes.
8. **Precio (economía / Vault)**: botones de +$50/+$100/+$500 y -$50/-$100/
   -$500 para armar el precio que quieras. Si el precio queda en **$0, el kit
   no se puede comprar** (solo se reclama por permiso normal). Si se le pone
   cualquier precio mayor a $0, los jugadores con el permiso de compra podrán
   comprarlo aunque no tengan el permiso normal de reclamo. Requiere el
   plugin **Vault** + un plugin de economía (Essentials, CMI, etc.) — si no
   están instalados, la compra simplemente se desactiva con un aviso, sin
   romper el resto del plugin.

## Permisos de reclamo y compra

- Si el kit se configuró **sin horario**: `kit.<nombre>`
- Si el kit tiene cooldown: `kit.<nombre>.1h`, `kit.<nombre>.1d`,
  `kit.<nombre>.1s` (semana) o `kit.<nombre>.1m` (mes), según lo configurado.
- Si el kit tiene un precio mayor a $0: `kit.<nombre>.buy` — permite comprarlo
  pagando con la economía del servidor, **sin cooldown** y sin necesitar el
  permiso normal de reclamo.

Cada vez que guardas un kit desde el asistente, el propio archivo
`kits/<nombre>.yml` incluye un bloque de comentarios al principio con estos
permisos ya armados y listos para copiar a tu gestor de permisos/rangos
(LuckPerms, PermissionsEx, etc.).

## Notas técnicas

- Los kits se guardan en `plugins/KitsPersonalizados/kits/<nombre>.yml`.
- El cooldown de cada jugador se guarda en
  `plugins/KitsPersonalizados/playerdata/<uuid>.yml`.
- Los efectos personalizados se revisan cada `intervalo-revision-efectos`
  ticks (20 por defecto = 1 segundo) y se identifican mediante
  PersistentDataContainer en los propios ítems, no por inventario del jugador
  en general — así funcionan aunque el jugador tenga varios kits mezclados.
- Vault es un **softdepend** (opcional): el plugin funciona igual sin él,
  solo se desactiva la opción de comprar kits.
- `/kit <nombre>` primero revisa el permiso normal de reclamo (y su
  cooldown); si el jugador no lo tiene, revisa si el kit tiene precio y si el
  jugador tiene `kit.<nombre>.buy` para ofrecerle la compra en su lugar.
