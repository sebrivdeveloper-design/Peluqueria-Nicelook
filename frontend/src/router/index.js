import { createRouter, createWebHistory } from 'vue-router'

// Vistas
import LoginView from '@/views/LoginView.vue'
import ServicioClienteView from '@/views/ServicioClienteView.vue'
import ServiciosView from '@/views/ServicioView.vue'
// Layouts
import AdminLayout from '@/layouts/AdminLayout.vue'
import RecepcionistaLayout from '@/layouts/RecepcionistaLayout.vue'
import EmpleadoLayout from '@/layouts/EmpleadoLayout.vue'
import ClienteLayout from '@/layouts/ClienteLayout.vue'

// Admin views
import CategoriasView from '@/views/CategoriasView.vue'
import CategoriaDetalle from '@/views/CategoriaDetalle.vue'
import EmpleadosView from '@/views/EmpleadosView.vue'

const routes = [
  { path: '/', component: LoginView },
  {
    path: '/admin', component: AdminLayout, children: [
      {
        path: '',
        redirect: '/admin/categorias' // ESTA LÍNEA ES LA CLAVE
      },
      {
        path: 'categorias',
        component: CategoriasView
      },
      {
        path: 'categorias/:idCategoria',
        component: CategoriaDetalle
      },
      {
        path: 'empleados',
        component: EmpleadosView
      },
      {
        path: 'servicios',
        name: 'servicios',
        component: ServiciosView
      },
      {
        path: '/admin/servicios/:id',
        name: 'ServicioDetalle',
        component: () => import('@/views/ServicioDetalleView.vue')
      }


    ]
  },

  // 🟪 CLIENTE (🔥 BIEN UBICADO)
  {
    path: '/cliente',
    component: ClienteLayout,
    children: [
      {
        path: '',
        redirect: '/cliente/servicios'
      },
      {
        path: 'servicios',
        name: 'serviciosCliente',
        component: ServicioClienteView
      }
    ]
  },

  // OTROS ROLES
  { path: '/recepcionista', component: RecepcionistaLayout },
  { path: '/empleado', component: EmpleadoLayout }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to, from, next) => {

  const token = localStorage.getItem("token");

  // 🟢 RUTAS PÚBLICAS (cliente)
  if (to.path.startsWith("/cliente")) {
    return next();
  }

  // 🟢 LOGIN siempre permitido
  if (to.path === "/") {
    return next();
  }

  // 🔴 PROTEGIDAS
  if (!token) {
    return next("/");
  }

  next();
});
export default router