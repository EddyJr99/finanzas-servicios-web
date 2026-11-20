// =========================================================
// SGFP - Cuentas, categorias y medios de pago conectados a la API
// =========================================================

(function () {
  var sesion = SGFP.exigirSesion();
  if (!sesion) return;
  SGFP.prepararMenu(sesion);

  function mensaje(id, texto, tipo) {
    var el = document.getElementById(id);
    el.textContent = texto || '';
    el.className = 'panel-msg ' + (tipo || '');
  }

  function filaVacia(columnas, texto) {
    return '<tr class="empty-row"><td colspan="' + columnas + '">' + texto + '</td></tr>';
  }

  // ---------- Cuentas del usuario ----------
  async function cargarCuentas() {
    var r = await SGFP.pedir('GET', '/api/cuentas?idUsuario=' + sesion.idUsuario);
    var tbody = document.getElementById('cuentas-tbody');
    if (!r.ok) { mensaje('cuenta-msg', SGFP.mensajeError(r), 'error'); return; }
    tbody.innerHTML = r.datos.length === 0 ? filaVacia(4, 'Aún no tienes cuentas.') :
      r.datos.map(function (c) {
        return '<tr><td>' + SGFP.escapar(c.nombreCuenta) + '</td><td>' + SGFP.escapar(c.tipoCuenta) +
          '</td><td>' + SGFP.escapar(c.banco) + '</td><td><button class="btn-del" data-id="' + c.idCuenta +
          '">Eliminar</button></td></tr>';
      }).join('');
  }

  document.getElementById('cuenta-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    var r = await SGFP.pedir('POST', '/api/cuentas', {
      nombreCuenta: document.getElementById('cta-nombre').value.trim(),
      tipoCuenta: document.getElementById('cta-tipo').value,
      banco: document.getElementById('cta-banco').value.trim(),
      idUsuario: sesion.idUsuario
    });
    if (!r.ok) { mensaje('cuenta-msg', SGFP.mensajeError(r), 'error'); return; }
    e.target.reset();
    mensaje('cuenta-msg', 'Cuenta "' + r.datos.nombreCuenta + '" creada.', 'ok');
    cargarCuentas();
  });

  document.getElementById('cuentas-tbody').addEventListener('click', async function (e) {
    var b = e.target.closest('.btn-del');
    if (!b || !window.confirm('¿Eliminar esta cuenta?')) return;
    var r = await SGFP.pedir('DELETE', '/api/cuentas/' + b.dataset.id);
    // Si la cuenta tiene movimientos la API responde 409 y se muestra el motivo.
    mensaje('cuenta-msg', r.ok ? 'Cuenta eliminada.' : SGFP.mensajeError(r), r.ok ? 'ok' : 'error');
    cargarCuentas();
  });

  // ---------- Categorias ----------
  async function cargarCategorias() {
    var r = await SGFP.pedir('GET', '/api/categorias');
    var tbody = document.getElementById('categorias-tbody');
    if (!r.ok) { mensaje('categoria-msg', SGFP.mensajeError(r), 'error'); return; }
    tbody.innerHTML = r.datos.length === 0 ? filaVacia(3, 'No hay categorías.') :
      r.datos.map(function (c) {
        return '<tr><td>' + SGFP.escapar(c.nombreCategoria) + '</td><td><span class="tipo-chip ' + c.tipo + '">' +
          (c.tipo === 'INGRESO' ? 'Ingreso' : 'Gasto') + '</span></td><td><button class="btn-del" data-id="' +
          c.idCategoria + '">Eliminar</button></td></tr>';
      }).join('');
  }

  document.getElementById('categoria-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    var r = await SGFP.pedir('POST', '/api/categorias', {
      nombreCategoria: document.getElementById('cat-nombre').value.trim(),
      tipo: document.getElementById('cat-tipo').value
    });
    if (!r.ok) { mensaje('categoria-msg', SGFP.mensajeError(r), 'error'); return; }
    e.target.reset();
    mensaje('categoria-msg', 'Categoría "' + r.datos.nombreCategoria + '" creada.', 'ok');
    cargarCategorias();
  });

  document.getElementById('categorias-tbody').addEventListener('click', async function (e) {
    var b = e.target.closest('.btn-del');
    if (!b || !window.confirm('¿Eliminar esta categoría?')) return;
    var r = await SGFP.pedir('DELETE', '/api/categorias/' + b.dataset.id);
    mensaje('categoria-msg', r.ok ? 'Categoría eliminada.' : SGFP.mensajeError(r), r.ok ? 'ok' : 'error');
    cargarCategorias();
  });

  // ---------- Medios de pago ----------
  async function cargarMedios() {
    var r = await SGFP.pedir('GET', '/api/medios-pago');
    var tbody = document.getElementById('medios-tbody');
    if (!r.ok) { mensaje('medio-msg', SGFP.mensajeError(r), 'error'); return; }
    tbody.innerHTML = r.datos.length === 0 ? filaVacia(2, 'No hay medios de pago.') :
      r.datos.map(function (m) {
        return '<tr><td>' + SGFP.escapar(m.nombreMedioPago) + '</td><td>' + SGFP.escapar(m.entidadFinanciera) + '</td></tr>';
      }).join('');
  }

  document.getElementById('medio-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    var r = await SGFP.pedir('POST', '/api/medios-pago', {
      nombreMedioPago: document.getElementById('med-nombre').value.trim(),
      entidadFinanciera: document.getElementById('med-entidad').value.trim()
    });
    if (!r.ok) { mensaje('medio-msg', SGFP.mensajeError(r), 'error'); return; }
    e.target.reset();
    mensaje('medio-msg', 'Medio de pago "' + r.datos.nombreMedioPago + '" creado.', 'ok');
    cargarMedios();
  });

  cargarCuentas();
  cargarCategorias();
  cargarMedios();
})();
