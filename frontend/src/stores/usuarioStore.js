import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/axiosInstance'

export const useUsuarioStore = defineStore('usuario', () => {
  const nombreCompleto = ref('')
  const correo = ref('')
  const rol = ref('')

  function init() {
    const token = localStorage.getItem('token')
    if (!token) return
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      correo.value = payload.sub || ''
      rol.value = payload.rol || ''
      nombreCompleto.value = payload.nombreCompleto || payload.sub || ''
    } catch (e) {
      console.error('Error al decodificar JWT', e)
    }
    // El JWT puede tener el nombre desactualizado si el perfil fue editado:
    // refrescar con los datos reales del servidor.
    api.get('/usuarios/me')
      .then(res => {
        if (res.data?.nombreCompleto) nombreCompleto.value = res.data.nombreCompleto
      })
      .catch(() => {})
  }

  async function actualizarPerfil(data) {
    const res = await api.put('/usuarios/me', data)
    nombreCompleto.value = res.data.nombreCompleto
    return res.data
  }

  return { nombreCompleto, correo, rol, init, actualizarPerfil }
})
