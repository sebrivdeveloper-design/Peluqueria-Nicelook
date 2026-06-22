import api from './axiosInstance'

export const getCategoriasAdmin = () =>
  api.get('/categorias/admin')

export const getCategorias = () =>
  api.get('/categorias')

export const crearCategoria = (data) =>
  api.post('/categorias', data)

export const actualizarCategoria = (idCategoria, data) =>
  api.put(`/categorias/${idCategoria}`, data)

export const deshabilitarCategoria = (idCategoria) =>
  api.delete(`/categorias/${idCategoria}`)

export const activarCategoria = (idCategoria) =>
  api.put(`/categorias/activar/${idCategoria}`, {})

export const getCategoriaById = (idCategoria) =>
  api.get(`/categorias/${idCategoria}`)
