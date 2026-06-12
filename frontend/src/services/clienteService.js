import api from './axiosInstance'

export default {
  getClientes() {
    return api.get('/clientes')
  },

  registrarCliente(cliente) {
    return api.post('/clientes', cliente)
  }
}
