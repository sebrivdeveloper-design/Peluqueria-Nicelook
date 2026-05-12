<template>
  <div class="layout">

    <!-- SIDEBAR -->
    <Sidebar rol="CLIENTE" />

    <!-- CONTENIDO -->
    <div class="content">
      <router-view />
    </div>

  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue';


export default {
  components: {
    Sidebar
  }
}
</script>

<style scoped>
.layout {
  display: flex;
  background: #f5f6f8;
  min-height: 100vh;
}

.content {
  flex: 1;
  padding: 25px;
}
</style>
