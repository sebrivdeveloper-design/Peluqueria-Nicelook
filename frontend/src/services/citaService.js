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
  },

  // Citas de un cliente (mis citas + historial)
  getCitasCliente(idCliente) {
    return api.get(`/citas/cliente/${idCliente}`)
  },

  // Agenda diaria de citas (todos los barberos)
  getCitasDelDia(fecha) {
    return api.get('/citas/dia', { params: fecha ? { fecha } : {} })
  },

  // Confirmar asistencia
  confirmarCita(idCita) {
    return api.put(`/citas/${idCita}/confirmar`)
  },

  // Finalizar cita (barbero)
  finalizarCita(idCita) {
    return api.put(`/citas/${idCita}/finalizar`)
  },

  // Reprogramar cita
  reprogramarCita(idCita, data) {
    return api.put(`/citas/${idCita}/reprogramar`, data)
  },

  // Cancelar cita
  cancelarCita(idCita) {
    return api.delete(`/citas/${idCita}`)
  }
}
