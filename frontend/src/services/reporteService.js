import api from './axiosInstance'

export function getReporte(desde, hasta) {
  return api.get('/reportes', { params: { desde, hasta } })
}

// Saldos de caja por día de un mes
export function getSaldosDiarios(anio, mes) {
  return api.get('/reportes/saldos-diarios', { params: { anio, mes } })
}

// Detalle de un día (servicios cobrados + totales)
export function getSaldosDia(fecha) {
  return api.get('/reportes/saldos-dia', { params: { fecha } })
}
