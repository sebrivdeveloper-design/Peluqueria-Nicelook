# Product Backlog – Peluquería NICE LOOK

Documento generado a partir del archivo `Peluquerías_NICE_LOOK_actualizado__1_.xlsx`, hoja **Product Backlog**.

Total de Historias de Usuario (HU): **44**

## Tabla resumen

| ID | Rol | Funcionalidad | Peso | Prioridad |
|----|-----|---------------|------|-----------|
| HU-1 | Usuario(Administrador, Recepsionista, Empleado) | Iniciar sesion | 13 | M |
| HU-2 | Yo como usuario(administrador) | Crear categorias | 8 | M |
| HU-3 | Yo como usuario(administrador) | Requiero consultar una categoria | 8 | C |
| HU-4 | Yo, como Administrador | Requiero desactivar una categoria | 13 | C |
| HU-31 | Yo como administrador | Requiero  editar una categoría | 5 | S |
| HU-5 | Yo como usuario(administrador) | Registrar servicios en una categoria especifica | 13 | S |
| HU-6 | Yo, como Administrador | Requiero consultar un servicio | 8 | C |
| HU-32 | Yo como administrador | requiero editar un servicio | 8 | S |
| HU-7 | Yo, como Administrador | Requiero desactivar un servicio | 13 | C |
| HU-8 | Yo como Recepcionista | Requiero registrar datos de cliente | 20 | M |
| HU-9 | Yo como Recepcionista | Quiero visualizar la disponibilidad de todos los barberos | 20 | S |
| HU-10 | Yo como Recepcionista | Quiero registrar la base con la que inicio el día | 8 | M |
| Hu-11 | Yo como Recepcionista | Quiero registrar el pago de cada servicio recibido | 40 | S |
| HU-12 | Yo como Barbero | Requiero agendar mis horas de trabajo | 20 | M |
| HU-13 | Yo como Barbero | Requiero editar mis horas de trabajo agendadas | 20 | S |
| HU-14 | Yo como Barbero | Requiero ver turnos agendados | 13 | C |
| HU-15 | Yo como administrador | Requiero registrar empleados | 20 | M |
| HU-16 | Yo como administrador | Requiero desactivar un empleado | 20 | C |
| HU-17 | Yo como Cliente | Requiero ver el listado de servicios ofertados | 8 | S |
| HU-18 | Yo como Cliente | Requiero hacer búsquedas de servicios en la página | 13 | C |
| HU-19 | Yo como Cliente | Requiero reservar una cita para un servicio | 40 | M |
| HU-20 | Yo como cliente | Requiero cancelar una cita programada | 20 | S |
| HU-21 | Yo como cliente | Requiero reprogramar una cita existente | 13 | M |
| HU-22 | Yo como cliente | Requiero consultar el historial de mis citas | 13 | C |
| HU-23 | Yo como cliente | Requiero pagar mi cita en línea | 40 | M |
| HU-24 | Yo como cliente | Requiero confirmar mi asistencia a la cita | 20 | M |
| HU-26 | Yo como recepcionista | Requiero visualizar la agenda diaria de citas | 13 | M |
| HU-27 | Yo como barbero | Requiero marcar una cita como finalizada | 20 | S |
| HU-28 | Yo como Usuario(Administrador, Recepcionista, Empleado, cliente) | Requiero cerrar sesion | 8 | W |
| HU-29 | Yo como Administrador | Requiero colocar un porcentaje variable por servicio | 8 | S |
| HU-30 | Yo como Administrador | Requiero seleccionar los servicios que trabajó un empleado | 13 | C |
| HU-33 | Yo como cliente | Requiero autenticarme o registrarme en el momento de reservar una cita | 13 | M |
| HU-34 | Yo como Recepcionista | Requiero consultar los datos de un cliente | 8 | S |
| HU-35 | Yo como Recepcionista | Requiero editar los datos de un cliente | 8 | S |
| HU-36 | Yo, como Administrador | Requiero consultar la información de un empleado | 8 | C |
| HU-37 | Yo como Administrador | Requiero editar los datos de un empleado | 8 | S |
| HU-38 | Yo como Recepcionista | Requiero registrar una cita para un cliente que llega sin reserva previa (presencial) | 13 | S |
| HU-39 | Yo como Recepcionista | Requiero consultar el historial de pagos del día y anular un pago registrado por error | 13 | S |
| HU-40 | Yo como Barbero | Requiero bloquear un día u horario puntual de mi agenda | 8 | C |
| HU-41 | Yo como Administrador | Requiero consultar reportes de ingresos y desempeño del negocio | 20 | C |
| HU-42 | Yo como Usuario (Administrador, Recepcionista) | Requiero ver notificaciones de la actividad del sistema | 13 | C |
| HU-43 | Yo como Administrador | Requiero consultar el historial de pagos realizados a un empleado | 8 | C |
| HU-44 | Yo como Usuario (Administrador, Recepcionista, Barbero, Cliente) | Requiero editar mi perfil | 8 | W |
| HU-45 | Yo como Recepcionista | Requiero desactivar un cliente | 8 | W |

## Historias de Usuario detalladas

### HU-1 – Iniciar sesion

- **Rol:** Usuario(Administrador, Recepsionista, Empleado)
- **Quiero / Necesito:** Iniciar sesion
- **Para / Razón:** Para tener acceso con mi rol
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el usuario accede al sistema, cuando abre la página principal, entonces el sistema debe mostrar la pantalla de inicio de sesión.

2. Dado que el usuario está en la pantalla de login, cuando presiona el botón “Iniciar sesión con Google”, entonces el sistema debe redirigir al servicio de autenticación de Google.

3. Dado que el usuario se autentica correctamente con Google, cuando el sistema valida su cuenta, entonces el sistema debe permitir el acceso al sistema según el rol asignado (Administrador, Recepcionista o Empleado).

4. Dado que un usuario se autentica con Google por primera vez, cuando el sistema no encuentra la cuenta en la base de datos, entonces el sistema muestra un mensaje "El usuario no se encuentra registrado"

5. La pantalla de inicio de sesión debe mostrar el logo del negocio, un eslogan o texto de bienvenida, el botón de inicio de sesión con Google.

6. Si ocurre un error durante la autenticación con Google, el sistema debe mostrar un mensaje indicando que no fue posible iniciar sesión.

---

### HU-2 – Crear categorias

- **Rol:** Yo como usuario(administrador)
- **Quiero / Necesito:** Crear categorias
- **Para / Razón:** Para asignar servicios ofertados
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de gestión de categorías, cuando selecciona la opción registrar categoría, el sistema debe mostrar un formulario para ingresar los datos de la categoría.

2. El formulario debe permitir ingresar los siguientes datos: nombre de la categoría (tipo alfabético no mayor a 20 caracteres) y descripción (tipo alfabético no mayor a 150 caracteres).

3. Dado que el administrador completa el formulario correctamente y confirma el registro, el sistema debe guardar la categoría en la base de datos.

4. Dado que la categoría fue registrada exitosamente, el sistema debe mostrar un mensaje de confirmación.

5. Dado que existen categorías registradas, el sistema debe mostrarlas en la página principal mediante tarjetas con su nombre y descripción que faciliten la navegación del cliente.

6. Dado que el cliente selecciona una categoría, el sistema debe mostrar los servicios asociados a dicha categoría.

Casos Alternos
1. El sistema no podrá crear otra categoría con el mismo nombre de una que ya se encuentre registrada.

---

### HU-3 – Requiero consultar una categoria

- **Rol:** Yo como usuario(administrador)
- **Quiero / Necesito:** Requiero consultar una categoria
- **Para / Razón:** para consultar información de las categorias
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador debe completar correctamente el inicio de sesión para acceder al modulo de administracion.

2. La ventana debe tener un boton que permita regresar a la pagina principal.
3. El Administrador seleccionará la opción "Categorias".

4. La aplicación debe tener un modulo de consultar categoria.

5.  El administrador digitará el nombre de la categoria para consultar los datos.

6. La aplicación buscará en la base de datos el registro solicitado.

7. El sistema mostrará la información de la categoria (nombre).

8. Si el registro no existe, la aplicación mostrará un mensaje de error indicando que la categoria no existe en la base de datos.

9. El administrador podrá realizar una nueva búsqueda y/o cerrar el formulario de consulta.

---

### HU-4 – Requiero desactivar una categoria

- **Rol:** Yo, como Administrador
- **Quiero / Necesito:** Requiero desactivar una categoria
- **Para / Razón:** para desactivar una categoria
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador debe completar correctamente el inicio de sesión para acceder al modulo de administracion.

2. La ventana debe tener un boton que permita regresar a la pagina principal.

3. El Administrador seleccionará la opción "Categorias".

4. El sistema mostrara un listado de las categorias existentes.

5. Cada categoría tiene la opción de desactivar .
5.1. Al presionar la opción "desactivar categoría" el sistema mostrará un cuadro emergente que reconfirmará la opción desactivar categoría.
5.2. Tendrá dos opciones de reconfirmación: si presiona "sí" el sistema desactiva la categoría junto con los servicios asociados a la misma"
5.3 Si presiona "no" el sistema lo devuelve a la pantalla anterior del formulario y cancela la desactivación.

6. El sistema desactivará el servicio (ocultará para vista cliente),pero la información de este se mantendrá en la base de datos.

---

### HU-31 – Requiero  editar una categoría

- **Rol:** Yo como administrador
- **Quiero / Necesito:** Requiero  editar una categoría
- **Para / Razón:** para cambiar algo que se digitó mal
- **Peso (story points):** 5
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

Dado que el administrador accede al módulo de gestión de categorías, cuando selecciona la opción editar sobre una categoría existente, el sistema debe mostrar un formulario precargado con los datos actuales de dicha categoría (nombre, descripción).
El formulario debe permitir modificar los siguientes datos: nombre de la categoría (tipo alfabético no mayor a 20 caracteres), descripción (tipo alfabético no mayor a 150 caracteres)
Dado que el administrador modifica uno o varios campos y confirma la edición, el sistema debe validar que los datos cumplan con los formatos y restricciones establecidos antes de guardar los cambios.
Dado que el administrador modifica el campo correctamente y confirma la edición, el sistema debe actualizar la información de la categoría en la base de datos.
Dado que la categoría fue editada exitosamente, el sistema debe mostrar un mensaje de confirmación indicando que los cambios se guardaron correctamente.
Dado que la categoría editada se muestra en la página principal, el sistema debe reflejar de inmediato los cambios realizados (nuevo nombre, descripción) en la visualización para el cliente.
Dado que el administrador intenta salir del formulario de edición sin guardar los cambios, el sistema debe mostrar una alerta de confirmación preguntando si desea descartar los cambios realizados.

Casos Alternos:

Si el administrador intenta guardar el formulario dejando un campo obligatorio vacío, el sistema debe mostrar un mensaje de alerta indicando que el campo es obligatorio y no permitir guardar los cambios.
Si el administrador ingresa un nombre de categoría que ya existe (diferente a la categoría que está editando), el sistema debe mostrar un mensaje de alerta indicando que el nombre ya está en uso y no permitir guardar la edición.
Si el administrador supera el límite de caracteres permitido en el nombre (20) o en la descripción (150), el sistema debe mostrar un mensaje de alerta indicando el límite máximo permitido.
Dado que existen servicios asociados a la categoría editada, el sistema debe mantener dichas asociaciones intactas tras la edición.
Si ocurre un error durante el proceso de actualización (por ejemplo, fallo de conexión con la base de datos), el sistema debe mostrar un mensaje de alerta informando que la edición no pudo completarse y solicitar al administrador intentarlo nuevamente.

---

### HU-5 – Registrar servicios en una categoria especifica

- **Rol:** Yo como usuario(administrador)
- **Quiero / Necesito:** Registrar servicios en una categoria especifica
- **Para / Razón:** Para mostrar servicios ofertados
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de servicios, cuando selecciona la opción registrar servicio, entonces el sistema debe mostrar un formulario para ingresar los datos del servicio.

2. Dado que el administrador completa el formulario con nombre(tipo alfabetico, no mayor a 30 caracteres), descripción (tipo alfabetico, no mayor a 100 caracteres), duración(tipo alfanumerico, no mayor a 30 caracteres), precio del servicio (tipo numerico, no mayor a 8 digitos) y una imagen representativa del servicio (tipo imagen jpg, png), y asociarlo a una categoría. Cuando presiona guardar, entonces el sistema debe registrar el servicio en la base de datos.

3. Dado que el servicio fue registrado correctamente, entonces el sistema debe mostrar un mensaje de confirmación.

4. Dado que existen servicios registrados, cuando un usuario consulta la lista de servicios, entonces el sistema debe mostrarlos en el catálogo de servicios disponibles.

---

### HU-6 – Requiero consultar un servicio

- **Rol:** Yo, como Administrador
- **Quiero / Necesito:** Requiero consultar un servicio
- **Para / Razón:** para conocer información detallada del servicio.
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador debe completar correctamente el inicio de sesión para acceder al modulo de administracion.

2. La ventana debe tener un boton que permita regresar a la pagina principal.

3. El Administrador seleccionará el boton "Servicios".

4. El Administrador seleccionará la opción "Consultar servicios".

4.1. Dado que el administrador accede a la página principal del sistema, cuando ingresa al apartado de servicios, entonces el sistema debe mostrar el listado de servicios disponibles.

4.2. Dado que existen servicios registrados en el sistema, entonces el sistema debe mostrarlos organizados por categoría.

4.3. Dado que el cliente visualiza el listado de servicios, entonces cada servicio debe mostrar al menos su nombre, descripción y precio.

5.  El administrador digitará el nombre del servicio (tipo texto alfabetico)  para consultar los datos

6. La aplicación buscará en la base de datos el registro solicitado.

7. El sistema mostrará la información del servicio (nombre, descripcion, precio, categoría, photo_urls)

8. Si el registro no existe, la aplicación mostrará un mensaje de error indicando que el servicio no existe en la base de datos.

9. El administrador pordrá realizar una nueva búsqueda y/o cerrar el formulario de consulta.

---

### HU-32 – requiero editar un servicio

- **Rol:** Yo como administrador
- **Quiero / Necesito:** requiero editar un servicio
- **Para / Razón:** para corregir algo mal digitado
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

Dado que el administrador accede al módulo de servicios, cuando selecciona la opción editar sobre un servicio existente, entonces el sistema debe mostrar un formulario precargado con los datos actuales del servicio (nombre, descripción, duración, precio y categoría asociada).
El formulario debe permitir modificar los siguientes datos: nombre (tipo alfabético, no mayor a 30 caracteres), descripción (tipo alfabético, no mayor a 100 caracteres), duración (tipo alfanumérico, no mayor a 30 caracteres), precio del servicio (tipo numérico, no mayor a 8 dígitos), la imagen representativa del servicio (tipo imagen jpg, png) y la categoría asociada.
Dado que el administrador modifica uno o varios campos y presiona guardar, entonces el sistema debe validar que los datos cumplan con los formatos y restricciones establecidos antes de actualizar la información.
Dado que el administrador modifica los datos correctamente y confirma la edición, entonces el sistema debe actualizar la información del servicio en la base de datos.
Dado que el servicio fue editado correctamente, entonces el sistema debe mostrar un mensaje de confirmación indicando que los cambios se guardaron exitosamente.
Dado que un usuario consulta la lista de servicios, entonces el sistema debe mostrar de inmediato la información actualizada del servicio en el catálogo de servicios disponibles.
Dado que el administrador intenta salir del formulario de edición sin guardar los cambios, entonces el sistema debe mostrar una alerta de confirmación preguntando si desea descartar los cambios realizados.

Casos Alternos:

Si el administrador intenta guardar el formulario dejando un campo obligatorio vacío, el sistema debe mostrar un mensaje de alerta indicando que el campo es obligatorio y no permitir guardar los cambios.
Si el administrador ingresa un valor que excede el número máximo de caracteres permitido en nombre (30), descripción (100) o duración (30), el sistema debe mostrar un mensaje de alerta indicando el límite máximo permitido.
Si el administrador ingresa en el campo precio un valor que no sea numérico o que supere los 8 dígitos, el sistema debe mostrar un mensaje de alerta indicando que el valor no es válido.
Si el administrador deja el campo de categoría sin seleccionar, el sistema debe mostrar un mensaje de alerta solicitando que se asocie el servicio a una categoría válida.
Si el administrador selecciona una categoría que ya no existe o fue eliminada, el sistema debe mostrar un mensaje de alerta indicando que debe seleccionar una categoría válida.
Si el administrador intenta cargar una imagen con un formato distinto a jpg o png, el sistema debe mostrar un mensaje de alerta indicando los formatos válidos y rechazar el archivo.
Si ocurre un error durante el proceso de actualización (por ejemplo, fallo de conexión con la base de datos), el sistema debe mostrar un mensaje de alerta informando que la edición no pudo completarse y solicitar al administrador intentarlo nuevamente.

---

### HU-7 – Requiero desactivar un servicio

- **Rol:** Yo, como Administrador
- **Quiero / Necesito:** Requiero desactivar un servicio
- **Para / Razón:** para desactivar un servicio que ya no se oferte
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador debe completar correctamente el inicio de sesión para acceder al modulo de administracion.

2. Dado que el administrador está en el módulo de consulta, cuando presiona el botón regresar, entonces el sistema vuelve al panel principal.

3. El Administrador seleccionará el boton "Servicios".

4. Cada servicio tendrá la opción "Desactivar", que es un botón como el de categorías con la palabta desactivar-y cuando este desactivado, activar.

5. Se requiere confirmar si se desea desactivar el servicio.

6. El sistema desactivará el servicio (ocultará para vista cliente),pero la información de este se mantendrá en la base de datos. Ademas mostrará un mensaje en el apartado de las notificaciones de la descativación o activación que se hizo

7. El administrador podrá realizar una nueva desactivación de servicios, podrá cancelar y/o cerrar el formulario.

---

### HU-8 – Requiero registrar datos de cliente

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero registrar datos de cliente
- **Para / Razón:** Contactarlo en caso de cambios en su cita
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de clientes, cuando selecciona registrar cliente, entonces el sistema debe mostrar un formulario para ingresar sus datos.

2. Dado que la recepcionista ingresa nombre (tipo alfabético, no mayor a 100 caracteres), teléfono(numérico, no mayor a 12 caracteres) y correo electrónico( alfanumérico con caracteres especiales, no mayor a 50 caracteres), cuando guarda la información, entonces el sistema debe registrar el cliente.

3. La recepcionista dará clic en el botón "Registrar" para confirmar el registro del cliente.
3.1Dado que el cliente fue registrado correctamente, entonces el sistema debe mostrar un mensaje de confirmación.

4. Si el cliente ya está registrado con el mismo teléfono o correo, el sistema debe mostrar un mensaje indicando que el cliente ya existe.

---

### HU-9 – Quiero visualizar la disponibilidad de todos los barberos

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Quiero visualizar la disponibilidad de todos los barberos
- **Para / Razón:** Asignar citas
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de agenda, cuando consulta la disponibilidad, entonces el sistema debe mostrar la agenda de todos los barberos.

2. Dado que se visualiza la agenda, entonces el sistema debe mostrar los horarios disponibles y los horarios ocupados.

3. Dado que existe un horario disponible, cuando la recepcionista lo selecciona, entonces el sistema debe permitir asignar una cita.

4. Al dar clic en el boton "Asignar cita" se mostrara una ventana emergente, donde estara el formulario para registrar cita.

---

### HU-10 – Quiero registrar la base con la que inicio el día

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Quiero registrar la base con la que inicio el día
- **Para / Razón:** Para saber con cuanta cantidad de dinero inicio el día en el negocio
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dentro de las funcionalidades del recepcionista, al ingresar a su perfil, habrá un módulo el cual tendrá la opción de registrar la base diaria (cantidad de dinero con la que empieza el día)
1.1 En el menú lateral seleccionará la opcion de registrar base
1.2. El sistema despliega un pequeño formulario en el cual podrá ingresar la cantidad de dinero diaria (tipo numerico no mayor a 8 digitos).
1.3.En la parte inferior, una vez ingresada la cantidad de dinero, habrá un botón en el cual registra el dinero con el que inicia.
1.4.El sistema muestra un mensaje de confirmación emergente que menciona si la cantidad de dinero ingresada es correcta. Tendrá dos opciones: sí para confirmar y proceder con el registro; y no para volver a editar la cantidad ingresada en la pantalla anterior.
1.5. El sistema mostrará un mensaje de confirmación exitoso del registro.

---

### Hu-11 – Quiero registrar el pago de cada servicio recibido

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Quiero registrar el pago de cada servicio recibido
- **Para / Razón:** Cerrar la caja diaria de forma exacta y sin errores
- **Peso (story points):** 40
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de pagos, cuando selecciona una cita finalizada, entonces el sistema debe mostrar el servicio realizado y su valor.
1.1. La recepcionista podrá seleccionar el medio de pago por el cual se realiza el pago del servicio(efectivo, transferencia bancaria, tarjeta). Una vez seleccionado el método de pago, la recepcionista podrá continuar con el registro del pago del cliente.

2. Dado que la recepcionista registra el pago del cliente, cuando confirma el registro, entonces el sistema debe guardar la transacción.

3. Dado que el pago fue registrado correctamente, entonces el sistema debe mostrar un mensaje de confirmación.

4. Dado que existen pagos registrados durante el día, cuando se consulta el cierre de caja, entonces el sistema debe mostrar el total de ingresos (separado de la base inicial registrada).

---

### HU-12 – Requiero agendar mis horas de trabajo

- **Rol:** Yo como Barbero
- **Quiero / Necesito:** Requiero agendar mis horas de trabajo
- **Para / Razón:** Para tener un control de los clientes a atender
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el barbero accede a su agenda, cuando selecciona configurar horario de trabajo, entonces el sistema debe mostrar un calendario de disponibilidad.

2. Dado que el barbero define sus días y horarios disponibles, cuando guarda los cambios, entonces el sistema debe registrar su disponibilidad.

3. Dado que el horario fue registrado correctamente, entonces debe reflejarse en la agenda visible para la recepcionista y los clientes.

---

### HU-13 – Requiero editar mis horas de trabajo agendadas

- **Rol:** Yo como Barbero
- **Quiero / Necesito:** Requiero editar mis horas de trabajo agendadas
- **Para / Razón:** Para tener un control de  mis horarios.
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** S – Should (Importante)
- **Observación:** Solo la recepcionista se comunicará de manera directa con los clientes para reagendar su cita en una nueva franja horaria

**Criterios de aceptación:**

1. Dado que el barbero accede a su agenda, cuando selecciona configurar horario de trabajo, entonces el sistema debe mostrar un calendario de disponibilidad.

2. Dado que el barbero define sus días y horarios disponibles, el barbero seleccionará la opción de "editar agenda"

3. El barbero procederá a seleccionar los días y horarios disponibles nuevamente.

4. Después de seleccionar su horario habrá una opción de guardar cambios, el cual tendrá un mensaje de confirmación emergente para confirmar o cancelar los cambios de horario a registrar.

5. La confirmación de cambio de horario será exitosa siempre y cuando no hayan clientes agendados en esa franja de horario registrada antes de la edición del mismo horario.
5.1.En caso que hayan clientes agendados, el sistema mostrará un mensaje diciendo "hay clientes agendados, por favor comuniquese con la recepcionista para reasignar las citas".

3. Dado que el horario fue registrado correctamente, entonces debe reflejarse en la agenda visible para la recepcionista y los clientes.

---

### HU-14 – Requiero ver turnos agendados

- **Rol:** Yo como Barbero
- **Quiero / Necesito:** Requiero ver turnos agendados
- **Para / Razón:** Para atender clientes
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el barbero accede a su agenda, cuando consulta sus turnos, entonces el sistema debe mostrar la lista de citas programadas.

2. Dado que existen citas programadas, entonces el sistema debe mostrar el horario, el cliente y el servicio solicitado.

3. Si no existen citas en un horario, el sistema debe mostrar el horario como disponible.

---

### HU-15 – Requiero registrar empleados

- **Rol:** Yo como administrador
- **Quiero / Necesito:** Requiero registrar empleados
- **Para / Razón:** Para que accedan a su agenda personal
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de empleados, cuando selecciona registrar empleado, entonces el sistema debe mostrar un formulario para ingresar los datos.

2. Dado que el administrador ingresa nombre, rol y datos de contacto del empleado, cuando guarda la información, entonces el sistema debe registrar el empleado.

3. Dado que el empleado fue registrado correctamente, entonces el sistema debe mostrar un mensaje de confirmación.

4. Dado que el empleado está registrado, entonces debe poder acceder al sistema según el rol asignado.

---

### HU-16 – Requiero desactivar un empleado

- **Rol:** Yo como administrador
- **Quiero / Necesito:** Requiero desactivar un empleado
- **Para / Razón:** Para que no tenga acceso al sistema
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de empleados, cuando selecciona un empleado registrado, entonces el sistema debe mostrar la opción de desactivar empleado.

2. Dado que el administrador confirma la desactivación, entonces el sistema debe cambiar el estado del empleado a inactivo.

3. Dado que el empleado está desactivado, entonces el sistema no debe permitir su acceso al sistema.

---

### HU-17 – Requiero ver el listado de servicios ofertados

- **Rol:** Yo como Cliente
- **Quiero / Necesito:** Requiero ver el listado de servicios ofertados
- **Para / Razón:** Con el fin de conocer facilmente todas las opciones disponibles y decidir que servicio tomar
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que el cliente accede a una landing page, no al sistema como lo hacen el admin, empleado o recepcionista, cuando ingresa se le muestran los servicios, entonces el sistema debe mostrar cards de servicios disponibles.

2. Dado que existen servicios registrados en el sistema, entonces el sistema debe mostrarlos organizados por categoría.

3. Dado que el cliente visualiza el listado de servicios, entonces cada servicio debe mostrar al menos su nombre, descripción y precio.

4. Si no existen servicios registrados en el sistema, entonces el sistema debe mostrar un mensaje indicando que no hay servicios disponibles.

5. Dado que el cliente accede a la landing page (catálogo de categorías y servicios), el sistema no debe solicitarle iniciar sesión ni registrarse.

---

### HU-18 – Requiero hacer búsquedas de servicios en la página

- **Rol:** Yo como Cliente
- **Quiero / Necesito:** Requiero hacer búsquedas de servicios en la página
- **Para / Razón:** Para tener una búsqueda más simplificada y directa
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el cliente accede a la página principal, entonces el sistema debe mostrar una barra de búsqueda para consultar servicios.

2. Dado que el cliente ingresa texto en la barra de búsqueda, cuando presiona el botón de búsqueda o la tecla Enter, entonces el sistema debe mostrar los servicios cuyo nombre coincida con el texto ingresado.

3. Dado que el cliente realiza una búsqueda, entonces el sistema debe permitir únicamente caracteres alfabéticos en el campo de búsqueda.

4. Si la búsqueda no encuentra resultados, entonces el sistema debe mostrar un mensaje indicando que no se encontraron servicios.

5. Dado que existen resultados de búsqueda, entonces el sistema debe mostrar la lista de servicios encontrados con su nombre, descripción y precio.

---

### HU-19 – Requiero reservar una cita para un servicio

- **Rol:** Yo como Cliente
- **Quiero / Necesito:** Requiero reservar una cita para un servicio
- **Para / Razón:** Para asegurar mi atención en un horario disponible.
- **Peso (story points):** 40
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el cliente accede al listado de servicios sin necesidad de autenticarse, cuando selecciona un servicio, entonces el sistema debe mostrar los barberos disponibles y sus horarios.

2. Dado que el cliente selecciona un barbero y un horario disponible, cuando confirma que desea reservar, entonces el sistema debe solicitarle iniciar sesión o registrarse con Google antes de continuar.

3. Dado que el cliente inicia sesión o se registra correctamente, entonces el sistema debe mostrar un formulario para confirmar/completar sus datos (nombre, número de WhatsApp y correo electrónico).

4. Dado que el cliente completa el formulario y confirma la reserva, entonces el sistema debe registrar la cita en la agenda del barbero.

5. Dado que la cita fue registrada correctamente, entonces el sistema debe mostrar un mensaje de confirmación con los detalles de la cita.

6. Dado que la cita fue confirmada, entonces el sistema debe enviar un mensaje de WhatsApp al número registrado del cliente con la información del servicio, fecha, hora y barbero asignado.

7. Dado que la cita está programada, entonces el sistema debe enviar un mensaje de WhatsApp recordatorio al cliente 24 horas antes de la cita.

8. Si el horario seleccionado ya fue reservado por otro cliente, el sistema debe impedir la reserva y mostrar un mensaje indicando que el horario no está disponible.

---

### HU-20 – Requiero cancelar una cita programada

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero cancelar una cita programada
- **Para / Razón:** Para liberar el horario si no puedo asistir.
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que el cliente tiene una o más citas registradas, cuando accede a la sección “Mis citas”, entonces el sistema debe mostrar la lista de citas programadas.

2. Dado que el cliente selecciona una cita programada, cuando presiona la opción “Cancelar cita”, entonces el sistema debe solicitar confirmación antes de realizar la cancelación.

3. Dado que el cliente confirma la cancelación con más de 1 hora de anticipación, entonces el sistema debe: cambiar el estado de la cita a cancelada, liberar el horario en la agenda del barbero, realizar la devolución correspondiente del pago si aplica.

4. Dado que el cliente cancela la cita con 1 hora o menos de anticipación, entonces el sistema debe: cambiar el estado de la cita a cancelada, realizar una devolución parcial del dinero al cliente, registrar la parte restante como compensación por cancelación tardía.

5. Dado que la cancelación fue realizada correctamente, entonces el sistema debe mostrar un mensaje de confirmación al cliente.

6. Dado que una cita fue cancelada, entonces el horario correspondiente debe volver a aparecer como disponible en la agenda del barbero.

---

### HU-21 – Requiero reprogramar una cita existente

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero reprogramar una cita existente
- **Para / Razón:** Para cambiar el horario si surge algún inconveniente.
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el cliente tiene una cita registrada, cuando accede a la sección “Mis citas”, entonces el sistema debe mostrar la opción “Reprogramar cita”.

2. Dado que el cliente selecciona la opción reprogramar cita, entonces el sistema debe mostrar los horarios disponibles del barbero.

3. Dado que el cliente selecciona un nuevo horario disponible, cuando confirma el cambio, entonces el sistema debe actualizar la fecha y hora de la cita.

4. Dado que la cita ya tenía un pago registrado, entonces el sistema debe mantener ese pago asociado a la nueva fecha de la cita, sin realizar devolución de dinero.

5. Dado que la cita fue reprogramada correctamente, entonces el sistema debe mostrar un mensaje de confirmación con los nuevos datos de la cita.

6. Dado que la cita fue modificada, entonces el sistema debe enviar un mensaje de WhatsApp al cliente con la información actualizada de la cita.

7. Dado que faltan menos de 1 hora para la cita, entonces el sistema no debe permitir reprogramarla y debe mostrar un mensaje indicando que debe comunicarse con el establecimiento.

8. El horario previamente reservado debe volver a aparecer como disponible en la agenda del barbero.

---

### HU-22 – Requiero consultar el historial de mis citas

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero consultar el historial de mis citas
- **Para / Razón:** Para tener registro de los servicios recibidos.
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el cliente accede a su perfil, cuando selecciona historial de citas, entonces el sistema debe mostrar la lista de citas realizadas.

2. Dado que existen citas registradas, entonces el sistema debe mostrar la fecha, el servicio, el barbero y el estado de la cita.

3. Si el cliente no tiene citas registradas, entonces el sistema debe mostrar un mensaje indicando que no existen citas en el historial.

---

### HU-23 – Requiero pagar mi cita en línea

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero pagar mi cita en línea
- **Para / Razón:** Para confirmar mi reserva sin tener que pagar en el establecimiento.
- **Peso (story points):** 40
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el cliente confirma una cita, entonces el sistema debe mostrar la opción pagar en línea.

2. Dado que el cliente selecciona pagar en línea, entonces el sistema debe redirigir a una pasarela de pago.

3. Dado que el pago se realiza correctamente, entonces el sistema debe marcar la cita como pagada.

4. Dado que el pago fue confirmado, entonces el sistema debe enviar un mensaje de WhatsApp de confirmación de pago al cliente.

Caso Alterno: Pago no exitoso
1.El sistema deberá mostrar un mensaje si la transacción no fue exitosa.

---

### HU-24 – Requiero confirmar mi asistencia a la cita

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero confirmar mi asistencia a la cita
- **Para / Razón:** Para informar al establecimiento que asistiré al servicio reservado.
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que un cliente tiene una cita programada, cuando faltan 24 horas para la cita, entonces el sistema debe enviar un mensaje de WhatsApp recordatorio con la información de la cita.

2. Dado que el cliente recibe el mensaje de WhatsApp recordatorio, entonces el mensaje debe incluir un enlace con la opción “Confirmar asistencia”.

3. Dado que el cliente selecciona la opción confirmar asistencia, entonces el sistema debe actualizar el estado de la cita a “confirmada”.

4. Dado que el cliente no confirma la asistencia, entonces la cita debe mantenerse programada en la agenda del barbero.

5. Dado que el cliente accede a su cita desde la plataforma, entonces debe poder visualizar el estado actual de la cita (programada o confirmada).

---

### HU-26 – Requiero visualizar la agenda diaria de citas

- **Rol:** Yo como recepcionista
- **Quiero / Necesito:** Requiero visualizar la agenda diaria de citas
- **Para / Razón:** Para tener control de los servicios programados durante el día.
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de agenda, cuando selecciona una fecha, entonces el sistema debe mostrar las citas programadas para ese día.

2. Dado que existen citas registradas, entonces el sistema debe mostrar el horario, el cliente, el servicio y el barbero asignado.

3. Si no existen citas programadas para ese día, entonces el sistema debe mostrar un mensaje indicando que no hay citas registradas.

---

### HU-27 – Requiero marcar una cita como finalizada

- **Rol:** Yo como barbero
- **Quiero / Necesito:** Requiero marcar una cita como finalizada
- **Para / Razón:** Para indicar que el servicio fue realizado.
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que el barbero accede a su agenda de citas, cuando selecciona una cita programada, entonces el sistema debe mostrar la opción finalizar cita.

2. Dado que el barbero confirma la finalización, entonces el sistema debe actualizar el estado de la cita a finalizada.

3. Dado que la cita está finalizada, entonces debe quedar disponible para registro de pago por parte de la recepcionista.

---

### HU-28 – Requiero cerrar sesion

- **Rol:** Yo como Usuario(Administrador, Recepcionista, Empleado, cliente)
- **Quiero / Necesito:** Requiero cerrar sesion
- **Para / Razón:** Para garantizar la seguridad del sistema
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** W – Won't have / Wish (Futuro)

**Criterios de aceptación:**

1. El Usuario debe completar correctamente el inicio de sesión.

2. En el modulo del usuario debe existir un boton cerrar sesión.

3. Al hacer click en este boton, el sistema limpiara la cookie de sesión relacionada al usuario.

4. El sistema redireccionara el usuario a la pagina principal.

5. El sistema no permitira retornar a la pagina anterior y en su lugar lo dirigira a la ventana del login.

---

### HU-29 – Requiero colocar un porcentaje variable por servicio

- **Rol:** Yo como Administrador
- **Quiero / Necesito:** Requiero colocar un porcentaje variable por servicio
- **Para / Razón:** para definir la cantidad de ganancia que le corresponde al empleado y al negocio
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. El administrador ingresa al modulo de administrador.

2. El administrador ingresa a  la pestaña servicios.

3. El administrador tendrá dentro de cada servicio una opción de porcentaje el cual será variable y mostrará con base en el mismo cuánto le corresponde al empleado.

4. Una vez ingresado el porcentaje, habrá un botón de guardar, el cual registrará el porcentaje asignado a ese servicio.

---

### HU-30 – Requiero seleccionar los servicios que trabajó un empleado

- **Rol:** Yo como Administrador
- **Quiero / Necesito:** Requiero seleccionar los servicios que trabajó un empleado
- **Para / Razón:** para pagar diario o semanal a un empleado
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador ingresa al modulo de administrador.

2. El administrador seleccionará en el modulo la opción de pagar empleado.

3. El administrador seleccionará los servicios realizados por ese trabajador y, al lado tendrá una casilla que podrá multiplicar la cantidad de veces que realizó ese servicio en el día o semana.

4. El sistema mostrará el total a pagar al final de la selección de los servicios.

5.El sistema almacenará el registro del pago en la base de datos.

---

### HU-33 – Requiero autenticarme o registrarme en el momento de reservar una cita

- **Rol:** Yo como cliente
- **Quiero / Necesito:** Requiero autenticarme o registrarme en el momento de reservar una cita
- **Para / Razón:** para poder gestionar mi reserva y recibir notificaciones por WhatsApp
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** M – Must (Imprescindible)

**Criterios de aceptación:**

1. Dado que el cliente navega por la landing page (catálogo de categorías y servicios), el sistema no debe solicitarle autenticación.

2. Dado que el cliente selecciona "Reservar cita", entonces el sistema debe solicitarle iniciar sesión con Google.

3. Dado que el cliente inicia sesión con Google por primera vez, entonces el sistema debe mostrar un formulario para completar su registro con nombre, número de WhatsApp (tipo numérico, no mayor a 12 caracteres) y correo electrónico.

4. Dado que el cliente ya tiene una cuenta registrada, entonces el sistema debe reconocer su cuenta y continuar directamente con el proceso de reserva sin solicitar nuevamente sus datos.

5. Dado que el cliente completa el registro correctamente, entonces el sistema debe guardar su información y continuar con el flujo de selección de cita.

6. Dado que el cliente está autenticado, entonces el sistema debe permitir acceder a las secciones "Mis citas" e "Historial" únicamente con la cuenta autenticada.

Casos Alternos:
1. Si ocurre un error durante la autenticación con Google, el sistema debe mostrar un mensaje indicando que no fue posible iniciar sesión y permitir reintentar.

---

### HU-34 – Requiero consultar los datos de un cliente

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero consultar los datos de un cliente
- **Para / Razón:** para verificar su información antes de agendar o atender una cita
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de clientes, cuando selecciona consultar cliente, entonces el sistema debe mostrar un campo de búsqueda por nombre, número de WhatsApp o correo.

2. Dado que la recepcionista ingresa un criterio de búsqueda y confirma, entonces el sistema debe buscar en la base de datos el cliente correspondiente.

3. Dado que el cliente existe, entonces el sistema debe mostrar su información (nombre, número de WhatsApp, correo electrónico e historial de citas asociado).

4. Si el cliente no existe, el sistema debe mostrar un mensaje indicando que no se encontró ningún cliente con esos datos.

5. La recepcionista podrá realizar una nueva búsqueda y/o cerrar el formulario de consulta.

---

### HU-35 – Requiero editar los datos de un cliente

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero editar los datos de un cliente
- **Para / Razón:** para corregir información mal digitada (nombre, número de WhatsApp o correo)
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista consulta un cliente registrado, cuando selecciona la opción editar, entonces el sistema debe mostrar un formulario precargado con los datos actuales del cliente (nombre, número de WhatsApp, correo electrónico).

2. El formulario debe permitir modificar: nombre (tipo alfabético, no mayor a 100 caracteres), número de WhatsApp (tipo numérico, no mayor a 12 caracteres) y correo electrónico (alfanumérico con caracteres especiales, no mayor a 50 caracteres).

3. Dado que la recepcionista modifica uno o varios campos y confirma, entonces el sistema debe validar los formatos y restricciones antes de guardar los cambios.

4. Dado que los datos se actualizan correctamente, entonces el sistema debe guardar los cambios en la base de datos y mostrar un mensaje de confirmación.

5. Dado que existen citas asociadas al cliente, entonces el sistema debe mantener dichas asociaciones tras la edición.

Casos Alternos:
1. Si se deja un campo obligatorio vacío, el sistema debe mostrar una alerta indicando que el campo es obligatorio y no permitir guardar.
2. Si el nuevo número de WhatsApp o correo ya pertenece a otro cliente registrado, el sistema debe mostrar una alerta indicando que esos datos ya están en uso.
3. Si se supera el límite de caracteres permitido, el sistema debe mostrar una alerta indicando el máximo permitido.
4. Si ocurre un error durante la actualización, el sistema debe mostrar una alerta informando que la edición no pudo completarse y solicitar reintentar.

---

### HU-36 – Requiero consultar la información de un empleado

- **Rol:** Yo, como Administrador
- **Quiero / Necesito:** Requiero consultar la información de un empleado
- **Para / Razón:** para conocer sus datos de contacto, rol y estado
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. El administrador debe completar correctamente el inicio de sesión para acceder al módulo de administración.

2. La ventana debe tener un botón que permita regresar a la página principal.

3. El administrador seleccionará la opción "Empleados".

4. El administrador digitará el nombre del empleado para consultar sus datos.

5. La aplicación buscará en la base de datos el registro solicitado.

6. El sistema mostrará la información del empleado (nombre, rol, número de WhatsApp, correo y estado activo/inactivo).

7. Si el registro no existe, la aplicación mostrará un mensaje de error indicando que el empleado no existe en la base de datos.

8. El administrador podrá realizar una nueva búsqueda y/o cerrar el formulario de consulta.

---

### HU-37 – Requiero editar los datos de un empleado

- **Rol:** Yo como Administrador
- **Quiero / Necesito:** Requiero editar los datos de un empleado
- **Para / Razón:** para corregir información que se digitó mal
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que el administrador consulta un empleado registrado, cuando selecciona la opción editar, entonces el sistema debe mostrar un formulario precargado con los datos actuales (nombre, rol, número de WhatsApp, correo).

2. El formulario debe permitir modificar: nombre (tipo alfabético, no mayor a 100 caracteres), rol (Administrador, Recepcionista o Barbero/Empleado), número de WhatsApp (tipo numérico, no mayor a 12 caracteres) y correo electrónico (no mayor a 50 caracteres).

3. Dado que el administrador modifica los datos y confirma, entonces el sistema debe validar los formatos y restricciones antes de actualizar la información.

4. Dado que los datos se actualizan correctamente, entonces el sistema debe guardar los cambios y mostrar un mensaje de confirmación.

5. Dado que el rol del empleado fue modificado, entonces el sistema debe actualizar los permisos de acceso correspondientes a partir del próximo inicio de sesión.

Casos Alternos:
1. Si se deja un campo obligatorio vacío, el sistema debe mostrar una alerta indicando que el campo es obligatorio y no permitir guardar.
2. Si el correo ingresado ya está registrado para otro empleado, el sistema debe mostrar una alerta indicando que ya está en uso.
3. Si se supera el límite de caracteres permitido en algún campo, el sistema debe mostrar una alerta indicando el máximo permitido.
4. Si ocurre un error durante la actualización, el sistema debe mostrar una alerta informando que la edición no pudo completarse y solicitar reintentar.

---

### HU-38 – Requiero registrar una cita para un cliente que llega sin reserva previa (presencial)

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero registrar una cita para un cliente que llega sin reserva previa (presencial)
- **Para / Razón:** para asignarle un horario disponible con un barbero
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de agenda, cuando selecciona "Nueva cita presencial", entonces el sistema debe mostrar un buscador para identificar al cliente.

2. Dado que el cliente ya está registrado, el sistema debe permitir seleccionarlo desde la búsqueda; si no está registrado, el sistema debe permitir registrarlo con nombre, número de WhatsApp y correo electrónico.

3. Dado que el cliente está identificado, entonces el sistema debe mostrar los barberos disponibles, sus horarios libres y los servicios ofertados.

4. Dado que la recepcionista selecciona un barbero, un servicio y un horario disponible, cuando confirma, entonces el sistema debe registrar la cita en la agenda del barbero con estado "programada".

5. Dado que la cita fue registrada correctamente, entonces el sistema debe mostrar un mensaje de confirmación.

6. Si el horario seleccionado ya fue ocupado, el sistema debe impedir el registro y mostrar un mensaje indicando que el horario no está disponible.

---

### HU-39 – Requiero consultar el historial de pagos del día y anular un pago registrado por error

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero consultar el historial de pagos del día y anular un pago registrado por error
- **Para / Razón:** para llevar un cierre de caja correcto y corregir errores de digitación
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** S – Should (Importante)

**Criterios de aceptación:**

1. Dado que la recepcionista accede al módulo de pagos, cuando selecciona "Consultar pagos del día", entonces el sistema debe mostrar el listado de pagos registrados con fecha, servicio, cliente, medio de pago y valor.

2. Dado que la recepcionista selecciona un pago registrado por error, cuando elige "Anular", entonces el sistema debe solicitar confirmación.

3. Dado que la recepcionista confirma la anulación, entonces el sistema debe cambiar el estado del pago a "anulado" y excluirlo del total de ingresos del cierre de caja, manteniendo el registro en la base de datos para auditoría.

4. Dado que se consulta el cierre de caja, entonces el sistema debe mostrar: base inicial registrada, total de ingresos válidos (sin pagos anulados) y el total esperado en caja.

5. Si se intenta anular un pago que ya fue anulado previamente, el sistema debe mostrar un mensaje indicando que el pago ya se encuentra anulado.

---

### HU-40 – Requiero bloquear un día u horario puntual de mi agenda

- **Rol:** Yo como Barbero
- **Quiero / Necesito:** Requiero bloquear un día u horario puntual de mi agenda
- **Para / Razón:** para indicar que no estaré disponible (ej. incapacidad o permiso)
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el barbero accede a su agenda, cuando selecciona "Bloquear horario", entonces el sistema debe mostrar un calendario para elegir el día u horario a bloquear.

2. Dado que el barbero selecciona un rango sin citas agendadas y confirma, entonces el sistema debe marcar ese horario como no disponible para los clientes y la recepcionista.

3. Si existen citas agendadas en el horario que se intenta bloquear, el sistema debe mostrar el mensaje "hay clientes agendados, por favor comuníquese con la recepcionista para reasignar las citas" y no permitir el bloqueo.

4. Dado que el barbero desea liberar un horario bloqueado previamente, entonces el sistema debe permitir desbloquearlo y volver a mostrarlo como disponible.

---

### HU-41 – Requiero consultar reportes de ingresos y desempeño del negocio

- **Rol:** Yo como Administrador
- **Quiero / Necesito:** Requiero consultar reportes de ingresos y desempeño del negocio
- **Para / Razón:** para tomar decisiones sobre el negocio
- **Peso (story points):** 20
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de reportes, entonces el sistema debe permitir filtrar la información por rango de fechas (día, semana o mes).

2. Dado que el administrador selecciona un rango de fechas, entonces el sistema debe mostrar el total de ingresos generados en ese periodo (excluyendo pagos anulados).

3. El sistema debe mostrar un listado de los servicios más solicitados en el periodo seleccionado, ordenados de mayor a menor.

4. El sistema debe mostrar, por cada barbero, el número de citas atendidas y el monto generado en el periodo seleccionado.

5. Si no existen datos para el periodo seleccionado, el sistema debe mostrar un mensaje indicando que no hay información disponible.

---

### HU-42 – Requiero ver notificaciones de la actividad del sistema

- **Rol:** Yo como Usuario (Administrador, Recepcionista)
- **Quiero / Necesito:** Requiero ver notificaciones de la actividad del sistema
- **Para / Razón:** para estar informado de nuevas citas, cancelaciones y cambios
- **Peso (story points):** 13
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el usuario inicia sesión, entonces el sistema debe mostrar un ícono de notificaciones con la cantidad de notificaciones no leídas.

2. Dado que se registra, cancela o reprograma una cita, o se activa/desactiva un servicio o categoría, entonces el sistema debe generar una notificación visible para el administrador y/o la recepcionista, según corresponda.

3. Dado que el usuario selecciona el ícono de notificaciones, entonces el sistema debe mostrar el listado de notificaciones ordenadas de la más reciente a la más antigua.

4. Dado que el usuario revisa una notificación, entonces el sistema debe marcarla como leída.

5. Si no existen notificaciones, el sistema debe mostrar un mensaje indicando que no hay notificaciones nuevas.

---

### HU-43 – Requiero consultar el historial de pagos realizados a un empleado

- **Rol:** Yo como Administrador
- **Quiero / Necesito:** Requiero consultar el historial de pagos realizados a un empleado
- **Para / Razón:** para llevar control de los pagos efectuados y evitar duplicados
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** C – Could (Deseable)

**Criterios de aceptación:**

1. Dado que el administrador accede al módulo de pago a empleados, cuando selecciona un empleado, entonces el sistema debe mostrar el historial de pagos realizados (fecha, periodo, servicios incluidos y monto total).

2. Dado que el administrador filtra por rango de fechas, entonces el sistema debe mostrar únicamente los pagos realizados dentro de ese rango.

3. Si el empleado no tiene pagos registrados, el sistema debe mostrar un mensaje indicando que no existen pagos registrados para ese empleado.

4. Dado que el administrador selecciona un pago del historial, entonces el sistema debe mostrar el detalle de los servicios y cantidades que lo componen.

---

### HU-44 – Requiero editar mi perfil

- **Rol:** Yo como Usuario (Administrador, Recepcionista, Barbero, Cliente)
- **Quiero / Necesito:** Requiero editar mi perfil
- **Para / Razón:** para mantener actualizados mis datos de contacto y mi foto
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** W – Won't have / Wish (Futuro)

**Criterios de aceptación:**

1. Dado que el usuario inicia sesión, cuando accede a la opción "Mi perfil", entonces el sistema debe mostrar un formulario precargado con sus datos actuales (nombre, número de WhatsApp/teléfono, correo y foto de perfil).

2. El formulario debe permitir modificar: nombre (tipo alfabético, no mayor a 100 caracteres), número de WhatsApp/teléfono (tipo numérico, no mayor a 12 caracteres) y foto de perfil (tipo imagen jpg, png). El correo electrónico no podrá modificarse ya que está vinculado a la cuenta de Google.

3. Dado que el usuario modifica uno o varios campos y confirma, entonces el sistema debe validar los formatos y restricciones antes de guardar los cambios.

4. Dado que los datos se actualizan correctamente, entonces el sistema debe guardar los cambios y mostrar un mensaje de confirmación.

Casos Alternos:
1. Si se deja un campo obligatorio vacío, el sistema debe mostrar una alerta indicando que el campo es obligatorio y no permitir guardar.
2. Si se supera el límite de caracteres permitido en el nombre o el teléfono/WhatsApp, el sistema debe mostrar una alerta indicando el máximo permitido.
3. Si el usuario intenta cargar una imagen con un formato distinto a jpg o png, el sistema debe mostrar una alerta indicando los formatos válidos y rechazar el archivo.
4. Si ocurre un error durante la actualización, el sistema debe mostrar una alerta informando que la edición no pudo completarse y solicitar reintentar.

---

### HU-45 – Requiero desactivar un cliente

- **Rol:** Yo como Recepcionista
- **Quiero / Necesito:** Requiero desactivar un cliente
- **Para / Razón:** para que no aparezca en las búsquedas activas (ej. registro duplicado o solicitud de eliminación de datos)
- **Peso (story points):** 8
- **Prioridad (MoSCoW):** W – Won't have / Wish (Futuro)

**Criterios de aceptación:**

1. Dado que la recepcionista consulta un cliente registrado, cuando selecciona la opción "Desactivar", entonces el sistema debe mostrar un cuadro de confirmación.

2. Dado que la recepcionista confirma la desactivación, entonces el sistema debe cambiar el estado del cliente a "inactivo", ocultándolo de las búsquedas activas pero manteniendo su información y su historial de citas en la base de datos.

3. Si la recepcionista presiona "No" en el cuadro de confirmación, el sistema debe regresar a la pantalla anterior sin realizar cambios.

4. Dado que un cliente está desactivado, entonces no debe poder iniciar sesión ni reservar nuevas citas; si lo intenta, el sistema debe mostrar un mensaje indicando que debe comunicarse con el establecimiento.

5. El administrador o la recepcionista podrán reactivar un cliente desactivado siguiendo el mismo proceso.

---
