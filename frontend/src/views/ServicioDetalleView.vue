<template>
  <section class="detalle-page" v-if="servicio">

    <div class="acciones-top">
      <button class="btn-volver" @click="$router.back()">
        ← Volver
      </button>

      <button class="btn-editar" @click="editar = true">
        <span class="icono">✏️</span>
        Editar servicio
      </button>
    </div>


    <div v-if="editar" class="modal-overlay" @click.self="editar = false">
      <div class="modal">

        <h2>Editar servicio</h2>

        <input v-model="form.nombreServicio" placeholder="Nombre" />
        <span class="error">{{ errores.nombreServicio }}</span>

        <textarea v-model="form.descripcion" placeholder="Descripción"></textarea>
        <span class="error">{{ errores.descripcion }}</span>

        <input v-model="form.duracion" placeholder="Duración" />
        <span class="error">{{ errores.duracion }}</span>

        <input v-model="form.precio" type="number" placeholder="Precio" />
        <span class="error">{{ errores.precio }}</span>

        <div class="acciones">
          <button class="btn-cancelar" @click="editar = false">Cancelar</button>
          <button class="btn-guardar" @click="guardarCambios" :disabled="cargando">
            {{ cargando ? "Guardando..." : "Guardar" }}
          </button>
        </div>

      </div>
    </div>




    <div class="card">
      <h1>{{ servicio.nombreServicio }}</h1>

      <p class="descripcion">
        {{ servicio.descripcion }}
      </p>

      <div class="info">
        <span>💲 Precio: {{ servicio.precio }}</span>
        <span>⏱ Duración: {{ servicio.duracion }}</span>
      </div>

      <div class="categoria">
        Categoría: {{ servicio.categoria.nombreCategoria }}
      </div>

      <span 
        class="badge"
        :class="servicio.estado === 'activo' ? 'Activo' : 'Inactivo'"
      >
        {{ servicio.estado === 'activo' ? 'Activo' : 'Inactivo' }}
      </span>

    </div>

    <div 
      v-if="notificacion.visible"
      class="toast"
      :class="notificacion.tipo"
    >
      {{ notificacion.mensaje }}
    </div>

  </section>
  

</template>

<script>
import servicioService from '@/services/servicioService'

export default {
  data() {
    return {
      servicio: null,
      editar: false,
      form: {
        nombreServicio: "",
        descripcion: "",
        duracion: "",
        precio: "",
      },
      errores: {
        nombreServicio: "",
        descripcion: "",
        duracion: "",
        precio: "",
      },
      notificacion: {
        visible: false,
        mensaje: "",
        tipo: "success"
      },
      cargando: false
    }
  },

  methods: {
    async guardarCambios() {

      if (!this.validar()) return

      const confirmar = confirm("¿Seguro que deseas guardar los cambios?")
      if (!confirmar) return

      this.cargando = true

      try {
        await servicioService.actualizar(this.servicio.idServicio, this.form)

        this.mostrarNotificacion("✅ Servicio actualizado correctamente", "success")

        this.editar = false

        const res = await servicioService.getById(this.servicio.idServicio)
        this.servicio = res.data

      } catch (error) {
        console.error(error)
        this.mostrarNotificacion("❌ Error al actualizar", "error")
      } finally {
        this.cargando = false
      }
    },

    validar() {
      let ok = true

      this.errores = {
        nombreServicio: "",
        descripcion: "",
        duracion: "",
        precio: ""
      }

      // NOMBRE
      if (!this.form.nombreServicio || this.form.nombreServicio.trim() === "") {
        this.errores.nombreServicio = "El nombre es un campo obligatorio"
        ok = false
      } else if (this.form.nombreServicio.length > 30) {
        this.errores.nombreServicio = "Máximo 30 caracteres"
        ok = false
      }

      // DESCRIPCIÓN
      if (!this.form.descripcion || this.form.descripcion.trim() === "") {
        this.errores.descripcion = "Este campo es obligatorio"
        ok = false
      } else if (this.form.descripcion.length > 500) {
        this.errores.descripcion = "Máximo 500 caracteres"
        ok = false
      }

      // DURACIÓN
      if (!this.form.duracion || this.form.duracion.trim() === "") {
        this.errores.duracion = "Este campo es obligatorio"
        ok = false
      } else if (this.form.duracion.length > 30) {
        this.errores.duracion = "Máximo 30 caracteres"
        ok = false
      }

      // PRECIO
      if (!this.form.precio) {
        this.errores.precio = "Este campo es obligatorio"
        ok = false
      } else if (this.form.precio <= 0) {
        this.errores.precio = "Debe ser mayor a 0"
        ok = false
      }

      return ok
    },

    mostrarNotificacion(mensaje, tipo = "success") {
      this.notificacion.mensaje = mensaje
      this.notificacion.tipo = tipo
      this.notificacion.visible = true

      setTimeout(() => {
        this.notificacion.visible = false
      }, 3000)
    }



  },


  async mounted() {
    const id = this.$route.params.id

    console.log("🟡 ID recibido:", id)

    try {
      const res = await servicioService.getById(id)

      console.log("🟢 RESPUESTA BACKEND:", res)
      console.log("🟢 DATA:", res.data)

      this.servicio = res.data
      this.form = {
        nombreServicio: res.data.nombreServicio,
        descripcion: res.data.descripcion,
        duracion: res.data.duracion,
        precio: res.data.precio,
      }
    } catch (error) {
      console.error("🔴 ERROR:", error)

      this.mostrarNotificacion("Servicio no encontrado", "error")

      setTimeout(() => {
        this.$router.push("/admin/servicios")
      }, 1500)
    }

  }
}
</script>

<style scoped>
.detalle-page {
  padding: 20px;
}

.btn-volver {
  margin-bottom: 20px;
  background: #eee;
  border: none;
  padding: 10px;
  cursor: pointer;
}

.card {
  background: white;
  padding: 24px;
  border-radius: 18px;
  border: 1px solid #ddd;
}

.descripcion {
  margin-top: 10px;
}

.info {
  margin-top: 15px;
  display: flex;
  gap: 20px;
}

.categoria {
  margin-top: 10px;
  color: #555;
}

.badge {
  display: inline-block;
  margin-top: 15px;
  padding: 6px 12px;
  border-radius: 12px;
}

/* ACTIVO */
.badge.Activo {
  background: #e6f4ea;
  color: #1b5e20;
}

/* INACTIVO */
.badge.Inactivo {
  background: #fdecea;
  color: #b42318;
}

.error {
  color: red;
  font-size: 12px;
  margin-bottom: 8px;
  display: block;
}
/* FONDO OSCURO */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

/* CAJA MODAL */
.modal {
  background: white;
  padding: 24px;
  border-radius: 18px;
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  animation: fadeIn 0.2s ease;
}

/* ANIMACIÓN */
@keyframes fadeIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* INPUTS */
.modal input,
.modal textarea {
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #ccc;
  outline: none;
}

.modal input:focus,
.modal textarea:focus {
  border-color: #004518;
}

/* BOTONES */
.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.btn-cancelar {
  background: #eee;
  border: none;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
}

.btn-guardar {
  background: #004518;
  color: white;
  border: none;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
}

/* ERRORES */
.error {
  color: red;
  font-size: 12px;
}

/* CONTENEDOR SUPERIOR */
.acciones-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* BOTÓN VOLVER */
.btn-volver {
  background: #f1f1f1;
  border: none;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
}

.btn-volver:hover {
  background: #e0e0e0;
}

/* BOTÓN EDITAR (PRO) */
.btn-editar {
  display: flex;
  align-items: center;
  gap: 8px;

  background: linear-gradient(135deg, #004518, #0a6b2e);
  color: white;
  border: none;

  padding: 10px 16px;
  border-radius: 12px;

  font-size: 14px;
  font-weight: 600;

  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(0, 69, 24, 0.2);
}

.btn-editar:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(0, 69, 24, 0.3);
}

.btn-editar:active {
  transform: scale(0.98);
}

/* ICONO */
.icono {
  font-size: 14px;
}

/* TOAST */
.toast {
  position: fixed;
  bottom: 20px;
  right: 20px;

  padding: 14px 18px;
  border-radius: 12px;

  color: white;
  font-weight: 500;
  font-size: 14px;

  box-shadow: 0 6px 20px rgba(0,0,0,0.15);

  animation: slideIn 0.3s ease;
  z-index: 1000;
}

/* SUCCESS */
.toast.success {
  background: #1b5e20;
}

/* ERROR */
.toast.error {
  background: #b42318;
}

/* ANIMACIÓN */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}


</style>
