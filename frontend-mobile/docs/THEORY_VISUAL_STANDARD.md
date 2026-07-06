# Estándar visual de pantallas de teoría

Referencia oficial: las pantallas aprobadas de Resta y Multiplicación del Nivel
Intermedio. Las pantallas antiguas del Nivel Básico todavía no forman parte de
este estándar y no deben modificarse hasta planificar su migración.

## Estructura

- Margen horizontal de pantalla: `16.dp`.
- Separación vertical principal: `18–22.dp`.
- Contenido con scroll vertical y acciones al final.
- Tarjetas de ancho completo, radio de `18–24.dp`, borde claro y elevación baja.
- El contenido matemático recibe el mayor ancho disponible.
- NEO acompaña la explicación o el resultado sin dominar la composición.

## Tipografía

- Título de lección: `24.sp`, interlineado cercano a `30.sp`, peso `Black` y
  color casi negro (`#111827`).
- Encabezados de tarjeta: `14–15.sp`, peso `Bold`.
- Texto explicativo: `14–15.sp`, interlineado `21–23.sp`.
- Fracciones y operaciones principales: `25–28.sp`, peso visual dominante.

## Responsive

- Objetivos mínimos: `360.dp`, `392.dp` y `411.dp`.
- Usar pesos y ancho disponible antes que tamaños horizontales rígidos.
- Mantener un ancho mínimo solo donde evite deformar fracciones o resultados.
- Permitir crecimiento vertical y scroll; no recortar texto ni reducir NEO
  hasta volverlo ilegible.

## Acciones

- Botones inferiores de `54.dp`, radio de `18.dp`.
- Acción secundaria clara y acción primaria morada.
- Conservar el patrón de navegación existente de cada flujo.
