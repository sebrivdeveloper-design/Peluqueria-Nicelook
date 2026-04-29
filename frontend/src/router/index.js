import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '@/views/LoginView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import RecepcionistaLayout from '@/layouts/RecepcionistaLayout.vue'
import EmpleadoLayout from '@/layouts/EmpleadoLayout.vue'
import ClienteLayout from '@/layouts/ClienteLayout.vue'
import CategoriasView from '@/views/CategoriasView.vue'
import CategoriaDetalle from '@/views/CategoriaDetalle.vue'
import EmpleadosView from '@/views/EmpleadosView.vue'
import ServiciosView from '@/views/ServicioView.vue'


const routes = [
  { path: '/', component: LoginView },
  { path: '/admin', component: AdminLayout, children: [
      {
      path: '',
      redirect: '/admin/categorias' // ESTA LÍNEA ES LA CLAVE
      },
      {
        path: 'categorias',
        name: 'categorias',
        component: CategoriasView
      },
      {
        path: 'categorias/:idCategoria',
        name: 'categoriaDetalle',
        component: CategoriaDetalle
        
      },
      {
        path: 'empleados',
        name: 'empleados',
        component: EmpleadosView
      },
      {
        path: 'servicios',
        name: 'servicios',
        component: ServiciosView
      }

    ]
  },

  { path: '/recepcionista', component: RecepcionistaLayout },
  { path: '/empleado', component: EmpleadoLayout },
  { path: '/cliente', component: ClienteLayout }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {

  const token = localStorage.getItem("token");

  // rutas públicas
  if (to.path === "/" || to.path === "/cliente") {
    return next();
  }

  // si no hay token → login
  if (!token) {
    return next("/");
  }

  try {
    // validar token (decodificar)
    const payload = JSON.parse(atob(token.split('.')[1]));
    const rol = payload.rol;

    // PROTECCIÓN POR ROL
    if (to.path.startsWith("/admin") && rol !== "ADMIN") {
      return next("/");
    }

    if (to.path.startsWith("/recepcionista") && rol !== "RECEPCIONISTA") {
      return next("/");
    }

    if (to.path.startsWith("/empleado") && rol !== "EMPLEADO") {
      return next("/");
    }

    next();

  } catch (e) {
    // token corrupto o inválido
    localStorage.removeItem("token");
    return next("/");
  }
});

export default router