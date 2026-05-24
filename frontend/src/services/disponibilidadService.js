import api from './axiosInstance'

export function getDisponibilidad(idEmpleado, mes, anio) {
  return api.get(`/disponibilidad/${idEmpleado}`, {
    params: { mes, anio }
  })
}

export function crearDisponibilidad(data) {
  return api.post('/disponibilidad', data)
}

export function editarDisponibilidad(id, data) {
  return api.put(`/disponibilidad/${id}`, data)
}