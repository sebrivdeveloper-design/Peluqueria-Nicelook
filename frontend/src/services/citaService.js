import api from './axiosInstance'

export default {
  // Horarios disponibles de un barbero en una fecha
  getDisponibilidad(idEmpleado, fecha) {
    return api.get(`/citas/disponibilidad/${idEmpleado}`, {
      params: { fecha }
    })
  },

  // Todos los empleados (barberos)
  getEmpleados() {
    return api.get('/empleados')
  },

  // Registrar una cita
  registrarCita(data) {
    return api.post('/citas', data)
  }
}
