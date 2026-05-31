<template>
  <section class="base-page">

    <div class="page-header">
      <div>
        <h1>Base Diaria</h1>
        <p>
          Registra el dinero con el que inicia la jornada laboral.
        </p>
      </div>
    </div>

    <!-- BASE ACTUAL -->

    <div
      v-if="baseActual"
      class="info-card"
    >
      <div class="info-icon">
        💵
      </div>

      <div>
        <h3>Base registrada hoy</h3>

        <p>
          La caja inició el día con:
        </p>

        <span class="monto">
          ${{ formatearMoneda(baseActual.montoInicial) }}
        </span>
      </div>
    </div>

    <!-- FORMULARIO -->

    <div
      v-if="!baseActual"
      class="form-card"
    >

      <div class="form-header">

        <div class="icon-box">
          💰
        </div>

        <div>
          <h2>Monto Inicial</h2>
          <p>
            Ingresa la cantidad de dinero disponible al comenzar el día.
          </p>
        </div>

      </div>

      <div class="form-group">

        <label for="monto">
          Cantidad
        </label>

        <input
          id="monto"
          v-model="montoInicial"
          type="number"
          min="0"
          placeholder="Ej: 100000"
        />

      </div>

      <button
        class="btn-guardar"
        @click="registrar"
      >
        Registrar Base
      </button>

    </div>

  </section>
</template>

<script>
import baseDiariaService from '@/services/baseDiariaService'

export default {

  data() {
    return {
      montoInicial: '',
      baseActual: null,
      cargando: false
    }
  },

  methods: {

    async cargarBaseDelDia() {

      try {

        const res =
          await baseDiariaService.obtenerBaseDelDia()

        this.baseActual = res.data

      } catch (error) {

        this.baseActual = null

      }

    },

    async registrar() {

      if (!this.montoInicial) {

        alert('Ingrese un monto')
        return

      }

      const confirmar = confirm(
        `¿Confirma registrar $${this.montoInicial} como base del día?`
      )

      if (!confirmar) return

      try {

        await baseDiariaService.registrarBase({
          montoInicial: this.montoInicial
        })

        alert('✅ Base registrada correctamente')

        this.montoInicial = ''

        await this.cargarBaseDelDia()

      } catch (error) {

        alert(
          error.response?.data ||
          'Error al registrar'
        )

      }

    },

    formatearMoneda(valor) {

      return new Intl.NumberFormat(
        'es-CO'
      ).format(valor)

    }

  },

  mounted() {

    this.cargarBaseDelDia()

  }

}
</script>
<style scoped>

.base-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* HEADER */

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: 42px;
  font-weight: 700;
  color: #101828;
  letter-spacing: -1px;
}

.page-header p {
  margin-top: 8px;
  color: #667085;
  font-size: 15px;
}

/* CARD */

.form-card {
  background: white;
  border-radius: 24px;
  border: 1px solid #eaecf0;
  padding: 32px;
  max-width: 650px;
  box-shadow:
    0 4px 20px rgba(0,0,0,0.04);
}

.form-header {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 28px;
}

.icon-box {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: #ecfdf3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
}

.form-header h2 {
  margin: 0;
  color: #101828;
  font-size: 20px;
  font-weight: 700;
}

.form-header p {
  margin-top: 6px;
  color: #667085;
  font-size: 14px;
}

/* INPUT */

.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-group label {
  color: #344054;
  font-size: 14px;
  font-weight: 600;
}

.form-group input {
  height: 52px;
  border: 1px solid #d0d5dd;
  border-radius: 14px;
  padding: 0 16px;
  font-size: 15px;
  transition: all .2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #16a34a;
  box-shadow: 0 0 0 4px rgba(22,163,74,0.12);
}

/* BOTON */

.btn-guardar {
  margin-top: 24px;
  height: 50px;
  border: none;
  border-radius: 14px;
  background: #027a48;
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all .2s ease;
}

.btn-guardar:hover {
  background: #01663d;
}

.btn-guardar:active {
  transform: scale(.98);
}

/* RESPONSIVE */

@media (max-width: 768px) {

  .page-header h1 {
    font-size: 32px;
  }

  .form-card {
    padding: 24px;
  }

}
/* TARJETA BASE ACTUAL */

.info-card {
  display: flex;
  align-items: center;
  gap: 20px;

  background: white;
  border: 1px solid #eaecf0;
  border-radius: 24px;

  padding: 28px;

  max-width: 650px;

  box-shadow:
    0 4px 20px rgba(0,0,0,0.04);
}

.info-icon {
  width: 70px;
  height: 70px;

  border-radius: 18px;

  background: #ecfdf3;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 34px;
}

.info-card h3 {
  margin: 0;
  color: #101828;
  font-size: 20px;
}

.info-card p {
  margin: 6px 0;
  color: #667085;
}

.monto {
  display: inline-block;

  margin-top: 8px;

  font-size: 32px;
  font-weight: 700;

  color: #027a48;
}

</style>