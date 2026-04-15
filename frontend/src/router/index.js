import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '@/views/LoginView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import RecepcionistaLayout from '@/layouts/RecepcionistaLayout.vue'
import EmpleadoLayout from '@/layouts/EmpleadoLayout.vue'
import ClienteLayout from '@/layouts/ClienteLayout.vue'
import CategoriasView from '@/views/CategoriasView.vue'
import CategoriaDetalle from '@/views/CategoriaDetalle.vue'

const routes = [
  { path: '/', component: LoginView },

  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
      path: '',
      redirect: '/admin/categorias' // 🔥 ESTA LÍNEA ES LA CLAVE
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

export default router