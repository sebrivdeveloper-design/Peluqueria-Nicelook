import api from './axiosInstance'

export const crearEmpleado = (data) =>
  api.post('/empleados', data)

export const editarEmpleado = (id, data) =>
  api.put(`/empleados/${id}`, data)

export const getEmpleados = () =>
  api.get('/empleados')
