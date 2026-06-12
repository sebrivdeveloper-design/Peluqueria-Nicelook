<template>
  <div class="calendar-wrapper">

    <div class="calendar-legend">
      <span class="legend-item">
        <span class="legend-dot disponible"></span>
        Disponible
      </span>
      <span class="legend-item">
        <span class="legend-dot ocupado"></span>
        Ocupado
      </span>
    </div>

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
    'eventClick'
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

        initialView: window.innerWidth < 640 ? 'timeGridDay' : 'timeGridWeek',

        selectable: this.selectable,

        editable: false,

        slotEventOverlap: false,

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
        }
      }
    }
  }
}
</script>

<style scoped>

.calendar-wrapper {
  background: white;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 24px;

  box-shadow:
    0 2px 8px rgba(1, 68, 33, 0.06),
    0 1px 2px rgba(1, 68, 33, 0.04);
}

/* LEYENDA */

.calendar-legend {
  display: flex;
  gap: 18px;
  margin-bottom: 16px;
  justify-content: flex-end;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  font-weight: 600;
  color: #4f5d52;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 4px;
}

.legend-dot.disponible {
  background: #e7f4ea;
  border: 2px solid #1d7a3a;
}

.legend-dot.ocupado {
  background: #fdecec;
  border: 2px solid #c0392b;
}

/* FULLCALENDAR CUSTOM */

:deep(.fc) {
  font-family: 'Manrope', 'Inter', sans-serif;
}

:deep(.fc-toolbar-title) {
  font-size: 22px;
  font-weight: 700;
  color: #173221;
  text-transform: capitalize;
}

:deep(.fc-button) {
  background: #004518 !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 8px 14px !important;
  font-weight: 600 !important;
  box-shadow: none !important;
}

:deep(.fc-button:hover) {
  background: #0b5d23 !important;
}

:deep(.fc-button-active) {
  background: #0b5d23 !important;
}

:deep(.fc-theme-standard td),
:deep(.fc-theme-standard th),
:deep(.fc-theme-standard .fc-scrollgrid) {
  border-color: #e8f0e9;
}

:deep(.fc-col-header-cell) {
  background: #f0f7f1;
  padding: 8px 0;
}

:deep(.fc-col-header-cell-cushion) {
  color: #4a7c59;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  text-decoration: none;
}

:deep(.fc-timegrid-slot-label-cushion) {
  font-size: 12px;
  color: #6b7a72;
  font-weight: 600;
}

:deep(.fc-timegrid-slot) {
  height: 52px;
}

:deep(.fc-event) {
  border: none;
  padding: 4px 6px;
  border-radius: 8px;
  font-size: 12.5px;
  font-weight: 700;
  cursor: pointer;
  transition: filter 0.15s ease;
}

:deep(.fc-event:hover) {
  filter: brightness(0.96);
}

:deep(.evento-disponible) {
  background: #e7f4ea !important;
  border-left: 3px solid #1d7a3a !important;
  color: #014421 !important;
}

:deep(.evento-disponible .fc-event-main) {
  color: #014421 !important;
}

:deep(.evento-ocupado) {
  background: #fdecec !important;
  border-left: 3px solid #c0392b !important;
  color: #8c1d12 !important;
}

:deep(.evento-ocupado .fc-event-main) {
  color: #8c1d12 !important;
}

:deep(.fc-day-today) {
  background:
    rgba(0, 69, 24, 0.05) !important;
}

@media (max-width: 768px) {
  .calendar-wrapper {
    padding: 14px;
  }

  .calendar-legend {
    justify-content: flex-start;
  }
}

</style>