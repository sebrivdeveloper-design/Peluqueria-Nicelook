import api from './axiosInstance'

export function getReporte(desde, hasta) {
  return api.get('/reportes', { params: { desde, hasta } })
}
