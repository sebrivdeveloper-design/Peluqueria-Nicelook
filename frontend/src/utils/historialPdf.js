// Informe PDF del historial por estilista (vía impresión del navegador → Guardar como PDF).
// Mismo patrón visual que el informe de PagosView.

function moneda(v) {
  return Number(v || 0).toLocaleString('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0
  })
}

function fechaLarga(f) {
  if (!f) return ''
  return new Date(f + 'T00:00:00').toLocaleDateString('es-CO', {
    day: 'numeric', month: 'long', year: 'numeric'
  })
}

function horaCorta(hora) {
  if (!hora) return ''
  const [h, m] = String(hora).split(':').map(Number)
  const d = new Date(); d.setHours(h, m)
  return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true })
}

/**
 * @param {{estilista:string, desde:string, hasta:string, filas:Array, resumen:Object}} data
 * @returns {boolean} false si el navegador bloqueó la ventana emergente
 */
export function historialPdf({ estilista, desde, hasta, filas, resumen }) {
  const r = resumen || {}

  const filasHtml = (filas || []).map(f => `
    <tr>
      <td>${fechaLarga(f.fecha)}</td>
      <td>${horaCorta(f.hora)}</td>
      <td>${f.cliente || ''}</td>
      <td>${f.servicio || ''}</td>
      <td class="num">${moneda(f.valorServicio)}</td>
      <td class="num">${moneda(f.valorArrendamiento)}</td>
      <td class="num">${moneda(f.valorAPagarEstilista)}</td>
      <td>${f.estadoPago === 'pagado' ? 'Pagado' : 'Pendiente'}</td>
    </tr>`).join('')

  const generado = new Date().toLocaleString('es-CO')

  const html = `
    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <title>Historial de estilista - NiceLook</title>
      <style>
        * { font-family: 'Segoe UI', Arial, sans-serif; box-sizing: border-box; }
        body { margin: 32px; color: #173221; }
        .head { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #014421; padding-bottom: 14px; margin-bottom: 20px; }
        .marca { font-size: 26px; font-weight: 800; color: #014421; letter-spacing: 0.5px; }
        .sub { font-size: 13px; color: #5f6f66; margin-top: 2px; }
        .meta { text-align: right; font-size: 12px; color: #5f6f66; }
        .cards { display: flex; gap: 14px; margin: 18px 0; flex-wrap: wrap; }
        .card { flex: 1; min-width: 130px; border: 1px solid #d9e8db; border-radius: 12px; padding: 12px 14px; }
        .card .lbl { font-size: 11px; color: #5f6f66; }
        .card .val { font-size: 18px; font-weight: 800; color: #014421; }
        h2 { font-size: 15px; color: #014421; margin: 22px 0 8px; }
        table { width: 100%; border-collapse: collapse; font-size: 12px; }
        th { background: #f0f7f1; color: #2f5a3a; text-align: left; padding: 8px 10px; text-transform: uppercase; font-size: 9.5px; letter-spacing: 0.6px; border-bottom: 2px solid #d9e8db; }
        td { padding: 7px 10px; border-bottom: 1px solid #edf2ee; }
        .num { text-align: right; font-variant-numeric: tabular-nums; }
        tr:nth-child(even) td { background: #fafdfb; }
        .foot { margin-top: 26px; font-size: 10.5px; color: #8a9b8f; border-top: 1px solid #e8f0e9; padding-top: 10px; }
        @media print { body { margin: 12mm; } }
      </style>
    </head>
    <body>
      <div class="head">
        <div>
          <div class="marca">NiceLook</div>
          <div class="sub">Historial de servicios — ${estilista || ''}</div>
        </div>
        <div class="meta">
          <div><strong>Período:</strong> ${fechaLarga(desde)} — ${fechaLarga(hasta)}</div>
          <div>Servicios: ${r.totalServicios || 0}</div>
        </div>
      </div>

      <div class="cards">
        <div class="card"><div class="lbl">Total facturado</div><div class="val">${moneda(r.totalFacturado)}</div></div>
        <div class="card"><div class="lbl">Arrendamiento (salón)</div><div class="val">${moneda(r.totalArrendamiento)}</div></div>
        <div class="card"><div class="lbl">A pagar al estilista</div><div class="val">${moneda(r.totalAPagarEstilista)}</div></div>
      </div>

      <h2>Detalle de servicios</h2>
      <table>
        <thead>
          <tr>
            <th>Fecha</th><th>Hora</th><th>Cliente</th><th>Servicio</th>
            <th class="num">Valor</th><th class="num">Arrendamiento</th><th class="num">A pagar</th><th>Pago</th>
          </tr>
        </thead>
        <tbody>${filasHtml}</tbody>
      </table>

      <div class="foot">
        Documento generado por el sistema NiceLook el ${generado}.
        Los servicios cobrados muestran el reparto congelado; los pendientes, una estimación con tarifas vigentes.
      </div>
    </body>
    </html>`

  const win = window.open('', '_blank')
  if (!win) return false
  win.document.write(html)
  win.document.close()
  win.focus()
  setTimeout(() => win.print(), 350)
  return true
}
