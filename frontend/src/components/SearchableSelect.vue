<template>
  <div class="ss-wrapper" ref="wrapper">

    <!-- TRIGGER -->
    <div
      class="ss-trigger"
      :class="{ open: isOpen, 'has-value': selectedLabel }"
      @click="toggle"
    >
      <span class="ss-value" :class="{ placeholder: !selectedLabel }">
        {{ selectedLabel || placeholder }}
      </span>
      <svg class="ss-chevron" :class="{ rotated: isOpen }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <polyline points="6 9 12 15 18 9" />
      </svg>
    </div>

    <!-- DROPDOWN -->
    <div v-if="isOpen" class="ss-dropdown">

      <div class="ss-search-wrap">
        <svg class="ss-search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input
          ref="searchInput"
          class="ss-search"
          v-model="query"
          :placeholder="searchPlaceholder || 'Buscar...'"
          @keydown.escape="close"
          @keydown.enter.prevent="selectFirst"
        />
        <button v-if="query" class="ss-clear-query" @click.stop="query = ''">×</button>
      </div>

      <ul class="ss-list" ref="list">
        <li
          v-for="opt in filtered"
          :key="opt[valueKey]"
          class="ss-option"
          :class="{ active: opt[valueKey] === modelValue }"
          @click="select(opt)"
        >
          <span class="ss-option-check" v-if="opt[valueKey] === modelValue">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" width="14" height="14">
              <polyline points="20 6 9 17 4 12" />
            </svg>
          </span>
          <span class="ss-option-text">{{ opt[labelKey] }}</span>
        </li>
        <li v-if="!filtered.length" class="ss-empty">
          Sin resultados para "{{ query }}"
        </li>
      </ul>

    </div>

  </div>
</template>

<script>
export default {
  name: 'SearchableSelect',

  props: {
    modelValue:       { type: [Number, String], default: null },
    options:          { type: Array, default: () => [] },
    labelKey:         { type: String, default: 'label' },
    valueKey:         { type: String, default: 'value' },
    placeholder:      { type: String, default: 'Selecciona una opción' },
    searchPlaceholder:{ type: String, default: 'Buscar...' }
  },

  emits: ['update:modelValue'],

  data() {
    return {
      isOpen: false,
      query: ''
    }
  },

  computed: {
    filtered() {
      if (!this.query.trim()) return this.options
      const q = this.query.toLowerCase().trim()
      return this.options.filter(o =>
        String(o[this.labelKey]).toLowerCase().includes(q)
      )
    },

    selectedLabel() {
      if (this.modelValue === null || this.modelValue === '' || this.modelValue === undefined) return ''
      const found = this.options.find(o => o[this.valueKey] === this.modelValue)
      return found ? found[this.labelKey] : ''
    }
  },

  mounted() {
    this._clickHandler = (e) => {
      if (!this.$refs.wrapper?.contains(e.target)) this.close()
    }
    document.addEventListener('click', this._clickHandler)
  },

  beforeUnmount() {
    document.removeEventListener('click', this._clickHandler)
  },

  methods: {
    toggle() {
      this.isOpen ? this.close() : this.open()
    },

    open() {
      this.isOpen = true
      this.query = ''
      this.$nextTick(() => this.$refs.searchInput?.focus())
    },

    close() {
      this.isOpen = false
      this.query = ''
    },

    select(opt) {
      this.$emit('update:modelValue', opt[this.valueKey])
      this.close()
    },

    selectFirst() {
      if (this.filtered.length) this.select(this.filtered[0])
    }
  }
}
</script>

<style scoped>

.ss-wrapper {
  position: relative;
  width: 100%;
  font-family: inherit;
}

/* ── TRIGGER ── */
.ss-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 13px 16px;
  border-radius: 14px;
  border: 1px solid #d7e2da;
  background: #ffffff;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;
  min-height: 48px;
  user-select: none;
}

.ss-trigger:hover {
  border-color: #a4c4ac;
}

.ss-trigger.open {
  border-color: #004518;
  box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08);
}

.ss-value {
  font-size: 14px;
  font-weight: 600;
  color: #1e2a22;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ss-value.placeholder {
  color: #9aada2;
  font-weight: 500;
}

.ss-chevron {
  width: 16px;
  height: 16px;
  color: #6b7a72;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.ss-chevron.rotated {
  transform: rotate(180deg);
}

/* ── DROPDOWN ── */
.ss-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  background: #ffffff;
  border: 1px solid #d7e2da;
  border-radius: 16px;
  box-shadow: 0 12px 36px rgba(0, 69, 24, 0.12), 0 4px 12px rgba(0, 0, 0, 0.08);
  z-index: 500;
  overflow: hidden;
  animation: ssDown 0.15s ease;
}

@keyframes ssDown {
  from { opacity: 0; transform: translateY(-6px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── SEARCH ── */
.ss-search-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-bottom: 1px solid #edf2ee;
}

.ss-search-icon {
  width: 15px;
  height: 15px;
  color: #8a9b8f;
  flex-shrink: 0;
}

.ss-search {
  flex: 1;
  border: none;
  outline: none;
  font-size: 13px;
  font-weight: 500;
  color: #1e2a22;
  background: transparent;
  font-family: inherit;
}

.ss-search::placeholder {
  color: #9aada2;
}

.ss-clear-query {
  background: none;
  border: none;
  color: #8a9b8f;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  padding: 0 2px;
}

.ss-clear-query:hover {
  color: #004518;
}

/* ── LIST ── */
.ss-list {
  list-style: none;
  margin: 0;
  padding: 6px;
  max-height: 220px;
  overflow-y: auto;
}

.ss-list::-webkit-scrollbar {
  width: 4px;
}
.ss-list::-webkit-scrollbar-track {
  background: transparent;
}
.ss-list::-webkit-scrollbar-thumb {
  background: #d0ddd2;
  border-radius: 4px;
}

.ss-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.14s ease;
  font-size: 13.5px;
  font-weight: 500;
  color: #1e2a22;
}

.ss-option:hover {
  background: #f0f7f1;
}

.ss-option.active {
  background: #004518;
  color: #ffffff;
  font-weight: 700;
}

.ss-option-check {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  color: #ffffff;
}

.ss-option-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ss-empty {
  padding: 14px 12px;
  font-size: 13px;
  color: #8a9b8f;
  text-align: center;
  font-style: italic;
}

</style>
