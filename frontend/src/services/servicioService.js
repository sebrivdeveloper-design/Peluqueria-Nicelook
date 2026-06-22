import api from './axiosInstance'

export default {

  buscarServicios(texto) {
    return api.get('/servicios/buscar', {
      params: { texto }
    })
  },

  getServicios() {
    return api.get('/servicios')
  },

  getById(id) {
    return api.get(`/servicios/${id}`)
  },

  getServiciosAdmin() {
    return api.get('/servicios/admin')
  },

  crearServicio(data) {
    return api.post('/servicios', data)
  },

  actualizar(id, data) {
    return api.put(`/servicios/${id}`, data)
  },

  deshabilitar(id) {
    return api.delete(`/servicios/${id}`)
  },

  activar(id) {
    return api.put(`/servicios/activar/${id}`, {})
  },

  getCategorias() {
    return api.get('/categorias')
  }
}
