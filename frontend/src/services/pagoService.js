import api from './axiosInstance'

export function getCitasPorCobrar(fecha) {
  return api.get('/pagos/pendientes', { params: fecha ? { fecha } : {} })
}

export function registrarPago(idCita, metodoPago, confirmarExcepcion = false) {
  return api.post('/pagos', {
    idCita: String(idCita),
    metodoPago,
    confirmarExcepcion: String(confirmarExcepcion)
  })
}

export function getPagosDelDia(fecha) {
  return api.get('/pagos/dia', { params: fecha ? { fecha } : {} })
}

export function anularPago(idPago) {
  return api.put(`/pagos/${idPago}/anular`)
}
