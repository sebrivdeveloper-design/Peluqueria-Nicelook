import api from './axiosInstance'

export function registrarBase(montoBase) {
  return api.post('/caja', { montoBase: String(montoBase) })
}

export function getBaseHoy() {
  return api.get('/caja/hoy')
}

export function getCierre(fecha) {
  return api.get('/caja/cierre', { params: fecha ? { fecha } : {} })
}
