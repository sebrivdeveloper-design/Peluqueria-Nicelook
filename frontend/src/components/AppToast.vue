<template>
  <transition name="toast-fade">
    <div v-if="visible" :class="['toast', type]">
      <div class="toast-content">
        <strong>{{ title }}</strong>
        <p>{{ message }}</p>
      </div>

      <button class="toast-close" @click="$emit('close')">×</button>
    </div>
  </transition>
</template>

<script>
export default {
  props: {
    visible: Boolean,
    type: {
      type: String,
      default: 'info'
    },
    title: {
      type: String,
      default: 'Notificación'
    },
    message: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.toast {
  position: fixed;
  top: 24px;
  right: 24px;
  min-width: 320px;
  max-width: 420px;
  padding: 16px 18px;
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.18);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  z-index: 2000;
  border: 1px solid transparent;
}

.toast-content strong {
  display: block;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
}

.toast-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.45;
}

.toast-close {
  background: transparent;
  border: none;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  color: inherit;
}

.toast.success {
  background: #eef8f0;
  color: #1f5f2c;
  border-color: #cfe6d4;
}

.toast.error {
  background: #fff1f1;
  color: #8a2d2d;
  border-color: #f0caca;
}

.toast.warning {
  background: #fff8e8;
  color: #7a5a00;
  border-color: #f0dfaa;
}

.toast.info {
  background: #eef5ff;
  color: #24508a;
  border-color: #cdddf5;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.22s ease;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 600px) {
  .toast {
    top: 16px;
    right: 16px;
    left: 16px;
    min-width: auto;
    max-width: none;
  }
}
</style>