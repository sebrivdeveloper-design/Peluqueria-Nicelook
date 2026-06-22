<template>
  <div class="landing">

    <!-- ═══════════════ HERO ═══════════════ -->
    <header class="hero" :class="{ 'hero-img': usarBannerImagen }">
      <div class="hero-overlay">
        <img :src="logo" alt="NICE LOOK" class="hero-logo" />
        <h1 class="hero-title">{{ negocio.heroTitulo }}</h1>
        <p class="hero-sub">{{ negocio.heroSubtitulo }}</p>
        <button class="hero-cta" @click="irAReservar">Reservar cita</button>
      </div>
    </header>

    <main class="landing-main">

      <!-- ═══════════════ SOBRE NOSOTROS ═══════════════ -->
      <section class="seccion sobre">
        <h2 class="seccion-titulo">Sobre nosotros</h2>
        <p class="sobre-texto">{{ negocio.sobreNosotros }}</p>
      </section>

      <!-- ═══════════════ INFO PRÁCTICA ═══════════════ -->
      <section class="seccion info-grid">
        <div class="info-item">
          <MapPin :size="22" class="info-ic" />
          <div>
            <span class="info-label">Dirección</span>
            <p>{{ negocio.contacto.direccion }}</p>
          </div>
        </div>
        <div class="info-item">
          <Clock :size="22" class="info-ic" />
          <div>
            <span class="info-label">Horario</span>
            <p>{{ negocio.contacto.horario }}</p>
          </div>
        </div>
        <div class="info-item">
          <Phone :size="22" class="info-ic" />
          <div>
            <span class="info-label">Contacto</span>
            <p>{{ negocio.contacto.telefono }}</p>
            <a :href="`https://wa.me/${negocio.contacto.whatsapp}`" target="_blank" rel="noopener" class="wa-link">
              <MessageCircle :size="15" /> Escríbenos por WhatsApp
            </a>
          </div>
        </div>
      </section>

      <!-- ═══════════════ CATÁLOGO DE SERVICIOS ═══════════════ -->
      <section class="seccion">
        <h2 class="seccion-titulo">Nuestros servicios</h2>
        <div v-if="cargandoServicios" class="skeleton-grid">
          <div v-for="n in 4" :key="n" class="skeleton-card"></div>
        </div>
        <div v-else-if="servicios.length === 0" class="estado">No hay servicios disponibles por ahora.</div>
        <div v-else class="catalogo">
          <div v-for="(items, cat) in serviciosPorCategoria" :key="cat" class="cat-grupo">
            <h3 class="cat-nombre">{{ cat }}</h3>
            <div class="serv-grid">
              <article v-for="s in items" :key="s.idServicio" class="serv-card">
                <div class="serv-info">
                  <strong>{{ s.nombreServicio }}</strong>
                  <p class="serv-desc">{{ s.descripcion }}</p>
                  <span class="serv-meta"><Clock :size="13" /> {{ s.duracion }}</span>
                </div>
                <div class="serv-accion">
                  <span class="serv-precio">{{ formatoMoneda(s.precio) }}</span>
                  <button class="btn-mini-pri" @click="reservarServicio(s)">Reservar</button>
                </div>
              </article>
            </div>
          </div>
        </div>
      </section>

      <!-- ═══════════════ ESTILISTAS ═══════════════ -->
      <section class="seccion">
        <h2 class="seccion-titulo">Nuestro equipo</h2>
        <div v-if="cargandoEstilistas" class="skeleton-grid">
          <div v-for="n in 3" :key="n" class="skeleton-card sk-est"></div>
        </div>
        <div v-else-if="estilistas.length === 0" class="estado">Pronto conocerás a nuestro equipo.</div>
        <div v-else class="equipo-grid">
          <article v-for="e in estilistas" :key="e.idEmpleado" class="est-card">
            <div class="est-avatar">{{ inicial(e.nombre) }}</div>
            <strong>{{ e.nombre.trim() }}</strong>
            <span v-if="e.especialidad" class="est-esp">{{ e.especialidad }}</span>
          </article>
        </div>
      </section>

      <!-- ═══════════════ WIZARD DE RESERVA ═══════════════ -->
      <section id="wizard" class="seccion wizard-sec">
        <h2 class="seccion-titulo">Agenda tu cita</h2>

        <!-- Progreso -->
        <div v-if="paso <= 5" class="progreso">
          <div class="progreso-info">
            <span>Paso {{ paso }} de 5</span>
            <span class="progreso-label">{{ pasosLabels[paso - 1] }}</span>
          </div>
          <div class="progreso-track"><div class="progreso-fill" :style="{ width: (paso / 5 * 100) + '%' }"></div></div>
        </div>

        <div class="wizard-card">

          <!-- PASO 1: SERVICIO -->
          <div v-if="paso === 1">
            <h3 class="paso-titulo">Elige un servicio</h3>
            <div v-if="cargandoServicios" class="estado">Cargando servicios...</div>
            <div v-else class="opciones">
              <button
                v-for="s in servicios"
                :key="s.idServicio"
                class="opcion"
                :class="{ sel: servicioSel?.idServicio === s.idServicio }"
                @click="elegirServicio(s)"
              >
                <div class="opcion-top">
                  <strong>{{ s.nombreServicio }}</strong>
                  <span class="precio">{{ formatoMoneda(s.precio) }}</span>
                </div>
                <span class="opcion-meta"><Clock :size="13" /> {{ s.duracion }} · {{ s.categoria }}</span>
              </button>
            </div>
          </div>

          <!-- PASO 2: ESTILISTA -->
          <div v-else-if="paso === 2">
            <h3 class="paso-titulo">Elige un estilista</h3>
            <div v-if="cargandoEstilistas" class="estado">Cargando estilistas...</div>
            <div v-else-if="estilistas.length === 0" class="estado">No hay estilistas disponibles.</div>
            <div v-else class="opciones opciones-est">
              <button
                v-for="e in estilistas"
                :key="e.idEmpleado"
                class="opcion opcion-est"
                :class="{ sel: estilistaSel?.idEmpleado === e.idEmpleado }"
                @click="elegirEstilista(e)"
              >
                <div class="est-avatar sm">{{ inicial(e.nombre) }}</div>
                <div>
                  <strong>{{ e.nombre.trim() }}</strong>
                  <span v-if="e.especialidad" class="opcion-meta">{{ e.especialidad }}</span>
                </div>
              </button>
            </div>
            <div class="nav-btns">
              <button class="btn-sec" @click="paso = 1"><ChevronLeft :size="16" /> Atrás</button>
            </div>
          </div>

          <!-- PASO 3: FECHA Y HORA -->
          <div v-else-if="paso === 3" class="paso3">
            <h3 class="paso-titulo">Fecha y hora</h3>
            <div class="paso3-grid">
              <div class="campo fecha-col">
                <label><Calendar :size="15" /> Fecha</label>
                <input type="date" v-model="fecha" :min="hoyISO" @change="cargarSlots" />
              </div>
              <div class="slots-col">
                <p class="sub">Horarios disponibles <small>(duración {{ duracionMin }} min)</small></p>
                <div v-if="cargandoSlots" class="estado">Buscando horarios...</div>
                <div v-else-if="!fecha" class="estado">Selecciona una fecha para ver horarios.</div>
                <div v-else-if="slots.length === 0" class="estado">No hay horarios disponibles este día. Prueba otra fecha.</div>
                <div v-else class="slots">
                  <button
                    v-for="slot in slots"
                    :key="slot.idDisponibilidad + '-' + slot.hora"
                    class="slot"
                    :class="{ sel: slotSel && slotSel.hora === slot.hora && slotSel.idDisponibilidad === slot.idDisponibilidad }"
                    @click="slotSel = slot"
                  >
                    {{ slot.hora.slice(0, 5) }}
                  </button>
                </div>
              </div>
            </div>
            <div class="nav-btns">
              <button class="btn-sec" @click="paso = 2"><ChevronLeft :size="16" /> Atrás</button>
              <button class="btn-pri" :disabled="!slotSel" @click="paso = 4">Continuar</button>
            </div>
          </div>

          <!-- PASO 4: DATOS -->
          <div v-else-if="paso === 4">
            <h3 class="paso-titulo">Tus datos</h3>
            <div class="campo">
              <label>Nombre completo</label>
              <input v-model="contacto.nombre" placeholder="Ej. María Pérez" />
            </div>
            <div class="campo">
              <label>Teléfono</label>
              <input v-model="contacto.telefono" placeholder="Ej. 3001234567" />
            </div>
            <div class="campo">
              <label>Correo electrónico</label>
              <input v-model="contacto.correo" type="email" placeholder="tucorreo@ejemplo.com" />
            </div>
            <p class="hint">Te enviaremos un código de verificación a este correo.</p>
            <p v-if="error" class="error-msg">{{ error }}</p>
            <div class="nav-btns">
              <button class="btn-sec" @click="paso = 3"><ChevronLeft :size="16" /> Atrás</button>
              <button class="btn-pri" :disabled="enviandoOtp" @click="enviarCodigo">
                {{ enviandoOtp ? 'Enviando...' : 'Enviar código' }}
              </button>
            </div>
          </div>

          <!-- PASO 5: OTP -->
          <div v-else-if="paso === 5">
            <h3 class="paso-titulo">Verifica tu correo</h3>
            <p class="sub">Ingresa el código de 6 dígitos que enviamos a <strong>{{ contacto.correo }}</strong>.</p>
            <div class="campo">
              <label>Código de verificación</label>
              <input v-model="codigo" inputmode="numeric" maxlength="6" placeholder="••••••" class="otp-input" />
            </div>
            <p v-if="error" class="error-msg">{{ error }}</p>
            <div class="nav-btns">
              <button class="btn-sec" :disabled="enviandoOtp" @click="enviarCodigo">Reenviar código</button>
              <button class="btn-pri" :disabled="confirmando || codigo.length < 6" @click="confirmar">
                {{ confirmando ? 'Confirmando...' : 'Confirmar reserva' }}
              </button>
            </div>
          </div>

          <!-- PASO 6: CONFIRMACIÓN -->
          <div v-else class="confirmacion">
            <div class="check">✓</div>
            <h3 class="paso-titulo">¡Reserva confirmada!</h3>
            <div class="resumen">
              <p><span>Servicio</span><strong>{{ confirmacion.servicio }}</strong></p>
              <p><span>Estilista</span><strong>{{ confirmacion.estilista?.trim() }}</strong></p>
              <p><span>Fecha</span><strong>{{ formatoFecha(confirmacion.fecha) }}</strong></p>
              <p><span>Hora</span><strong>{{ confirmacion.horaInicio?.slice(0,5) }} - {{ confirmacion.horaFin?.slice(0,5) }}</strong></p>
              <p><span>Precio</span><strong>{{ formatoMoneda(confirmacion.precio) }}</strong></p>
            </div>
            <p class="hint">
              Te esperamos. El pago se realiza de forma presencial en el salón.
              Si necesitas cancelar o reprogramar, contáctanos al {{ negocio.contacto.telefono }}.
            </p>
            <button class="btn-pri full" @click="reiniciar">Hacer otra reserva</button>
          </div>

        </div>
      </section>
    </main>

    <!-- ═══════════════ FOOTER ═══════════════ -->
    <footer class="landing-footer">
      <strong class="foot-marca">{{ negocio.nombre }}</strong>
      <p>{{ negocio.contacto.direccion }} · {{ negocio.contacto.telefono }}</p>
      <div v-if="hayRedes" class="footer-redes">
        <a v-if="negocio.redes.instagram" :href="negocio.redes.instagram" target="_blank" rel="noopener"><Instagram :size="20" /></a>
        <a v-if="negocio.redes.facebook" :href="negocio.redes.facebook" target="_blank" rel="noopener"><Facebook :size="20" /></a>
        <a v-if="negocio.redes.tiktok" :href="negocio.redes.tiktok" target="_blank" rel="noopener"><Music2 :size="20" /></a>
      </div>
      <span class="foot-tag">{{ negocio.tagline }}</span>
    </footer>
  </div>
</template>

<script>
import {
  getServiciosPublicos,
  getEstilistasPublicos,
  getDisponibilidadPublica,
  solicitarOtp,
  crearReserva
} from '@/services/publicReservaService'
import { negocio, usarBannerImagen } from '@/config/negocioConfig'
import logo from '@/assets/logo.png'
import {
  MapPin, Clock, Phone, MessageCircle, Calendar, ChevronLeft,
  Instagram, Facebook, Music2
} from 'lucide-vue-next'

export default {
  name: 'ReservarLandingView',

  components: { MapPin, Clock, Phone, MessageCircle, Calendar, ChevronLeft, Instagram, Facebook, Music2 },

  data() {
    return {
      negocio,
      usarBannerImagen,
      logo,
      pasosLabels: ['Servicio', 'Estilista', 'Fecha y hora', 'Tus datos', 'Verificación'],

      paso: 1,

      servicios: [],
      cargandoServicios: false,
      servicioSel: null,

      estilistas: [],
      cargandoEstilistas: false,
      estilistaSel: null,

      fecha: '',
      bloques: [],
      cargandoSlots: false,
      slotSel: null,

      contacto: { nombre: '', telefono: '', correo: '' },
      enviandoOtp: false,
      codigo: '',
      confirmando: false,
      confirmacion: {},

      error: ''
    }
  },

  computed: {
    hoyISO() {
      return new Date().toISOString().split('T')[0]
    },

    duracionMin() {
      return this.parseDuracion(this.servicioSel?.duracion)
    },

    // Catálogo agrupado por categoría (para la sección de marketing)
    serviciosPorCategoria() {
      const grupos = {}
      for (const s of this.servicios) {
        const cat = s.categoria || 'Otros'
        if (!grupos[cat]) grupos[cat] = []
        grupos[cat].push(s)
      }
      return grupos
    },

    hayRedes() {
      const r = this.negocio.redes
      return !!(r.instagram || r.facebook || r.tiktok)
    },

    slots() {
      if (!this.bloques.length || !this.duracionMin) return []
      const aMin = (h) => { const [hh, mm] = h.split(':').map(Number); return hh * 60 + mm }
      const deMin = (t) => `${String(Math.floor(t / 60)).padStart(2, '0')}:${String(t % 60).padStart(2, '0')}:00`
      const out = []
      for (const b of this.bloques) {
        let t = aMin(b.horaInicioBloque)
        const fin = aMin(b.horaFinBloque)
        while (t + this.duracionMin <= fin) {
          out.push({ hora: deMin(t), idDisponibilidad: b.idDisponibilidad })
          t += this.duracionMin
        }
      }
      return out
    }
  },

  mounted() {
    this.cargarServicios()
    this.cargarEstilistas()
  },

  methods: {
    formatoMoneda(v) {
      return Number(v || 0).toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
    },

    formatoFecha(f) {
      if (!f) return ''
      return new Date(f + 'T00:00:00').toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long' })
    },

    inicial(nombre) {
      return (nombre?.trim()?.[0] || '?').toUpperCase()
    },

    parseDuracion(d) {
      if (!d) return 60
      const s = String(d).toLowerCase().trim()
      const m = s.match(/\d+/)
      if (!m) return 60
      let v = parseInt(m[0])
      if (s.includes('hora') || (s.includes('h') && !s.includes('min'))) v *= 60
      return Math.max(v, 5)
    },

    irAReservar() {
      const el = document.getElementById('wizard')
      if (el) el.scrollIntoView({ behavior: 'smooth' })
    },

    // Desde el catálogo de marketing: preselecciona servicio y entra al wizard
    reservarServicio(s) {
      this.elegirServicio(s)
      this.$nextTick(() => this.irAReservar())
    },

    async cargarServicios() {
      this.cargandoServicios = true
      try {
        const res = await getServiciosPublicos()
        this.servicios = res.data
      } catch (e) { console.error(e) } finally { this.cargandoServicios = false }
    },

    elegirServicio(s) {
      this.servicioSel = s
      this.slotSel = null
      this.bloques = []
      this.paso = 2
      if (!this.estilistas.length) this.cargarEstilistas()
    },

    async cargarEstilistas() {
      this.cargandoEstilistas = true
      try {
        const res = await getEstilistasPublicos()
        this.estilistas = res.data
      } catch (e) { console.error(e) } finally { this.cargandoEstilistas = false }
    },

    elegirEstilista(e) {
      this.estilistaSel = e
      this.slotSel = null
      this.bloques = []
      this.paso = 3
      if (this.fecha) this.cargarSlots()
    },

    async cargarSlots() {
      this.slotSel = null
      this.bloques = []
      if (!this.fecha || !this.estilistaSel) return
      this.cargandoSlots = true
      try {
        const res = await getDisponibilidadPublica(this.estilistaSel.idEmpleado, this.fecha)
        this.bloques = res.data
      } catch (e) { console.error(e) } finally { this.cargandoSlots = false }
    },

    validarContacto() {
      this.error = ''
      const c = this.contacto
      if (!c.nombre.trim()) { this.error = 'Ingresa tu nombre.'; return false }
      if (!/^[0-9+\s-]{7,20}$/.test(c.telefono.trim())) { this.error = 'Ingresa un teléfono válido.'; return false }
      if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(c.correo.trim())) { this.error = 'Ingresa un correo válido.'; return false }
      return true
    },

    async enviarCodigo() {
      if (this.paso === 4 && !this.validarContacto()) return
      this.error = ''
      this.enviandoOtp = true
      try {
        await solicitarOtp(this.contacto.correo.trim(), this.contacto.nombre.trim())
        this.codigo = ''
        this.paso = 5
      } catch (e) {
        this.error = typeof e.response?.data === 'string' ? e.response.data : 'No se pudo enviar el código.'
      } finally {
        this.enviandoOtp = false
      }
    },

    async confirmar() {
      this.error = ''
      this.confirmando = true
      try {
        const res = await crearReserva({
          nombre: this.contacto.nombre.trim(),
          correo: this.contacto.correo.trim(),
          telefono: this.contacto.telefono.trim(),
          idEmpleado: this.estilistaSel.idEmpleado,
          idServicio: this.servicioSel.idServicio,
          idDisponibilidad: this.slotSel.idDisponibilidad,
          horaInicio: this.slotSel.hora
        })
        this.confirmacion = res.data
        this.paso = 6
      } catch (e) {
        this.error = typeof e.response?.data === 'string' ? e.response.data : 'No se pudo confirmar la reserva.'
      } finally {
        this.confirmando = false
      }
    },

    reiniciar() {
      this.paso = 1
      this.servicioSel = null
      this.estilistaSel = null
      this.fecha = ''
      this.bloques = []
      this.slotSel = null
      this.contacto = { nombre: '', telefono: '', correo: '' }
      this.codigo = ''
      this.confirmacion = {}
      this.error = ''
      this.$nextTick(() => this.irAReservar())
    }
  }
}
</script>

<style scoped>
.landing {
  background: #f4f9f4;
  min-height: 100vh;
  font-family: 'Manrope', sans-serif;
  color: #173221;
}

/* ═══ HERO ═══ */
.hero {
  /* TODO: para usar una foto real, define background-image aquí (o activa
     usarBannerImagen en negocioConfig.js e impórtala). */
  background: radial-gradient(circle at 30% 20%, #1f6a34 0%, #014421 60%, #00301a 100%);
  color: #fff;
  text-align: center;
}
.hero-overlay {
  max-width: 720px;
  margin: 0 auto;
  padding: 64px 20px 72px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}
.hero-logo {
  width: 84px; height: 84px; object-fit: contain;
  background: rgba(255,255,255,0.12); border-radius: 22px; padding: 10px;
  border: 1px solid rgba(255,255,255,0.18);
}
.hero-title { margin: 8px 0 0; font-size: 34px; font-weight: 800; line-height: 1.15; letter-spacing: -0.5px; }
.hero-sub { margin: 0; font-size: 16px; opacity: 0.85; max-width: 480px; }
.hero-cta {
  margin-top: 12px; background: #fff; color: #014421;
  border: none; border-radius: 999px; padding: 15px 36px;
  font-size: 16px; font-weight: 800; cursor: pointer; font-family: inherit;
  box-shadow: 0 10px 30px rgba(0,0,0,0.25); transition: transform 0.18s ease, box-shadow 0.18s ease;
}
.hero-cta:hover { transform: translateY(-2px); box-shadow: 0 14px 36px rgba(0,0,0,0.3); }

/* ═══ LAYOUT ═══ */
.landing-main {
  max-width: 920px;
  margin: 0 auto;
  padding: 16px 16px 40px;
}
.seccion { padding: 32px 0; }
.seccion-titulo {
  font-size: 22px; font-weight: 800; color: #014421; margin: 0 0 18px;
  text-align: center;
}

/* ═══ SOBRE NOSOTROS ═══ */
.sobre { text-align: center; }
.sobre-texto { max-width: 660px; margin: 0 auto; font-size: 15.5px; line-height: 1.7; color: #45564b; }

/* ═══ INFO ═══ */
.info-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px;
}
.info-item {
  display: flex; gap: 12px; align-items: flex-start;
  background: #fff; border: 1px solid #d9e8db; border-radius: 16px; padding: 18px;
}
.info-ic { color: #014421; flex-shrink: 0; }
.info-label { font-size: 12px; font-weight: 800; color: #4a7c59; text-transform: uppercase; letter-spacing: 0.6px; }
.info-item p { margin: 4px 0 0; font-size: 14px; color: #45564b; }
.wa-link {
  display: inline-flex; align-items: center; gap: 5px; margin-top: 8px;
  font-size: 13px; font-weight: 700; color: #128c4a; text-decoration: none;
}
.wa-link:hover { text-decoration: underline; }

/* ═══ CATÁLOGO ═══ */
.catalogo { display: flex; flex-direction: column; gap: 26px; }
.cat-nombre {
  font-size: 14px; font-weight: 800; color: #1f6a34; margin: 0 0 12px;
  text-transform: uppercase; letter-spacing: 0.8px;
  border-left: 3px solid #1f6a34; padding-left: 10px;
}
.serv-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; }
.serv-card {
  background: #fff; border: 1px solid #d9e8db; border-radius: 16px; padding: 16px;
  display: flex; justify-content: space-between; gap: 12px;
  transition: box-shadow 0.18s ease, transform 0.18s ease;
}
.serv-card:hover { box-shadow: 0 8px 24px rgba(1,68,33,0.1); transform: translateY(-2px); }
.serv-info { min-width: 0; }
.serv-info strong { font-size: 15px; color: #173221; }
.serv-desc { margin: 5px 0; font-size: 12.5px; color: #6b7a72; line-height: 1.4; }
.serv-meta { display: inline-flex; align-items: center; gap: 4px; font-size: 12px; color: #7a8a7f; font-weight: 600; }
.serv-accion { display: flex; flex-direction: column; align-items: flex-end; justify-content: space-between; gap: 8px; flex-shrink: 0; }
.serv-precio { font-weight: 800; color: #014421; white-space: nowrap; }
.btn-mini-pri {
  background: #014421; color: #fff; border: none; border-radius: 10px;
  padding: 8px 14px; font-size: 13px; font-weight: 700; cursor: pointer; font-family: inherit;
  transition: background 0.18s ease;
}
.btn-mini-pri:hover { background: #1f6a34; }

/* ═══ EQUIPO ═══ */
.equipo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 14px; }
.est-card {
  background: #fff; border: 1px solid #d9e8db; border-radius: 16px; padding: 20px 14px;
  display: flex; flex-direction: column; align-items: center; gap: 8px; text-align: center;
}
.est-avatar {
  width: 56px; height: 56px; border-radius: 50%;
  background: linear-gradient(135deg, #a8d8b0, #d4edda);
  display: flex; align-items: center; justify-content: center;
  color: #014421; font-weight: 800; font-size: 22px;
}
.est-avatar.sm { width: 42px; height: 42px; font-size: 17px; flex-shrink: 0; }
.est-card strong { font-size: 14.5px; color: #173221; }
.est-esp { font-size: 12px; color: #7a8a7f; }

/* ═══ WIZARD ═══ */
.wizard-sec { scroll-margin-top: 16px; }
.progreso { max-width: 560px; margin: 0 auto 16px; }
.progreso-info { display: flex; justify-content: space-between; font-size: 13px; font-weight: 700; color: #4f5d52; margin-bottom: 6px; }
.progreso-label { color: #014421; }
.progreso-track { height: 8px; background: #e0ece2; border-radius: 999px; overflow: hidden; }
.progreso-fill { height: 100%; background: linear-gradient(90deg, #1f6a34, #014421); border-radius: 999px; transition: width 0.3s ease; }

.wizard-card {
  max-width: 560px; margin: 0 auto;
  background: #fff; border: 1px solid #d9e8db; border-radius: 22px;
  padding: 26px 24px; box-shadow: 0 6px 24px rgba(1,68,33,0.08);
}
.paso-titulo { margin: 0 0 18px; font-size: 19px; font-weight: 800; color: #014421; }
.sub { font-size: 14px; color: #4f5d52; margin: 4px 0 12px; }
.hint { font-size: 12.5px; color: #6b7a72; margin: 10px 0 0; }

.estado {
  padding: 24px; text-align: center; color: #8a9b8f; font-style: italic;
  background: #f9fcf8; border: 1px dashed #d9e8db; border-radius: 14px; font-size: 14px;
}

.opciones { display: flex; flex-direction: column; gap: 12px; }
.opcion {
  text-align: left; width: 100%;
  border: 1.5px solid #dce8de; background: #fff; border-radius: 14px;
  padding: 15px 16px; cursor: pointer; font-family: inherit; transition: all 0.16s ease;
}
.opcion:hover { border-color: #1f6a34; background: #f6fbf7; }
.opcion.sel { border-color: #014421; background: #f0f7f1; box-shadow: 0 0 0 3px rgba(1,68,33,0.1); }
.opcion-top { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
.opcion-top strong { font-size: 15px; color: #173221; }
.precio { font-weight: 800; color: #014421; }
.opcion-meta { display: inline-flex; align-items: center; gap: 4px; font-size: 12.5px; color: #7a8a7f; font-weight: 600; }
.opcion-est { display: flex; align-items: center; gap: 12px; }

.campo { display: flex; flex-direction: column; gap: 7px; margin-bottom: 16px; }
.campo label { display: inline-flex; align-items: center; gap: 5px; font-size: 13px; font-weight: 700; color: #264434; }
.campo input {
  border: 1.5px solid #d5dfd4; background: #fff; border-radius: 13px;
  padding: 13px 15px; font-size: 15px; color: #173221; outline: none;
  font-family: inherit; box-sizing: border-box; width: 100%;
}
.campo input:focus { border-color: #014421; box-shadow: 0 0 0 4px rgba(1,68,33,0.1); }
.otp-input { text-align: center; letter-spacing: 10px; font-size: 24px; font-weight: 800; }

.paso3-grid { display: grid; grid-template-columns: 200px 1fr; gap: 20px; align-items: start; }
.fecha-col { margin-bottom: 0; }

.slots { display: grid; grid-template-columns: repeat(auto-fill, minmax(76px, 1fr)); gap: 10px; }
.slot {
  border: 1.5px solid #d5dfd4; background: #fff; border-radius: 12px;
  padding: 12px 6px; font-size: 14px; font-weight: 700; color: #173221;
  cursor: pointer; font-family: inherit; transition: all 0.14s ease;
}
.slot:hover { border-color: #1f6a34; background: #f6fbf7; }
.slot.sel { background: #014421; border-color: #014421; color: #fff; }

.nav-btns { display: flex; gap: 10px; margin-top: 22px; }
.btn-pri, .btn-sec {
  flex: 1; display: inline-flex; align-items: center; justify-content: center; gap: 4px;
  padding: 14px; border-radius: 14px; font-size: 15px; font-weight: 700;
  cursor: pointer; font-family: inherit; border: none; transition: all 0.18s ease;
}
.btn-pri { background: #014421; color: #fff; }
.btn-pri:hover:not(:disabled) { background: #1f6a34; }
.btn-pri:disabled { opacity: 0.45; cursor: not-allowed; }
.btn-pri.full { width: 100%; margin-top: 20px; }
.btn-sec { background: #eef3ef; color: #2f4338; border: 1px solid #d7e3d6; }
.btn-sec:hover:not(:disabled) { background: #dde7df; }

.error-msg {
  margin: 14px 0 0; padding: 11px 14px; background: #fdecec; border: 1px solid #f3c2bd;
  border-radius: 11px; font-size: 13px; color: #b42318;
}

.confirmacion { text-align: center; }
.check {
  width: 64px; height: 64px; margin: 0 auto 12px; border-radius: 50%;
  background: #e7f4ea; color: #1d7a3a; font-size: 34px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  animation: pop 0.4s ease;
}
@keyframes pop { 0% { transform: scale(0.4); opacity: 0; } 70% { transform: scale(1.12); } 100% { transform: scale(1); opacity: 1; } }
.resumen { text-align: left; background: #f9fcf8; border: 1px solid #e0ece2; border-radius: 16px; padding: 16px; margin: 18px 0 6px; }
.resumen p { display: flex; justify-content: space-between; gap: 12px; margin: 8px 0; font-size: 14px; }
.resumen span { color: #6b7a72; }
.resumen strong { color: #173221; text-align: right; text-transform: capitalize; }

/* ═══ SKELETONS ═══ */
.skeleton-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; }
.skeleton-card { height: 96px; border-radius: 16px; background: linear-gradient(90deg, #eef4ef 25%, #e2ece4 37%, #eef4ef 63%); background-size: 400% 100%; animation: shimmer 1.3s ease infinite; }
.sk-est { height: 130px; }
@keyframes shimmer { 0% { background-position: 100% 0; } 100% { background-position: -100% 0; } }

/* ═══ FOOTER ═══ */
.landing-footer {
  background: #014421; color: #fff; text-align: center;
  padding: 30px 20px; display: flex; flex-direction: column; align-items: center; gap: 6px;
}
.foot-marca { font-size: 18px; font-weight: 800; letter-spacing: 1px; }
.landing-footer p { margin: 0; font-size: 13px; opacity: 0.8; }
.footer-redes { display: flex; gap: 14px; margin: 8px 0; }
.footer-redes a { color: #fff; opacity: 0.85; transition: opacity 0.18s ease; }
.footer-redes a:hover { opacity: 1; }
.foot-tag { font-size: 12px; opacity: 0.6; margin-top: 4px; }

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .info-grid { grid-template-columns: 1fr; }
  .serv-grid { grid-template-columns: 1fr; }
  .paso3-grid { grid-template-columns: 1fr; gap: 14px; }
  .hero-title { font-size: 28px; }
}
@media (max-width: 480px) {
  .hero-overlay { padding: 48px 18px 56px; }
  .wizard-card { padding: 22px 16px; border-radius: 18px; }
  .nav-btns { flex-direction: column-reverse; }
  .skeleton-grid { grid-template-columns: 1fr; }
}
</style>
