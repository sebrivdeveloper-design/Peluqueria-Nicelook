<template>
  <div class="sidebar">

    <button class="toggle" @click="open = !open">☰</button>

    <div :class="['menu', { show: open }]">
      <div class="perfil">
        <div class="avatar"></div>
        <h4>ADMIN</h4>
      </div>

      <ul>
        <li>Categorías</li>
        <li>Servicios</li>
        <li>Empleados</li>
      </ul>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      open: window.innerWidth > 768
    }
  }
}
</script>

<style scoped>
.sidebar {
  background: #0b3d2e;
  color: white;
  padding: 10px;
  width: 220px;
}

.menu {
  display: block;
}

.toggle {
  display: none;
}

/* 📱 mobile */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
  }

  .menu {
    display: none;
  }

  .menu.show {
    display: block;
  }

  .toggle {
    display: block;
    background: none;
    border: none;
    color: white;
    font-size: 20px;
  }
}
</style>