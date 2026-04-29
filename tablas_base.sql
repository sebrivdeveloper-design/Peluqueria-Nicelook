-- ========================
-- TABLAS BASE
-- ========================

CREATE TABLE rol (
  id_rol SERIAL PRIMARY KEY,
  nombre_rol VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuario (
  id_usuario SERIAL PRIMARY KEY,
  id_rol INTEGER NOT NULL,
  nombre_completo VARCHAR(150) NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  correo VARCHAR(100) UNIQUE NOT NULL,
  telefono VARCHAR(20),
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado TEXT DEFAULT 'activo' CHECK (estado IN ('activo','inactivo')),
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE categoria_servicios (
  id_categoria SERIAL PRIMARY KEY,
  nombre_categoria VARCHAR(100) NOT NULL,
  descripcion TEXT
);

-- ========================
-- EMPLEADO Y CLIENTE
-- ========================

CREATE TABLE empleado (
  id_empleado SERIAL PRIMARY KEY,
  id_usuario INTEGER UNIQUE NOT NULL,
  documento VARCHAR(30) UNIQUE NOT NULL,
  fecha_ingreso DATE,
  especialidad VARCHAR(100),
  salario DECIMAL(10,2),
  estado_laboral TEXT DEFAULT 'activo'
    CHECK (estado_laboral IN ('activo','inactivo','suspendido','vacaciones')),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE cliente (
  id_cliente SERIAL PRIMARY KEY,
  id_usuario INTEGER UNIQUE NOT NULL,
  fecha_nacimiento DATE,
  observaciones TEXT,
  genero TEXT CHECK (genero IN ('masculino','femenino','otro')),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- ========================
-- SERVICIOS
-- ========================

CREATE TABLE servicio (
  id_servicio SERIAL PRIMARY KEY,
  id_categoria INTEGER NOT NULL,
  nombre_servicio VARCHAR(100) NOT NULL,
  descripcion TEXT,
  duracion INTEGER NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  estado TEXT DEFAULT 'activo' CHECK (estado IN ('activo','inactivo')),
  FOREIGN KEY (id_categoria) REFERENCES categoria_servicios(id_categoria)
);

CREATE TABLE empleado_servicio (
  id_empleado INTEGER NOT NULL,
  id_servicio INTEGER NOT NULL,
  PRIMARY KEY (id_empleado, id_servicio),
  FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE,
  FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio) ON DELETE CASCADE
);

-- ========================
-- CITAS
-- ========================

CREATE TABLE cita (
  id_cita SERIAL PRIMARY KEY,
  id_cliente INTEGER NOT NULL,
  id_empleado INTEGER NOT NULL,
  fecha_cita DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  estado_cita TEXT DEFAULT 'pendiente'
    CHECK (estado_cita IN ('pendiente','confirmada','cancelada','completada','no_asistio')),
  observaciones TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
);

CREATE TABLE detalle_cita (
  id_detalle_cita SERIAL PRIMARY KEY,
  id_cita INTEGER NOT NULL,
  id_servicio INTEGER NOT NULL,
  duracion_aplicada INTEGER NOT NULL,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (id_cita) REFERENCES cita(id_cita) ON DELETE CASCADE,
  FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE disponibilidad (
  id_disponibilidad SERIAL PRIMARY KEY,
  id_empleado INTEGER NOT NULL,
  fecha DATE NOT NULL,
  hora_inicio_bloque TIME NOT NULL,
  hora_fin_bloque TIME NOT NULL,
  estado_bloque TEXT DEFAULT 'disponible'
    CHECK (estado_bloque IN ('disponible','ocupado','bloqueado','descanso')),
  FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE
);

-- ========================
-- HISTÓRICO Y PAGOS
-- ========================

CREATE TABLE historico_citas (
  id_historial SERIAL PRIMARY KEY,
  id_cita INTEGER NOT NULL,
  id_cliente INTEGER NOT NULL,
  id_empleado INTEGER NOT NULL,
  estado_final TEXT CHECK (estado_final IN ('pendiente','confirmada','cancelada','completada','no_asistio')),
  observaciones TEXT,
  FOREIGN KEY (id_cita) REFERENCES cita(id_cita) ON DELETE CASCADE,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
);

CREATE TABLE pago (
  id_pago SERIAL PRIMARY KEY,
  id_cita INTEGER UNIQUE NOT NULL,
  monto_total DECIMAL(10,2) NOT NULL,
  metodo_pago TEXT CHECK (metodo_pago IN ('efectivo','tarjeta','transferencia','billetera_digital')),
  referencia VARCHAR(100),
  estado_pago TEXT DEFAULT 'pendiente'
    CHECK (estado_pago IN ('pendiente','pagado','anulado','reembolsado')),
  fecha_pago TIMESTAMP,
  FOREIGN KEY (id_cita) REFERENCES cita(id_cita) ON DELETE CASCADE
);