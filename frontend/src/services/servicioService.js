import axios from 'axios'

const API = 'http://localhost:8080/api'

function getAuthHeader() {
  const token = localStorage.getItem("token")
  
  if (!token) {
    return {}; 
  }
  
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export default {

  
  buscarServicios(texto) {
    
    return axios.get(`${API}/servicios/buscar`, {
      params: { texto: texto },
      ...getAuthHeader()
    });
  },

  //MÉTODOS PARA CLIENTES

  
  getServicios() {
    return axios.get(`${API}/servicios`, getAuthHeader())
  },

  // Obtener el detalle de un servicio específico por su ID
  getById(id) {
    return axios.get(`${API}/servicios/${id}`, getAuthHeader())
  },

  // --- MÉTODOS PARA ADMINISTRADORES ---

  getServiciosAdmin() {
    return axios.get(`${API}/servicios/admin`, getAuthHeader())
  },

  crearServicio(data) {
    return axios.post(`${API}/servicios`, data, getAuthHeader())
  },

  actualizar(id, data) {
    return axios.put(`${API}/servicios/${id}`, data, getAuthHeader())
  },

  
  deshabilitar(id) {
    return axios.delete(`${API}/servicios/${id}`, getAuthHeader())
  },

  
  activar(id) {
    return axios.put(`${API}/servicios/activar/${id}`, {}, getAuthHeader())
  },

   
  getCategorias() {
    return axios.get(`${API}/categorias`, getAuthHeader())
  }
}