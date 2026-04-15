import axios from 'axios'

const API = 'http://localhost:8080/api/categorias'

// 🔥 función para obtener headers con token
function getAuthHeader() {
  const token = localStorage.getItem("token")

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export const getCategorias = () =>
  axios.get(API, getAuthHeader())

export const crearCategoria = (data) =>
  axios.post(API, data, getAuthHeader())

export const actualizarCategoria = (idCategoria, data) =>
  axios.put(`${API}/${idCategoria}`, data, getAuthHeader())

export const deshabilitarCategoria = (idCategoria) =>
  axios.delete(`${API}/${idCategoria}`, getAuthHeader())

export const activarCategoria = (idCategoria) =>
  axios.put(`${API}/activar/${idCategoria}`, {}, getAuthHeader())

export const getCategoriaById = (idCategoria) =>
  axios.get(`${API}/${idCategoria}`, getAuthHeader())