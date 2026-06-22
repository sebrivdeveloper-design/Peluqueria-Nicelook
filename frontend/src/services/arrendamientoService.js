import api from './axiosInstance'

// Listado completo: valor por defecto + tarifas por estilista + por estilista/servicio
export const getArrendamientos = () =>
  api.get('/arrendamientos')

// Valor por defecto del salón
export const getArrendamientoDefault = () =>
  api.get('/arrendamientos/default')

// Previsualizar el valor que se aplicaría a una combinación estilista/servicio
export const resolverArrendamiento = (idEmpleado, idServicio) =>
  api.get('/arrendamientos/resolver', { params: { idEmpleado, idServicio } })

// Crear/actualizar el valor por defecto
export const guardarArrendamientoDefault = (valor) =>
  api.put('/arrendamientos/default', { valor })

// Crear/actualizar tarifa por estilista (idServicio null) o estilista+servicio
export const guardarArrendamiento = (data) =>
  api.post('/arrendamientos', data)

export const eliminarArrendamiento = (id) =>
  api.delete(`/arrendamientos/${id}`)
