import api from './axiosInstance'

export default {
  getClientes() {
    return api.get('/clientes')
  },

  registrarCliente(cliente) {
    return api.post('/clientes', cliente)
  },

  editarCliente(idCliente, cliente) {
    return api.put(`/clientes/${idCliente}`, cliente)
  },

  desactivarCliente(idCliente) {
    return api.put(`/clientes/${idCliente}/desactivar`)
  },

  activarCliente(idCliente) {
    return api.put(`/clientes/${idCliente}/activar`)
  }
}
