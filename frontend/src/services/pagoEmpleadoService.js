import api from './axiosInstance'

export function getServiciosTrabajados(idEmpleado, desde, hasta) {
  return api.get(`/pagos-empleados/servicios-trabajados/${idEmpleado}`, {
    params: { desde, hasta }
  })
}

export function registrarPagoEmpleado(data) {
  return api.post('/pagos-empleados', data)
}

export function getHistorialPagos(idEmpleado) {
  return api.get(`/pagos-empleados/${idEmpleado}`)
}
