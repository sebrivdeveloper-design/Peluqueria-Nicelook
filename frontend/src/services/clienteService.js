import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api'
})

// Agrega el token automáticamente en cada petición
axiosInstance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const api = {
  // GET /api/clientes
  getClientes() {
    return axiosInstance.get('/clientes')
  },

  // POST /api/clientes
  registrarCliente(cliente) {
    return axiosInstance.post('/clientes', cliente)
  }
}

export default api