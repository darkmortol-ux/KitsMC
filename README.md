# KitsPersonalizados

Plugin para Paper 1.21.11 (Java 21) que permite crear kits totalmente
personalizables mediante un asistente por GUI, con efectos especiales,
encantamientos hasta nivel 100, cooldowns/precio configurables, y kits
exclusivos de staff. **Todo se maneja con un único comando: `/kitsp`**, y
cada función tiene su propia pantalla visual (nada de GUIs a medias con
texto en el chat).

## Instalación

1. Compila con `mvn clean package` (requiere acceso al repositorio de PaperMC:
   `https://repo.papermc.io/repository/maven-public/`).
2. Copia `target/KitsPersonalizados.jar` a la carpeta `plugins` del servidor.
3. Reinicia el servidor. Se generará `plugins/KitsPersonalizados/config.yml`.

## El comando `/kitsp` (alias `/kits`)

Cada subcomando funciona en tres niveles: **completo** (todo por texto, para
quien ya sabe lo que quiere), **parcial** (le falta un dato y se abre la
pantalla justa para completarlo) o **vacío** (se abre la pantalla desde el
principio). Nunca hace falta escribir todo de memoria.

| Subcomando | Pantalla que abre | Permiso |
|---|---|---|
| `/kitsp` (sin nada) | **Menú principal**: accesos directos a todo lo demás, según tus permisos. | Ninguno |
| `/kitsp crear [nombre]` | **Yunque** para escribir el nombre (si no lo pusiste) → asistente de creación. | `kitspersonalizados.admin` |
| `/kitsp editar [nombre]` | Si no diste nombre: **selector de kits**. Si lo diste: entra directo al asistente, precargado con ese kit. | `kitspersonalizados.admin` |
| `/kitsp borrar [nombre]` | Si no diste nombre: **selector de kits**. Si lo diste: **pantalla de confirmación** (Sí/No) antes de borrar. | `kitspersonalizados.admin` |
| `/kitsp listar` | **Vista completa de admin**: todos los kits con su visibilidad, permisos, cooldown/precio. Click = editar. | `kitspersonalizados.admin` |
| `/kitsp dar [nombre] [jugador]` | Completa lo que falte con pantallas: **selector de kits** → **selector de jugador conectado** (cabezas). | `kitspersonalizados.admin.dar` |
| `/kitsp lista` | Kits **de Usuarios** a los que el jugador tiene acceso; click = reclamar/comprar. | Ninguno (filtra solo lo accesible) |
| `/kitsp staff` | Kits **de Staff** a los que el jugador tiene acceso; click = reclamo directo. | Ninguno (filtra solo lo accesible) |
| `/kitsp <nombre> [jugador]` | Atajo directo sin pantallas: el jugador se reclama el kit a sí mismo, o un admin se lo entrega a `[jugador]`. | Depende del tipo de kit / `kitspersonalizados.admin.dar` |

## El asistente de creación/edición

La primera pantalla pregunta la **visibilidad** del kit, y eso cambia el
resto del flujo:

- **Usuarios (Normal + VIP)**: 9 pantallas, incluyendo Cooldown y Precio.
  Se ve en `/kitsp lista`.
- **Solo Staff**: 7 pantallas (se saltea Cooldown y Precio). Se ve en
  `/kitsp staff`.

1. **Visibilidad**: Usuarios o Solo Staff.
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
   shift+click +10/-10 (bypasa el límite vanilla para poder llegar a 100).
7. **Efectos personalizados** (64 en total, 16 por categoría: Armadura,
   Armas, Herramientas, Varios). En un kit de Staff, este es el último paso.
8. **Cooldown** *(solo Usuarios)*: sin horario, 1 hora, 1 día, 1 semana o 1 mes.
9. **Precio (economía / Vault)** *(solo Usuarios)*: botones de
   +$50/+$100/+$500 y -$50/-$100/-$500. Con $0 no es comprable.

## Permisos por tipo de kit

**Kits de Usuarios** (Normal + VIP):
- Sin horario: `kit.<nombre>`
- Con cooldown: `kit.<nombre>.1h`, `kit.<nombre>.1d`, `kit.<nombre>.1s`
  (semana) o `kit.<nombre>.1m` (mes).
- Compra (si tiene precio > $0): `kit.<nombre>.buy` — sin cooldown, sin
  necesitar el permiso normal.

**Kits de Staff**:
- Un único permiso: `kit.<nombre>.staff` — reclamo directo, sin cooldown
  ni costo.

**Permisos generales del plugin**:
- `kitspersonalizados.admin`: crear, editar, borrar, listar (vista admin).
- `kitspersonalizados.admin.dar`: entregar cualquier kit a otro jugador.

Cada vez que guardas un kit desde el asistente, el propio archivo
`kits/<nombre>.yml` incluye un bloque de comentarios al principio con el o
los permisos exactos de ese kit, listos para copiar a tu gestor de
permisos/rangos (LuckPerms, PermissionsEx, etc.).

## Notas técnicas

- Los kits se guardan en `plugins/KitsPersonalizados/kits/<nombre>.yml`.
- El cooldown de cada jugador se guarda en
  `plugins/KitsPersonalizados/playerdata/<uuid>.yml` (los kits de Staff no
  generan entradas ahí).
- Los efectos personalizados se revisan cada `intervalo-revision-efectos`
  ticks (20 por defecto = 1 segundo) vía PersistentDataContainer en los
  propios ítems — funcionan aunque el jugador tenga varios kits mezclados.
- Vault es un **softdepend** (opcional): sin él solo se desactiva la compra
  de kits de Usuarios (los de Staff nunca dependen de la economía).
- La pantalla de "Crear kit" usa un **yunque real** (`InventoryType.ANVIL`)
  para que escribas el nombre sin salir del GUI; el costo de experiencia se
  fuerza a 0 para que nunca te cobre niveles.
- `/kitsp lista` y `/kitsp staff` son pantallas separadas: cada una solo
  muestra los kits de su propio tipo de visibilidad.
- Todas las pantallas sueltas (selector de kits, confirmación de borrado,
  selector de jugador, menú principal, vista admin) comparten una sola
  interfaz (`ClickeableGUI`) que maneja el `GUIListener` de forma genérica.
- Un kit **no puede llamarse** `crear`, `editar`, `borrar`, `listar`, `dar`,
  `lista` ni `staff` (son los subcomandos reservados de `/kitsp`); el yunque
  de creación bloquea esos nombres automáticamente.
