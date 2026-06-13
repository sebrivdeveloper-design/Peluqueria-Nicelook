import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/axiosInstance'

export const useNotificacionesStore = defineStore('notificaciones', () => {
  const lista = ref([])
  const noLeidas = computed(() => lista.value.filter(n => !n.leida).length)

  // Carga las notificaciones persistentes del backend (según el rol del JWT)
  async function cargar() {
    try {
      const res = await api.get('/notificaciones')
      lista.value = res.data.map(n => ({
        id: n.id,
        tipo: n.tipo,
        titulo: n.titulo,
        mensaje: n.mensaje,
        tiempo: n.fecha ? new Date(n.fecha) : new Date(),
        leida: n.leida
      }))
    } catch (e) {
      // Si el rol no tiene acceso (ej. cliente/empleado) simplemente queda vacío
      lista.value = []
    }
  }

  // Agrega una notificación local optimista (feedback inmediato en la misma sesión)
  function agregar(tipo, titulo, mensaje) {
    lista.value.unshift({
      id: Date.now(),
      tipo,
      titulo,
      mensaje,
      tiempo: new Date(),
      leida: false
    })
  }

  async function marcarTodasLeidas() {
    lista.value.forEach(n => (n.leida = true))
    try {
      await api.put('/notificaciones/leidas')
    } catch (e) {
      // No bloquea la UI si falla
    }
  }

  return { lista, noLeidas, cargar, agregar, marcarTodasLeidas }
})
