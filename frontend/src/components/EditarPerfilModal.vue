<template>
  <div class="overlay" @click.self="$emit('cerrar')">
    <div class="modal">
      <button class="cerrar" @click="$emit('cerrar')" aria-label="Cerrar">×</button>

      <!-- Avatar -->
      <div class="perfil-avatar">{{ inicial }}</div>

      <div class="modal-header">
        <h3>Editar perfil</h3>
        <p>Actualiza tu nombre y datos de contacto.</p>
      </div>

      <!-- Correo (readonly) -->
      <div class="form-group">
        <label>Correo electrónico</label>
        <input type="text" :value="store.correo" readonly class="input-readonly" tabindex="-1" />
      </div>

      <!-- Nombre -->
      <div class="form-group">
        <label for="ep-nombre">Nombre completo</label>
        <input
          id="ep-nombre"
          v-model="form.nombreCompleto"
          type="text"
          placeholder="Tu nombre completo"
          @keydown.enter="guardar"
        />
      </div>

      <!-- Teléfono -->
      <div class="form-group">
        <label for="ep-telefono">Teléfono</label>
        <input
          id="ep-telefono"
          v-model="form.telefono"
          type="tel"
          placeholder="Ej. 3001234567"
          @keydown.enter="guardar"
        />
      </div>

      <div class="acciones">
        <button class="btn-secundario" @click="$emit('cerrar')">Cancelar</button>
        <button class="btn-guardar" @click="guardar" :disabled="guardando">
          {{ guardando ? 'Guardando…' : 'Guardar cambios' }}
        </button>
      </div>
    </div>

    <AppToast
      :visible="toast.visible"
      :type="toast.type"
      :title="toast.title"
      :message="toast.message"
      @close="toast.visible = false"
    />
  </div>
</template>

<script>
import AppToast from './AppToast.vue'
import { useUsuarioStore } from '../stores/usuarioStore'
import api from '../services/axiosInstance'

export default {
  components: { AppToast },

  emits: ['cerrar'],

  data() {
    return {
      store: useUsuarioStore(),
      form: { nombreCompleto: '', telefono: '' },
      guardando: false,
      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    inicial() {
      return (this.form.nombreCompleto || this.store.nombreCompleto || '?')[0].toUpperCase()
    }
  },

  async mounted() {
    try {
      const res = await api.get('/usuarios/me')
      this.form.nombreCompleto = res.data.nombreCompleto || ''
      this.form.telefono = res.data.telefono || ''
    } catch (e) {
      this.form.nombreCompleto = this.store.nombreCompleto
    }
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3000)
    },

    async guardar() {
      if (!this.form.nombreCompleto.trim()) {
        this.mostrarToast('warning', 'Campo requerido', 'El nombre no puede estar vacío.')
        return
      }
      this.guardando = true
      try {
        await this.store.actualizarPerfil({
          nombreCompleto: this.form.nombreCompleto.trim(),
          telefono: this.form.telefono.trim()
        })
        this.mostrarToast('success', 'Perfil actualizado', 'Tus datos fueron guardados.')
        setTimeout(() => this.$emit('cerrar'), 900)
      } catch (e) {
        this.mostrarToast('error', 'Error', 'No se pudo actualizar el perfil.')
      } finally {
        this.guardando = false
      }
    }
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 19, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 1100;
}

.modal {
  width: 100%;
  max-width: 460px;
  background: #f7faf5;
  border: 1px solid #d7e3d6;
  border-radius: 28px;
  padding: 28px;
  position: relative;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.14);
}

/* Avatar centered */
.perfil-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #014421, #1a6b3c);
  color: #ffffff;
  font-size: 26px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 6px 18px rgba(1, 68, 33, 0.28);
}

.modal-header {
  text-align: center;
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #173221;
  line-height: 1.15;
}

.modal-header p {
  margin: 6px 0 0;
  font-size: 14px;
  color: #66766a;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #1d3524;
}

.form-group input {
  width: 100%;
  border: 1px solid #d5dfd4;
  background: #ffffff;
  border-radius: 14px;
  padding: 12px 16px;
  font-size: 14px;
  color: #173221;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group input:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.form-group input::placeholder {
  color: #90a08f;
}

.input-readonly {
  background: #f0f4ef !important;
  color: #8fa895 !important;
  cursor: default;
}

.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn-secundario,
.btn-guardar {
  border: none;
  border-radius: 14px;
  padding: 12px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
  font-family: inherit;
}

.btn-secundario {
  background: #eef3ea;
  color: #35513b;
  border: 1px solid #d7e3d6;
}

.btn-secundario:hover {
  background: #e5ede1;
}

.btn-guardar {
  background: #014421;
  color: #ffffff;
  box-shadow: 0 8px 20px rgba(1, 68, 33, 0.2);
}

.btn-guardar:hover:not(:disabled) {
  background: #1f6a34;
  transform: translateY(-1px);
}

.btn-guardar:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.cerrar {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 38px;
  height: 38px;
  border: 1px solid #d7e3d6;
  background: #ffffff;
  color: #4a5d4f;
  border-radius: 12px;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.cerrar:hover {
  background: #f1f5ef;
  color: #173221;
}

@media (max-width: 600px) {
  .overlay { padding: 16px; align-items: flex-end; }
  .modal { border-radius: 24px 24px 18px 18px; }
  .acciones { flex-direction: column; }
  .btn-secundario, .btn-guardar { width: 100%; }
}
</style>
