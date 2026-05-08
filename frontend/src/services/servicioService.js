import axios from 'axios'

const API = 'http://localhost:8080/api'

function getAuthHeader() {
  const token = localStorage.getItem("token")
  
   if (!token) {
    return {}; // 🔥 NO envía Authorization
  }
  
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export default {

  // CLIENTE (solo activos)
  getServicios() {
    return axios.get(`${API}/servicios`, getAuthHeader())
  },

  // ADMIN (todos)
  getServiciosAdmin() {
    return axios.get(`${API}/servicios/admin`, getAuthHeader())
  },

  crearServicio(data) {
    return axios.post(`${API}/servicios`, data, getAuthHeader())
  },

  // DESACTIVAR
  deshabilitar(id) {
    return axios.delete(`${API}/servicios/${id}`, getAuthHeader())
  },

  // ACTIVAR
  activar(id) {
    return axios.put(`${API}/servicios/activar/${id}`, {}, getAuthHeader())
  },

  getCategorias() {
    return axios.get(`${API}/categorias`, getAuthHeader())
  }
}
