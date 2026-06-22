// ============================================================================
// CONTENIDO EDITABLE DE LA LANDING PÚBLICA DE RESERVAS (NICE LOOK)
// ----------------------------------------------------------------------------
// Este es el ÚNICO lugar donde el dueño del negocio edita los textos y datos de
// contacto que se muestran en /reservar. No requiere tocar componentes.
// (No afecta la lógica de reserva ni el catálogo real de servicios/estilistas,
//  que se cargan desde el backend.)
// ============================================================================

export const negocio = {
  nombre: 'NICE LOOK',
  // TODO: reemplazar con el eslogan/tagline real del negocio
  tagline: 'Tu estilo, nuestra pasión',

  // Texto del hero (encabezado principal)
  heroTitulo: 'Reserva tu cita en NICE LOOK',
  heroSubtitulo: 'Agenda en línea en menos de un minuto, sin llamadas ni esperas.',

  // TODO: reemplazar con la descripción real del negocio
  sobreNosotros:
    'En NICE LOOK creemos que verse bien es sentirse bien. Somos un salón ' +
    'donde estilistas independientes y apasionados ofrecen servicios de ' +
    'cabello, color, cejas, pestañas y bienestar en un ambiente cálido y ' +
    'profesional. Reserva con tu estilista favorito y vive una experiencia ' +
    'pensada para ti.',

  // Información práctica (TODO: reemplazar con datos reales)
  contacto: {
    direccion: 'Cra. 00 # 00-00, tu ciudad',
    horario: 'Lun a Sáb · 8:00 a.m. – 7:00 p.m.',
    telefono: '+57 300 000 0000',
    // Se usa para el botón de WhatsApp (solo dígitos, con indicativo de país)
    whatsapp: '573000000000',
    correo: 'notificaciones.nicelook@gmail.com'
  },

  // Redes sociales (dejar el valor en '' para ocultar el ícono correspondiente)
  redes: {
    instagram: '', // ej. 'https://instagram.com/nicelook'
    facebook: '',
    tiktok: ''
  }
}

// Imagen de portada del hero.
// TODO: para usar una FOTO real del local, importa el archivo en
// ReservarLandingView.vue y pásalo como fondo. Por defecto se usa un degradado
// de marca (sin dependencia de imagen, carga instantánea).
export const usarBannerImagen = false
