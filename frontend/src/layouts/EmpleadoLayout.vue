<template>
  <div class="layout-container">
    
    <aside class="sidebar">
      <div class="profile-section">
        <div class="avatar-container">
          <div class="avatar-fallback">
            <i class="pi pi-user"></i>
          </div>
        </div>
        <h2 class="profile-role">BARBERO</h2>
        <p class="profile-email">{{ correoEmpleado }}</p>
      </div>

      <nav class="sidebar-menu">
        <router-link to="/empleado/agenda" class="menu-item" active-class="active">
          <i class="pi pi-calendar icon-large"></i>
          <span>Agenda</span>
        </router-link>

        <router-link to="/empleado/turnos" class="menu-item" active-class="active">
          <i class="pi pi-check-square icon-large"></i>
          <span>Turnos</span>
        </router-link>

        <router-link to="/empleado/servicios" class="menu-item" active-class="active">
          <i class="pi pi-scissors icon-large"></i>
          <span>Servicios</span>
        </router-link>

        <router-link to="/empleado/perfil" class="menu-item" active-class="active">
          <i class="pi pi-user-edit icon-large"></i>
          <span>Mi perfil</span>
        </router-link>
      </nav>
    </aside>

    <main class="main-content">
      <div class="view-wrapper">
        <router-view />
      </div>
    </main>

  </div>
</template>

<script>
export default {
  name: 'EmpleadoLayout',
  computed: {
    correoEmpleado() {
      const token = localStorage.getItem('token')
      if (!token) return ''
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.sub || ''
    }
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background-color: var(--color-background);
}

.sidebar {
  width: 280px;
  background-color: var(--color-primary);
  color: #ffffff;
  display: flex;
  flex-direction: column;
  padding: 40px 0;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.profile-section {
  text-align: center;
  padding: 0 20px 30px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.avatar-fallback {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background-color: #a8d5e2;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 4px solid var(--color-primary-medium);
}

.avatar-fallback i {
  font-size: 48px;
  color: #1e2a22;
}

.profile-role {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  margin: 0;
  color: #ffffff;
}

.profile-email {
  font-size: 13px;
  color: var(--color-secondary-soft);
  margin: 6px 0 0 0;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.menu-item:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.08);
}

.icon-large {
  font-size: 20px;
  width: 24px;
  text-align: center;
}

.menu-item.active {
  color: #ffffff;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.05) 100%);
  border-left: 4px solid var(--color-cream);
  font-weight: 600;
  box-shadow: inset 0 1px 1px rgba(255, 255, 255, 0.1);
}

.main-content {
  flex-grow: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.view-wrapper {
  padding: 40px;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

@media (max-width: 1024px) {
  .sidebar {
    width: 240px;
  }
  .view-wrapper {
    padding: 24px;
  }
}
</style>