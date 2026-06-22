<template>
  <section class="servicios-page">

    <div class="page-header">

      <div>
        <h1>Servicios</h1>
        <p>Administra los servicios disponibles en el sistema.</p>
      </div>

      <HeaderBar
        @crear="mostrarForm = true"
        textoBoton="Crear servicio"
      />

    </div>

    <!-- PESTAÑAS -->

    <div class="tabs-bar">
      <button
        class="tab"
        :class="{ active: tabActiva === 'todos' }"
        @click="tabActiva = 'todos'"
      >
        Todos
        <span class="tab-count">{{ servicios.length }}</span>
      </button>

      <button
        class="tab"
        :class="{ active: tabActiva === 'activos' }"
        @click="tabActiva = 'activos'"
      >
        Activos
        <span class="tab-count activos">{{ servicios.filter(s => s.estado === 'activo').length }}</span>
      </button>

      <button
        class="tab"
        :class="{ active: tabActiva === 'inactivos' }"
        @click="tabActiva = 'inactivos'"
      >
        Inactivos
        <span class="tab-count inactivos">{{ servicios.filter(s => s.estado !== 'activo').length }}</span>
      </button>
    </div>

    <!-- TABLA -->

    <div v-if="filtrados.length > 0" class="tabla-wrapper">

      <table class="tabla-servicios">

        <thead>

          <tr>
            <th>SERVICIO</th>
            <th>CATEGORÍA</th>
            <th>DURACIÓN</th>
            <th>PRECIO</th>
            <th>ESTADO</th>
            <th>ACCIONES</th>
          </tr>

        </thead>

        <tbody>

          <tr
            v-for="s in filtrados"
            :key="s.idServicio"
            :class="{ filaInactiva: s.estado === 'inactivo' }"
          >

            <!-- SERVICIO -->

            <td>

              <div class="servicio-info">

                <div class="servicio-avatar">

                  {{ s.nombreServicio.charAt(0).toUpperCase() }}

                </div>

                <div>

                  <h3>{{ s.nombreServicio }}</h3>

                  <p>{{ s.descripcion }}</p>

                </div>

              </div>

            </td>

            <!-- CATEGORIA -->

            <td>

              <span class="badge categoria">

                {{ s.categoria?.nombreCategoria || 'Sin categoría' }}

              </span>

            </td>

            <!-- DURACION -->

            <td>

              <div class="icon-text">

                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>

                {{ s.duracion }}

              </div>

            </td>

            <!-- PRECIO -->

            <td class="precio">

              ${{ s.precio }}

            </td>

            <!-- ESTADO -->

            <td>

              <span
                class="badge-estado"
                :class="s.estado === 'activo' ? 'activo' : 'inactivo'"
              >

                {{ s.estado === 'activo' ? 'Activo' : 'Inactivo' }}

              </span>

            </td>

            <!-- ACCIONES -->

            <td>

              <div class="acciones">

                <!-- ICONO EDITAR -->

                <button
                  class="btn-icon"
                  @click.stop="servicioEditar = s"
                >

                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path d="M12 20h9"/>
                    <path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/>
                  </svg>

                </button>

                <!-- SWITCH -->

                <label class="switch">

                  <input
                    type="checkbox"
                    :checked="s.estado === 'activo'"
                    @change="toggleEstado(s)"
                  />

                  <span class="slider"></span>

                </label>

              </div>

            </td>

          </tr>

        </tbody>

      </table>

    </div>

    <!-- EMPTY -->

    <div v-if="filtrados.length === 0" class="empty">

      <template v-if="servicios.length === 0">
        <h3>No hay servicios registrados</h3>
        <p>Comienza creando uno nuevo con el botón superior.</p>
      </template>

      <template v-else-if="busquedaActual">
        <h3>Sin resultados para "{{ busquedaActual }}"</h3>
        <p>Intenta con otro término o limpia la búsqueda.</p>
      </template>

      <template v-else-if="tabActiva === 'activos'">
        <h3>No hay servicios activos</h3>
        <p>Activa un servicio usando el interruptor en la tabla.</p>
      </template>

      <template v-else-if="tabActiva === 'inactivos'">
        <h3>No hay servicios inactivos</h3>
        <p>Todos los servicios están activos.</p>
      </template>

    </div>

    <!-- FORM CREAR -->

    <ServicioForm
      v-if="mostrarForm"
      @cerrar="mostrarForm = false"
      @guardado="recargar"
    />

    <!-- FORM EDITAR -->

    <ServicioForm
      v-if="servicioEditar"
      :servicio="servicioEditar"
      @cerrar="servicioEditar = null"
      @guardado="onEditarGuardado"
    />

    <AppConfirmModal
      :visible="confirmModal.visible"
      :title="confirmModal.titulo"
      :message="confirmModal.mensaje"
      @confirm="ejecutarToggle"
      @cancel="cancelarToggle"
    />

    <AppToast
      :visible="toast.visible"
      :type="toast.type"
      :title="toast.title"
      :message="toast.message"
      @close="toast.visible = false"
    />

  </section>
</template>

<script>
import HeaderBar from '@/components/HeaderBar.vue'
import servicioService from '@/services/servicioService'
import ServicioForm from '@/components/ServicioForm.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import AppToast from '@/components/AppToast.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {

  components: {
    HeaderBar,
    ServicioForm,
    AppConfirmModal,
    AppToast
  },

  inject: {
    adminSearch: { default: null }
  },

  data() {

    return {

      servicios: [],

      mostrarForm: false,

      servicioEditar: null,

      tabActiva: 'todos',

      confirmModal: { visible: false, titulo: '', mensaje: '', servicio: null },

      toast: { visible: false, type: 'info', title: '', message: '' }

    }

  },

  computed: {

    busquedaActual() {
      return (this.adminSearch ?? '').toLowerCase().trim()
    },

    filtrados() {

      // 1. Filtrar por pestaña
      let lista = this.servicios
      if (this.tabActiva === 'activos') {
        lista = lista.filter(s => s.estado === 'activo')
      } else if (this.tabActiva === 'inactivos') {
        lista = lista.filter(s => s.estado !== 'activo')
      }

      // 2. Filtrar por búsqueda del topbar
      const q = this.busquedaActual
      if (!q) return lista

      return lista.filter(s =>
        (s.nombreServicio || '').toLowerCase().includes(q) ||
        (s.descripcion || '').toLowerCase().includes(q) ||
        (s.categoria?.nombreCategoria || '').toLowerCase().includes(q)
      )

    }

  },


  methods: {

    async recargar() {

      try {

        const res =
          await servicioService.getServiciosAdmin()

        // MOSTRAR TODOS
        this.servicios = res.data

        this.mostrarForm = false

      } catch (error) {

        console.error(
          "Error al cargar servicios:",
          error
        )

      }

    },

    onEditarGuardado() {
      this.servicioEditar = null
      this.recargar()
    },

    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3000)
    },

    toggleEstado(s) {
      const accion = s.estado === 'activo' ? 'Desactivar' : 'Activar'
      this.confirmModal = {
        visible: true,
        titulo: `${accion} servicio`,
        mensaje: `¿${accion} "${s.nombreServicio}"?`,
        servicio: s
      }
    },

    cancelarToggle() {
      this.confirmModal.visible = false
      this.recargar() // restaura el switch a su posición real
    },

    async ejecutarToggle() {
      const s = this.confirmModal.servicio
      try {
        if (s.estado === 'activo') {
          await servicioService.deshabilitar(s.idServicio)
          useNotificacionesStore().agregar('warning', 'Servicio desactivado', `"${s.nombreServicio}" fue desactivado`)
          this.mostrarToast('success', 'Servicio desactivado', `"${s.nombreServicio}" ya no está disponible.`)
        } else {
          await servicioService.activar(s.idServicio)
          useNotificacionesStore().agregar('success', 'Servicio activado', `"${s.nombreServicio}" está activo nuevamente`)
          this.mostrarToast('success', 'Servicio activado', `"${s.nombreServicio}" está disponible.`)
        }
        this.recargar()
      } catch (error) {
        console.error(error)
        this.mostrarToast('error', 'Error', 'No se pudo cambiar el estado del servicio.')
        this.recargar()
      } finally {
        this.confirmModal.visible = false
      }
    }

  },

  mounted() {

    this.recargar()

  }

}
</script>

<style scoped>

.servicios-page {

  display: flex;

  flex-direction: column;

  gap: 24px;
}

/* HEADER */

.page-header {

  display: flex;

  align-items: center;

  justify-content: space-between;

  gap: 16px;
}

.page-header h1 {

  font-size: 42px;

  font-weight: 700;

  color: #101828;

  letter-spacing: -1px;
}

.page-header p {

  margin-top: 8px;

  color: #667085;

  font-size: 15px;
}

/* PESTAÑAS */

.tabs-bar {
  display: flex;
  gap: 4px;
  background: #f2f4f3;
  padding: 5px;
  border-radius: 14px;
  width: fit-content;
}

.tab {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: #667085;
  font-size: 13.5px;
  font-weight: 500;
  font-family: 'Manrope', sans-serif;
  cursor: pointer;
  transition: all 0.18s ease;
  white-space: nowrap;
}

.tab:hover {
  background: rgba(255, 255, 255, 0.65);
  color: #344054;
}

.tab.active {
  background: #ffffff;
  color: #101828;
  font-weight: 600;
  box-shadow:
    0 1px 4px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(0, 0, 0, 0.04);
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 20px;
  padding: 0 7px;
  border-radius: 999px;
  font-size: 11.5px;
  font-weight: 700;
  background: #e4e7e4;
  color: #667085;
  transition: all 0.18s ease;
}

.tab.active .tab-count {
  background: #e8eaea;
  color: #344054;
}

.tab.active .tab-count.activos {
  background: #dcfce7;
  color: #16a34a;
}

.tab.active .tab-count.inactivos {
  background: #fee2e2;
  color: #dc2626;
}

/* TABLA */

.tabla-wrapper {
  width: 100%;
  background: #ffffff;
  border-radius: 18px;
  overflow-x: auto;
  border: 1px solid #d9e8db;
  box-shadow:
    0 2px 8px rgba(1, 68, 33, 0.06),
    0 1px 2px rgba(1, 68, 33, 0.04);
}

.tabla-servicios {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
}

.tabla-servicios thead {
  background: #f0f7f1;
}

.tabla-servicios th {
  text-align: left;
  padding: 14px 20px;
  color: #4a7c59;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.3px;
  text-transform: uppercase;
  white-space: nowrap;
  border-bottom: 1px solid #e8f0e9;
}

.tabla-servicios td {
  padding: 16px 20px;
  border-top: 1px solid #edf2ee;
  vertical-align: middle;
}

.tabla-servicios tbody tr {
  transition: background 0.15s ease;
}

.tabla-servicios tbody tr:hover {
  background: #f6fbf7;
}

.filaInactiva {

  opacity: 0.65;
}

/* SERVICIO */

.servicio-info {

  display: flex;

  align-items: center;

  gap: 16px;
}

.servicio-avatar {

  width: 58px;

  height: 58px;

  border-radius: 16px;

  background: #ecfdf3;

  color: #027a48;

  display: flex;

  align-items: center;

  justify-content: center;

  font-size: 22px;

  font-weight: 700;
}

.servicio-info h3 {

  margin: 0;

  font-size: 16px;

  color: #101828;

  font-weight: 700;
}

.servicio-info p {

  margin-top: 6px;

  color: #667085;

  font-size: 13px;
}

/* BADGES */

.badge {

  display: inline-flex;

  align-items: center;

  padding: 7px 12px;

  border-radius: 999px;

  font-size: 12px;

  font-weight: 600;

  background: #f4f4f5;

  color: #344054;
}

.badge.categoria {

  background: #fefce8;

  color: #854d0e;

  border: 1px solid #fde68a;
}

/* DURACION */

.icon-text {

  display: flex;

  align-items: center;

  gap: 8px;

  color: #475467;

  font-size: 14px;
}

/* PRECIO */

.precio {

  color: #027a48;

  font-weight: 700;
}

/* ESTADO */

.badge-estado {

  display: inline-flex;

  align-items: center;

  padding: 7px 12px;

  border-radius: 999px;

  font-size: 12px;

  font-weight: 700;
}

.badge-estado.activo {

  background: #ecfdf3;

  color: #027a48;
}

.badge-estado.inactivo {

  background: #fef3f2;

  color: #b42318;
}

/* ACCIONES */

.acciones {

  display: flex;

  align-items: center;

  gap: 14px;
}

.btn-icon {

  width: 36px;

  height: 36px;

  border: 1px solid #d0d5dd;

  border-radius: 10px;

  background: white;

  cursor: pointer;

  display: flex;

  align-items: center;

  justify-content: center;

  transition: 0.2s;
}

.btn-icon:hover {

  background: #f9fafb;
}

/* SWITCH */

.switch {

  position: relative;

  display: inline-block;

  width: 44px;

  height: 24px;
}

.switch input {

  opacity: 0;

  width: 0;

  height: 0;
}

.slider {

  position: absolute;

  cursor: pointer;

  top: 0;

  left: 0;

  right: 0;

  bottom: 0;

  background-color: #d0d5dd;

  transition: .3s;

  border-radius: 999px;
}

.slider:before {

  position: absolute;

  content: "";

  height: 18px;

  width: 18px;

  left: 3px;

  bottom: 3px;

  background-color: white;

  transition: .3s;

  border-radius: 50%;
}

.switch input:checked + .slider {

  background-color: #16a34a;
}

.switch input:checked + .slider:before {

  transform: translateX(20px);
}

/* EMPTY */

.empty {

  background:
    rgba(255,255,255,0.92);

  border:
    1px solid rgba(0,0,0,0.04);

  border-radius: 30px;

  padding: 70px 30px;

  text-align: center;

  box-shadow:
    0 4px 20px rgba(16,24,40,0.04);
}

.empty h3 {

  color: #101828;

  margin-bottom: 8px;

  font-size: 22px;
}

.empty p {

  color: #667085;
}

/* MOBILE */

@media (max-width: 1024px) {

  .page-header h1 {
    font-size: 32px;
  }

}

@media (max-width: 640px) {

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .page-header h1 {
    font-size: 28px;
  }

  .tabs-bar {
    width: 100%;
    overflow-x: auto;
  }

  .servicio-avatar {
    width: 44px;
    height: 44px;
    font-size: 18px;
    border-radius: 12px;
  }

  .empty {
    padding: 48px 20px;
  }

}
</style>