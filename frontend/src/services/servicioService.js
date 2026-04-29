import axios from 'axios'

const API = 'http://localhost:8080/api'

function getAuthHeader() {
  const token = localStorage.getItem("token")
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export default {
  getServicios() {
    return axios.get(`${API}/servicios`, getAuthHeader())
  },

  crearServicio(data) {
    return axios.post(`${API}/servicios`, data, getAuthHeader())
  },

  getCategorias() {
    return axios.get(`${API}/categorias`, getAuthHeader())
  }
}