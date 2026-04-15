import { createRouter, createWebHistory } from 'vue-router'
import CategoriasView from '../view/CategoriasView.vue'

const routes = [
  { path: '/', redirect: '/categorias' },

  {
    path: '/categorias',
    name: 'categorias',
    component: CategoriasView
  },

  {
    path: '/admin/categorias/:idCategoria',
    component: () => import('../view/CategoriaDetalle.vue')
  },

  {
    path: '/admin/categorias',
    component: CategoriasView
  }
]
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router