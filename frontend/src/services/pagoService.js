import api from './axiosInstance'

export function getCitasPorCobrar(fecha) {
  return api.get('/pagos/pendientes', { params: fecha ? { fecha } : {} })
}

export function registrarPago(idCita, metodoPago) {
  return api.post('/pagos', { idCita: String(idCita), metodoPago })
}

export function getPagosDelDia(fecha) {
  return api.get('/pagos/dia', { params: fecha ? { fecha } : {} })
}

export function anularPago(idPago) {
  return api.put(`/pagos/${idPago}/anular`)
}
