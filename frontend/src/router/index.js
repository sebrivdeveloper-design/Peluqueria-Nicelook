import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import AdminView from '@/views/AdminView.vue'
import RecepcionistaView from '@/views/RecepcionistaView.vue'
import EmpleadoView from '@/views/EmpleadoView.vue'
import ClienteView from '@/views/ClienteView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: LoginView },
    { path: '/admin', component: AdminView },
    { path: '/recepcionista', component: RecepcionistaView },
    { path: '/empleado', component: EmpleadoView },
    { path: '/cliente', component: ClienteView }
  ],
})

export default router
