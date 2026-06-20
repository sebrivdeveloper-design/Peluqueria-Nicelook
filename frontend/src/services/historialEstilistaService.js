import api from './axiosInstance'

// ADMIN: historial de cualquier estilista
export const getHistorialEstilista = (idEmpleado, desde, hasta) =>
  api.get(`/historial-estilistas/${idEmpleado}`, { params: { desde, hasta } })

// ESTILISTA: su propio historial (resuelto desde la sesión)
export const getMiHistorial = (desde, hasta) =>
  api.get('/historial-estilistas/mio', { params: { desde, hasta } })
