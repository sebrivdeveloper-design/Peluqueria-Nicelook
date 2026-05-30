<template>

  <div class="header-bar">

    <button
      v-if="mostrarBoton"
      class="btn-primary"
      @click="$emit('crear')"
    >
      <span class="btn-icon"><Plus :size="16" /></span>
      <span>{{ textoBoton }}</span>
    </button>

  </div>

</template>

<script>
import { Plus } from 'lucide-vue-next'

export default {

  components: {
    Plus
  },

  props: {

    textoBoton: {
      type: String,
      default: 'Crear'
    },

    mostrarBoton: {
      type: Boolean,
      default: true
    }

  },

  emits: ['crear']

}
</script>

<style scoped>

.header-bar {
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #014421;
  color: white;
  border: none;
  padding: 0 20px;
  height: 42px;
  border-radius: 12px;
  font-size: 13.5px;
  font-weight: 600;
  font-family: 'Manrope', sans-serif;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow:
    0 1px 3px rgba(1, 68, 33, 0.25),
    0 4px 12px rgba(1, 68, 33, 0.18);
  letter-spacing: 0.1px;
}

.btn-primary:hover {
  background: #025c2c;
  transform: translateY(-1px);
  box-shadow:
    0 2px 6px rgba(1, 68, 33, 0.28),
    0 8px 20px rgba(1, 68, 33, 0.22);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow:
    0 1px 3px rgba(1, 68, 33, 0.2);
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.9;
}

@media (max-width: 600px) {
  .header-bar {
    justify-content: stretch;
  }
  .btn-primary {
    width: 100%;
    justify-content: center;
  }
}
</style>
