import axios from 'axios'

const API = 'http://localhost:8080/api'

function getAuthHeader() {
  const token = localStorage.getItem("token")
  
  if (!token) return {}
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export default {
  // Obtener horarios disponibles de un barbero en una fecha
  getDisponibilidad(idEmpleado, fecha) {
    return axios.get(`${API}/citas/disponibilidad/${idEmpleado}`, {
      params: { fecha },
      ...getAuthHeader()
    })
  },

  // Obtener todos los empleados (barberos)
  getEmpleados() {
    return axios.get(`${API}/empleados`, getAuthHeader())
  },

  // Registrar una cita
  registrarCita(data) {
    return axios.post(`${API}/citas`, data, getAuthHeader())
  },

  getHorariosDisponibles(idEmpleado, fecha, idServicio) {

  return axios.get(
    `${API}/citas/horarios-disponibles`,
    {
      params: {
        idEmpleado,
        fecha,
        idServicio
      },
      ...getAuthHeader()
    }
  )
}
}