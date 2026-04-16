<template>
  <div class="overlay">
    <div class="modal">
      <button class="cerrar" @click="$emit('cerrar')">X</button>

      <h3>Registrar Empleado</h3>

      <input v-model="form.nombreCompleto" placeholder="Nombre completo" />
      <input v-model="form.correo" placeholder="Correo" />
      <input v-model="form.telefono" placeholder="Teléfono" />

      <select v-model="form.rol">
        <option disabled value="">Seleccione rol</option>
        <option value="ADMIN">Administrador</option>
        <option value="EMPLEADO">Empleado</option>
        <option value="RECEPCIONISTA">Recepcionista</option>
      </select>

      <input v-model="form.documento" placeholder="Documento" />
      <input v-model="form.especialidad" placeholder="Especialidad" />
      <input v-model="form.salario" type="number" placeholder="Salario" />

      <button @click="guardar">Guardar</button>
    </div>
  </div>
</template>


<script>
import { crearEmpleado } from '../services/empleadoService'

export default {
  data() {
    return {
      form: {
        nombreCompleto: '',
        correo: '',
        telefono: '',
        rol: '',
        documento: '',
        especialidad: '',
        salario: ''
      }
    }
  },

  methods: {
    async guardar() {
      try {
        // VALIDACIONES BÁSICAS
        if (!this.form.nombreCompleto || !this.form.correo || !this.form.rol) {
          alert("Campos obligatorios incompletos")
          return
        }

        await crearEmpleado(this.form)

        alert("Empleado creado correctamente")

        this.$emit('cerrar')
        this.$emit('actualizar')

      } catch (error) {
        console.error(error)
        alert(error.response?.data || "Error al crear empleado")
      }
    }
  }
}
</script>
<style scoped>
/* 🔥 AQUÍ VA EL CSS */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
}

.modal {
  background: #dce6d7;
  padding: 20px;
  width: 400px;
  margin: 100px auto;
  border-radius: 15px;
  position: relative;
}

input, textarea, select {
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
}

.cerrar {
  position: absolute;
  top: 10px;
  right: 10px;
}

@media (max-width: 600px) {
  .modal {
    width: 90%;
    margin: 50px auto;
  }
}
</style>