import api from './axiosInstance'

// Endpoints públicos (sin login) para la landing de reservas.
// Reutilizan la misma instancia axios; al no haber token, no se envía Authorization.

export const getServiciosPublicos = () =>
  api.get('/public/servicios')

export const getEstilistasPublicos = () =>
  api.get('/public/estilistas')

export const getDisponibilidadPublica = (idEmpleado, fecha) =>
  api.get('/public/disponibilidad', { params: { idEmpleado, fecha } })

export const solicitarOtp = (correo, nombre) =>
  api.post('/public/otp', { correo, nombre })

export const crearReserva = (payload) =>
  api.post('/public/reservas', payload)
