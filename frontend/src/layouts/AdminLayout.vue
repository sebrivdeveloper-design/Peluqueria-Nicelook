<template>
  <div class="layout">
    <Sidebar />

    <div class="content">
      <slot />
    </div>
  </div>
</template>

<script>
import Sidebar from '../components/Sidebar.vue'

export default {
  components: { Sidebar }
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f4f4f4;
}

@media (max-width: 768px) {
  .layout {
    flex-direction: column;
  }
}
</style>