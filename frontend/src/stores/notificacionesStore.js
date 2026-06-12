import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotificacionesStore = defineStore('notificaciones', () => {
  const lista = ref([])
  const noLeidas = computed(() => lista.value.filter(n => !n.leida).length)

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

  function marcarTodasLeidas() {
    lista.value.forEach(n => (n.leida = true))
  }

  return { lista, noLeidas, agregar, marcarTodasLeidas }
})
