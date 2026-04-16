<template>

  <HeaderBar 
  @crear="mostrarModal = true"
  textoBoton="Crear empleado"
/>


  <h2>EMPLEADOS</h2>

  <div class="grid">
    <div v-for="emp in empleados" :key="emp.idEmpleado" class="card">
      
      <h3>{{ emp.usuario.nombreCompleto }}</h3>
      <p>{{ emp.usuario.correo }}</p>
      <p>{{ emp.documento }}</p>
      <p>{{ emp.especialidad }}</p>
      <p>${{ emp.salario }}</p>

    </div>
  </div>

  <EmpleadoModal
    v-if="mostrarModal"
    @cerrar="mostrarModal = false"
    @actualizar="cargarEmpleados"
  />

</template>

<script>
import HeaderBar from '../components/HeaderBar.vue'
import { getEmpleados } from '../services/empleadoService'
import EmpleadoModal from '../components/EmpleadoModal.vue'

export default {
  components: { EmpleadoModal, HeaderBar },

  data() {
    return {
      empleados: [],
      mostrarModal: false
    }
  },

  mounted() {
    this.cargarEmpleados()
  },

  methods: {
    async cargarEmpleados() {
      try {
        const res = await getEmpleados()
        this.empleados = res.data
      } catch (error) {
        console.error(error)
        alert("Error cargando empleados")
      }
    }
  }
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.card {
  background: #dce6d7;
  padding: 15px;
  border-radius: 15px;
}
</style>
