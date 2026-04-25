<template>
  <transition name="fade">
    <div v-if="visible" class="overlay" @click.self="$emit('cancel')">
      <div class="modal">
        <div class="icon-box">
          <span>!</span>
        </div>

        <h3>{{ title }}</h3>
        <p>{{ message }}</p>

        <div class="actions">
          <button class="btn-secondary" @click="$emit('cancel')">
            Cancelar
          </button>

          <button class="btn-danger" @click="$emit('confirm')">
            Confirmar
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'AppConfirmModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: 'Confirmar acción'
    },
    message: {
      type: String,
      default: '¿Estás seguro de continuar?'
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
  z-index: 1200;
}

.modal {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 24px;
  padding: 28px 24px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.14);
  text-align: center;
}

.icon-box {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  border-radius: 18px;
  background: #fff6cc;
  color: #7a6200;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
}

.modal h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #173221;
}

.modal p {
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.55;
  color: #66766a;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.btn-secondary,
.btn-danger {
  border: none;
  border-radius: 14px;
  padding: 12px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.22s ease;
}

.btn-secondary {
  background: #eef3ea;
  color: #35513b;
  border: 1px solid #d7e3d6;
}

.btn-secondary:hover {
  background: #e6eee2;
}

.btn-danger {
  background: #fff0b8;
  color: #6e5600;
  box-shadow: 0 8px 18px rgba(255, 208, 74, 0.18);
}

.btn-danger:hover {
  background: #ffe58a;
  transform: translateY(-1px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .modal {
    padding: 24px 18px;
  }

  .actions {
    flex-direction: column;
  }

  .btn-secondary,
  .btn-danger {
    width: 100%;
  }
}
</style>