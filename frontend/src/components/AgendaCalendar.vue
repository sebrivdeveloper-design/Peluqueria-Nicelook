<template>
  <div class="calendar-wrapper">

    <FullCalendar :options="calendarOptions" />

  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue3'

import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'

import esLocale from '@fullcalendar/core/locales/es'

export default {

  name: 'AgendaCalendar',

  components: {
    FullCalendar
  },

  props: {

    eventos: {
      type: Array,
      default: () => []
    },

    selectable: {
      type: Boolean,
      default: false
    }

  },

  emits: [
    'select',
    'eventClick',
    'datesSet'
  ],

  computed: {

    calendarOptions() {
      return {
        plugins: [
          dayGridPlugin,
          timeGridPlugin,
          interactionPlugin
        ],
        locale: esLocale,
        initialView: 'timeGridWeek',
        selectable: this.selectable,
        editable: false,
        height: 'auto',
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
          today: 'Hoy',
          month: 'Mes',
          week: 'Semana',
          day: 'Día'
        },
        slotMinTime: '08:00:00',
        slotMaxTime: '21:00:00',
        allDaySlot: false,
        events: this.eventos,
        select: (info) => {
          this.$emit('select', info)
        },
        eventClick: (info) => {
          this.$emit('eventClick', info)
        },
        datesSet: (info) => {
          this.$emit('datesSet', info)
        },
      }
    }
  }
}
</script>

<style scoped>

.calendar-wrapper {
  background: white;
  border-radius: 24px;
  padding: 24px;

  box-shadow:
    0 10px 30px rgba(0,0,0,0.05);
}

/* FULLCALENDAR CUSTOM */

:deep(.fc) {
  font-family: 'Inter', sans-serif;
}

:deep(.fc-toolbar-title) {
  font-size: 22px;
  font-weight: 700;
  color: #1e2a22;
}

:deep(.fc-button) {
  background: #004518 !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 8px 14px !important;
  font-weight: 600 !important;
}

:deep(.fc-button:hover) {
  background: #0b5d23 !important;
}

:deep(.fc-timegrid-slot) {
  height: 60px;
}

:deep(.fc-event) {
  border: none;
  padding: 4px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
}

:deep(.evento-disponible) {
  background: #28a745 !important;
}

:deep(.evento-ocupado) {
  background: #dc3545 !important;
}

:deep(.fc-day-today) {
  background:
    rgba(0, 69, 24, 0.05) !important;
}

</style>