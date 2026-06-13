import { createRouter, createWebHistory } from 'vue-router'

// Vistas
import LoginView from '@/views/LoginView.vue'
import ServicioClienteView from '@/views/ServicioClienteView.vue'
import ServiciosView from '@/views/ServicioView.vue'
import ClientesView from '@/views/ClientesView.vue'
// Layouts
import AdminLayout from '@/layouts/AdminLayout.vue'
import RecepcionistaLayout from '@/layouts/RecepcionistaLayout.vue'
import EmpleadoLayout from '@/layouts/EmpleadoLayout.vue'
import ClienteLayout from '@/layouts/ClienteLayout.vue'


// Admin views
import CategoriasView from '@/views/CategoriasView.vue'
import CategoriaDetalle from '@/views/CategoriaDetalle.vue'
import EmpleadosView from '@/views/EmpleadosView.vue'
import AgendaEmpleadoView from '@/views/AgendaEmpleadoView.vue'
import AgendaRecepcionistaView from '@/views/AgendaRecepcionistaView.vue'

const routes = [
  // LOGIN
  {
    path: '/',
    component: LoginView
  },

  // 🟩 ADMIN
  {
    path: '/admin',
    component: AdminLayout,
    meta: { roles: ['ADMIN'] },
    children: [
      {
        path: '',
        redirect: '/admin/categorias'
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
        path: 'servicios/:id', // Simplificado para que sea relativo al padre /admin
        name: 'ServicioDetalle',
        component: () => import('@/views/ServicioDetalleView.vue')
      },
      {
        path: 'pagos-empleados',
        name: 'PagoEmpleados',
        component: () => import('@/views/PagoEmpleadosView.vue')
      },
      {
        path: 'reportes',
        name: 'Reportes',
        component: () => import('@/views/ReportesView.vue')
      }
    ]
  },

  // 🟪 CLIENTE
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
      }, {
      path: 'servicios/:id',
      name: 'ServicioDetalleCliente',
      component: () => import('@/views/ServicioDetalleView.vue')
    }, {
      path: 'mis-citas',
      name: 'MisCitas',
      component: () => import('@/views/MisCitasView.vue')
    }
    ]
  },

  // 🟦 RECEPCIONISTA (Actualizado con rutas hijas)
  {
    path: '/recepcionista',
    component: RecepcionistaLayout,
    meta: { roles: ['RECEPCIONISTA', 'ADMIN'] },
    children: [
      {
        path: '',
        redirect: '/recepcionista/clientes' // Redirección por defecto al entrar al módulo
      },
      {
        path: 'clientes',
        name: 'RecepcionClientes',
        component: ClientesView // Carga la tabla de clientes calcada de tu diseño
      },
      {
        path: 'agenda',
        name: 'AgendaRecepcionista',
        component: AgendaRecepcionistaView
      },
      {
        path: 'caja',
        name: 'Caja',
        component: () => import('@/views/CajaView.vue')
      },
      {
        path: 'pagos',
        name: 'Pagos',
        component: () => import('@/views/PagosView.vue')
      }
    ]
  },

  // 🟧 EMPLEADO
{
  path: '/empleado',
  component: EmpleadoLayout,
  meta: { roles: ['EMPLEADO', 'ADMIN'] },
  children: [
    {
      path: '',
      redirect: '/empleado/agenda'
    },
    {
      path: 'agenda',
      name: 'AgendaEmpleado',
      component: AgendaEmpleadoView
    }
  ]
}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function rolDelToken(token) {
  try {
    return JSON.parse(atob(token.split('.')[1])).rol || null
  } catch {
    return null
  }
}

function homeDeRol(rol) {
  const homes = {
    ADMIN: '/admin',
    RECEPCIONISTA: '/recepcionista',
    EMPLEADO: '/empleado',
    CLIENTE: '/cliente/servicios'
  }
  return homes[rol] || '/'
}

router.beforeEach((to) => {
  const token = localStorage.getItem("token");

  // 🟢 RUTAS PÚBLICAS (catálogo del cliente)
  if (to.path.startsWith("/cliente")) {
    // Mis citas sí requiere sesión
    if (to.path.startsWith("/cliente/mis-citas") && !token) {
      return "/cliente/servicios";
    }
    return;
  }

  // 🟢 LOGIN siempre permitido
  if (to.path === "/") {
    return;
  }

  // 🔴 PROTEGIDAS: requiere token
  if (!token) {
    return "/";
  }

  // 🔴 VALIDACIÓN DE ROL: si la ruta exige roles, el rol del JWT debe estar incluido
  const rolesRuta = to.matched
    .map(r => r.meta?.roles)
    .find(r => Array.isArray(r));

  if (rolesRuta) {
    const rol = rolDelToken(token);
    if (!rol) return "/";
    if (!rolesRuta.includes(rol)) {
      return homeDeRol(rol);
    }
  }
});

export default router