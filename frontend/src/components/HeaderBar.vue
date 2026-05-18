<template>
  <div class="header">
    
    <!-- Buscador -->
    <div class="search-box">
      <Search :size="18" class="search-icon" />

      <input
        :value="busqueda"
        @input="$emit('update:busqueda', $event.target.value)"
        @keyup.enter="$emit('buscar')"
        :placeholder="placeholder"
        maxlength="30"
      />

      <button class="btn-buscar" @click="$emit('buscar')">
        Buscar
      </button>
    </div>

    <!-- Botón -->
    <button v-if="mostrarBoton" class="btn-primary" @click="$emit('crear')">
      <Plus :size="18" />
      <span>{{ textoBoton }}</span>
    </button>

  </div>
</template>

<script>
import { Search, Plus } from 'lucide-vue-next'

export default {
  components: {
    Search,
    Plus
  },
  props: {
    busqueda: String,
    textoBoton: {
      type: String,
      default: "Crear"
    },
    placeholder: {
      type: String,
      default: "Vamos a buscar tu estilo..."
    },
    mostrarBoton: {
      type: Boolean,
      default: true
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

/* 🔍 Buscador */
.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 18px;
  padding: 12px 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.04);
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.search-box:focus-within {
  border: 1px solid #004518;
  box-shadow: 0 10px 24px rgba(0, 69, 24, 0.12);
}

.search-icon {
  color: #6c7c70;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #173221;
  background: transparent;
}

.search-box input::placeholder {
  color: #8a9b8f;
}

.btn-buscar {
  background: #145c43;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.btn-buscar:hover {
  background: #004518;
}

/* 🟢 Botón principal */
.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #004518;
  color: #ffffff;
  border: none;
  padding: 12px 18px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
  box-shadow: 0 10px 20px rgba(0, 69, 24, 0.18);
}

.btn-primary:hover {
  background: #1f6a34;
  transform: translateY(-1px);
}

/* 📱 Responsive */
@media (max-width: 600px) {
  .header {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-primary {
    justify-content: center;
    width: 100%;
  }
}
</style>