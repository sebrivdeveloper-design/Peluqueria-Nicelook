import axios from 'axios'

const API = 'http://localhost:8080/api/categorias'

export const getCategorias = () => axios.get(API)
export const crearCategoria = (data) => axios.post(API, data)
export const actualizarCategoria = (idCategoria, data) => axios.put(`${API}/${idCategoria}`, data)

// 🔥 sigue siendo DELETE pero es soft delete
export const deshabilitarCategoria = (idCategoria) => axios.delete(`${API}/${idCategoria}`)

// 🔥 opcional
export const activarCategoria = (idCategoria) => axios.put(`${API}/activar/${idCategoria}`)
export const getCategoriaById = (idCategoria) => axios.get(`${API}/${idCategoria}`)