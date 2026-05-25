<template>
  <div class="container">

    <h1>Mis citas</h1>

    <div v-if="citas.length === 0" class="empty">
      No tienes citas registradas
    </div>

    <div v-else class="cards">

      <div
        class="card"
        v-for="(cita, index) in citas"
        :key="index"
      >
        <h3>{{ cita.servicio }}</h3>

        <p><strong>Fecha:</strong> {{ cita.fecha }}</p>

        <p><strong>Barbero:</strong> {{ cita.barbero }}</p>

        <p><strong>Estado:</strong> {{ cita.estado }}</p>
      </div>

    </div>

  </div>
</template>

<script>
import { getHistorial } from '../services/historialService'

export default {

  data() {
    return {
      citas: []
    }
  },

  async mounted() {

    try {

      // 🔥 TEMPORAL
      const idCliente = 4

      const res = await getHistorial(idCliente)

      this.citas = res.data

    } catch (error) {

      console.error("Error cargando historial:", error)

    }
  }
}
</script>

<style scoped>
.container {
  padding: 40px;
}

h1 {
  margin-bottom: 30px;
  color: #004518;
}

.cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.card {
  width: 300px;
  background: #dce6d7;
  border-radius: 20px;
  padding: 20px;
}

.card h3 {
  margin-bottom: 15px;
  color: #004518;
}

.empty {
  color: #777;
  font-style: italic;
}
</style>