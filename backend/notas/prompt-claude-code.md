# Prompt para Claude Code / Codex – Revisión e implementación del backlog NICE LOOK

Eres un agente de desarrollo full-stack trabajando sobre el repositorio del proyecto
**Peluquería NICE LOOK**. En la /backend/notas del proyecto encontrarás el archivo
`backlog.md`, que contiene las 45 Historias de Usuario (HU) del Product Backlog, cada una
con: Rol, Funcionalidad, Razón, Criterios de aceptación, Peso y Prioridad (MoSCoW: M = Must,
S = Should, C = Could, W = Wish).

## Objetivo general

1. **Auditar** el código actual y determinar, para cada HU del backlog, si está:
   - ✅ Implementada y cumple todos sus criterios de aceptación.
   - ⚠️ Implementada parcialmente (existe la funcionalidad pero falla algún criterio,
     validación, mensaje de error, caso alterno, etc.).
   - ❌ No implementada.

2. **Mejorar** las HU que ya existen (✅ o ⚠️) para que:
   - Cumplan al 100% sus criterios de aceptación y casos alternos.
   - Su UI siga el mismo estilo visual, componentes, paleta de colores, tipografía y
     patrones de interacción que ya se usan en el resto de la aplicación.
   - Sean completamente **responsive** (móvil, tablet y escritorio), usando el mismo
     enfoque de responsividad (breakpoints, sistema de grid/flex, librería CSS, etc.)
     que ya emplea el proyecto.

3. **Implementar** las HU que falten (❌), siguiendo:
   - La misma arquitectura, convenciones de nombres, estructura de carpetas y patrones
     de código (componentes, hooks, servicios, controladores, modelos, rutas, etc.) que
     ya existen en el proyecto.
   - El mismo stack tecnológico ya configurado (no introducir librerías nuevas a menos
     que sea estrictamente necesario y no exista una alternativa ya instalada).
   - Funcionalidad completa: lógica de negocio, validaciones, manejo de errores, mensajes
     de confirmación/alerta tal como se describen en los criterios de aceptación.
   - UI funcional, coherente con el diseño existente y responsive.

## Metodología de trabajo

1. Lee `backlog.md` completo antes de empezar.
2. Explora el repositorio (frontend y backend) para entender:
   - Arquitectura general (carpetas, módulos, capas).
   - Sistema de diseño actual (componentes UI reutilizables, estilos globales, framework
     CSS o de componentes, breakpoints usados para responsive).
   - Modelos de datos / esquema de base de datos existentes.
   - Convenciones de nombres de archivos, componentes, endpoints, rutas y commits.
3. Crea o actualiza un archivo `BACKLOG_STATUS.md` (o similar) con una tabla de estado por
   HU (ID, estado actual, observaciones, archivos relacionados). Mantenlo actualizado a
   medida que avances.
4. Procesa las HU **en orden de prioridad**: primero todas las `Must (M)`, luego
   `Should (S)`, luego `Could (C)` y al final `Wish (W)`. Dentro de cada prioridad,
   respeta dependencias lógicas (por ejemplo, HU-1 Iniciar sesión y HU-28 Cerrar sesión
   deben estar correctas antes de validar HU que dependan de roles autenticados).
5. Para cada HU:
   - Indica brevemente el diagnóstico (✅/⚠️/❌) y qué archivos/componentes implementan
     o implementarán la funcionalidad.
   - Aplica los cambios de código necesarios.
   - Verifica manualmente (o mediante pruebas si el proyecto ya tiene tests) que se
     cumplen todos los criterios de aceptación y casos alternos descritos en
     `backlog.md`.
   - Verifica responsividad: la vista debe funcionar correctamente en anchos pequeños
     (≈360px), medianos (≈768px) y grandes (≥1024px), sin overflow horizontal, con
     elementos legibles y botones/inputs usables al tacto.
6. No rompas funcionalidades existentes que ya cumplen sus criterios; si una mejora
   implica refactor, hazlo de forma incremental y prueba que el resto de módulos sigan
   funcionando.
7. Mantén la consistencia visual: reutiliza componentes (botones, modales, tarjetas,
   formularios, tablas, badges de estado, notificaciones/toasts, etc.) ya existentes
   en lugar de crear variantes nuevas, salvo que el criterio de aceptación exija un
   componente que no existe (en ese caso, créalo siguiendo el mismo estilo).

## Consideraciones especiales por dominio

- **Roles y permisos:** Administrador, Recepcionista, Barbero/Empleado y Cliente tienen
  vistas y permisos distintos. Verifica que cada HU se muestre/oculte según el rol
  correspondiente (ver HU-1, HU-28, HU-42, HU-44).
- **Categorías y servicios** (HU-2 a HU-7, HU-31, HU-32): CRUD completo con validaciones
  de longitud de campos, formatos de imagen (jpg/png), unicidad de nombres y
  activación/desactivación lógica (no eliminación física).
- **Clientes** (HU-8, HU-34, HU-35, HU-45): registro, consulta, edición y desactivación,
  con validación de duplicados por teléfono/correo.
- **Agenda y citas** (HU-9, HU-12-14, HU-19-27, HU-38, HU-40): disponibilidad de
  barberos, creación/edición/cancelación/reprogramación de citas, confirmación de
  asistencia, marcar cita como finalizada, bloqueo de horarios.
- **Pagos y reportes** (HU-10, HU-11, HU-23, HU-29, HU-30, HU-39, HU-41, HU-43):
  registro de caja base diaria, registro y anulación de pagos, pago en línea de citas,
  porcentaje variable por servicio, reportes de ingresos/desempeño e historial de pagos
  a empleados.
- **Empleados** (HU-15, HU-16, HU-36, HU-37): registro, consulta, edición y
  desactivación.
- **Notificaciones** (HU-42): centro de notificaciones para Administrador y
  Recepcionista sobre activaciones/desactivaciones y otros eventos relevantes.

## Entregables esperados

1. Código fuente actualizado/implementado para las HU correspondientes.
2. `BACKLOG_STATUS.md` actualizado con el estado final de cada HU (idealmente todas en
   ✅ al terminar, o documentando claramente lo que quedó pendiente y por qué).
3. Un resumen final (en el chat o en un archivo `CHANGES.md`) que liste, por HU,
   los cambios realizados, archivos modificados/creados y cómo se validó el
   cumplimiento de los criterios de aceptación y la responsividad.

No omitas ninguna HU del backlog. Si alguna HU no aplica al estado actual del proyecto
(por ejemplo, porque depende de una integración externa no configurada, como pagos en
línea o login con Google), implementa al menos la UI y la lógica interna simulando o
dejando preparada la integración, y deja documentada la limitación en
`BACKLOG_STATUS.md`.
