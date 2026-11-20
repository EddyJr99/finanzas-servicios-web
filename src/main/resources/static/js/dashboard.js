// =========================================================
// SGFP - Dashboard de movimientos conectado a la API
// Usa: /api/resumen, /api/movimientos, /api/categorias, /api/cuentas, /api/medios-pago
// =========================================================

(function () {
  var sesion = SGFP.exigirSesion();
  if (!sesion) return;
  SGFP.prepararMenu(sesion);
  document.getElementById('saludo').textContent =
    'Hola, ' + sesion.nombre.split(' ')[0] + '. Estos son tus ingresos y gastos.';

  var movimientos = [];
  var categorias = [];
  var cuentas = [];
  var medios = [];

  var tbody = document.getElementById('moves-tbody');
  var searchInput = document.getElementById('search-input');
  var categoryFilter = document.getElementById('filter-category');
  var listaMsg = document.getElementById('lista-msg');

  // Carga todo lo que necesita la pantalla desde la API.
  async function cargar() {
    var id = sesion.idUsuario;
    var r = await Promise.all([
      SGFP.pedir('GET', '/api/movimientos?idUsuario=' + id),
      SGFP.pedir('GET', '/api/resumen?idUsuario=' + id),
      SGFP.pedir('GET', '/api/categorias'),
      SGFP.pedir('GET', '/api/cuentas?idUsuario=' + id),
      SGFP.pedir('GET', '/api/medios-pago')
    ]);
    var fallo = r.find(function (x) { return !x.ok; });
    if (fallo) {
      listaMsg.textContent = SGFP.mensajeError(fallo);
      listaMsg.className = 'panel-msg error';
      return;
    }
    movimientos = r[0].datos;
    categorias = r[2].datos;
    cuentas = r[3].datos;
    medios = r[4].datos;
    pintarResumen(r[1].datos);
    llenarFiltro();
    render();
  }

  function pintarResumen(resumen) {
    document.getElementById('stat-ingresos').textContent = SGFP.moneda(resumen.totalIngresos);
    document.getElementById('stat-gastos').textContent = SGFP.moneda(resumen.totalGastos);
    document.getElementById('stat-balance').textContent = SGFP.moneda(resumen.balance);

    // Barras de gasto por categoria (solo las de tipo GASTO).
    var gastos = resumen.porCategoria.filter(function (c) { return c.tipo === 'GASTO'; });
    var total = Number(resumen.totalGastos) || 0;
    var contenedor = document.getElementById('category-bars');
    contenedor.innerHTML = '';
    if (gastos.length === 0) {
      contenedor.innerHTML = '<p class="panel-msg">Todavía no tienes gastos registrados.</p>';
      return;
    }
    gastos.forEach(function (c) {
      var pct = total > 0 ? Math.round((Number(c.total) / total) * 100) : 0;
      var fila = document.createElement('div');
      fila.className = 'cat-bar-row';
      fila.innerHTML =
        '<div class="top"><span>' + SGFP.escapar(c.nombreCategoria) + '</span><span class="amt">' +
        SGFP.moneda(c.total) + '</span></div>' +
        '<div class="cat-bar-track"><div class="cat-bar-fill" style="width:' + pct + '%"></div></div>';
      contenedor.appendChild(fila);
    });
  }

  function llenarFiltro() {
    var actual = categoryFilter.value;
    categoryFilter.innerHTML = '<option value="">Todas las categorías</option>';
    categorias.forEach(function (c) {
      var op = document.createElement('option');
      op.value = c.idCategoria;
      op.textContent = c.nombreCategoria;
      categoryFilter.appendChild(op);
    });
    categoryFilter.value = actual;
  }

  function render() {
    var query = (searchInput.value || '').toLowerCase().trim();
    var cat = categoryFilter.value;

    var filtrados = movimientos.filter(function (m) {
      var coincideTexto = !query || m.descripcion.toLowerCase().indexOf(query) !== -1;
      var coincideCat = !cat || m.categorias.some(function (c) { return String(c.idCategoria) === cat; });
      return coincideTexto && coincideCat;
    });

    tbody.innerHTML = '';
    if (filtrados.length === 0) {
      tbody.innerHTML = '<tr class="empty-row"><td colspan="6">' +
        (movimientos.length === 0 ? 'Aún no tienes movimientos. Usa “+ Nuevo movimiento”.' :
          'No hay movimientos que coincidan con la búsqueda.') + '</td></tr>';
      return;
    }
    filtrados.forEach(function (m) {
      var tipo = m.tipo === 'INGRESO' ? 'ingreso' : 'gasto';
      var signo = tipo === 'ingreso' ? '+ ' : '− ';
      var chips = m.categorias.map(function (c) {
        return '<span class="cat-chip">' + SGFP.escapar(c.nombreCategoria) + '</span>';
      }).join(' ') || '<span class="cat-chip">Sin categoría</span>';
      var tr = document.createElement('tr');
      tr.innerHTML =
        '<td>' + SGFP.fecha(m.fecha) + '</td>' +
        '<td class="desc-cell">' + SGFP.escapar(m.descripcion) + '</td>' +
        '<td>' + chips + '</td>' +
        '<td>' + SGFP.escapar(m.nombreCuenta) + '</td>' +
        '<td style="text-align:right;"><span class="amount ' + tipo + '">' + signo + SGFP.moneda(m.montoTotal) + '</span></td>' +
        '<td><button class="btn-del" data-id="' + m.idMovimiento + '" title="Eliminar">Eliminar</button></td>';
      tbody.appendChild(tr);
    });
  }

  // Eliminar un movimiento (DELETE /api/movimientos/{id}).
  tbody.addEventListener('click', async function (e) {
    var boton = e.target.closest('.btn-del');
    if (!boton) return;
    if (!window.confirm('¿Eliminar este movimiento?')) return;
    var r = await SGFP.pedir('DELETE', '/api/movimientos/' + boton.dataset.id);
    if (!r.ok) {
      listaMsg.textContent = SGFP.mensajeError(r);
      listaMsg.className = 'panel-msg error';
      return;
    }
    listaMsg.textContent = 'Movimiento eliminado.';
    listaMsg.className = 'panel-msg ok';
    cargar();
  });

  searchInput.addEventListener('input', render);
  categoryFilter.addEventListener('change', render);

  // ---------- Modal: nuevo movimiento (POST /api/movimientos) ----------
  var overlay = document.getElementById('modal-overlay');
  var form = document.getElementById('movement-form');
  var botonIngreso = document.getElementById('type-ingreso');
  var botonGasto = document.getElementById('type-gasto');
  var tipoActual = 'GASTO';

  function llenarSelect(id, lista, valor, texto) {
    var select = document.getElementById(id);
    select.innerHTML = '';
    lista.forEach(function (x) {
      var op = document.createElement('option');
      op.value = x[valor];
      op.textContent = texto(x);
      select.appendChild(op);
    });
  }

  // Solo se muestran las categorias del tipo elegido (ingreso o gasto).
  function setTipo(tipo) {
    tipoActual = tipo;
    botonIngreso.classList.toggle('active', tipo === 'INGRESO');
    botonGasto.classList.toggle('active', tipo === 'GASTO');
    var delTipo = categorias.filter(function (c) { return c.tipo === tipo; });
    llenarSelect('mov-categoria', delTipo, 'idCategoria', function (c) { return c.nombreCategoria; });
  }

  function banner(texto, tipo) {
    var b = document.getElementById('modal-banner');
    b.textContent = texto;
    b.className = 'form-banner' + (texto ? ' show ' + tipo : '');
  }

  function abrirModal() {
    form.reset();
    banner('');
    document.getElementById('error-mov-descripcion').textContent = '';
    document.getElementById('error-mov-monto').textContent = '';
    // Fecha de hoy en la hora local (toISOString usa UTC y en la noche daria el dia siguiente).
    var hoy = new Date();
    hoy.setMinutes(hoy.getMinutes() - hoy.getTimezoneOffset());
    document.getElementById('mov-fecha').value = hoy.toISOString().slice(0, 10);
    llenarSelect('mov-cuenta', cuentas, 'idCuenta', function (c) { return c.nombreCuenta; });
    llenarSelect('mov-medio', medios, 'idMedioPago', function (m) { return m.nombreMedioPago; });
    setTipo(tipoActual);
    if (cuentas.length === 0) {
      banner('Primero crea una cuenta en “Cuentas y categorías”.', 'error');
    }
    overlay.classList.add('open');
    document.getElementById('mov-descripcion').focus();
  }
  function cerrarModal() { overlay.classList.remove('open'); }

  document.getElementById('open-modal').addEventListener('click', abrirModal);
  document.getElementById('close-modal').addEventListener('click', cerrarModal);
  overlay.addEventListener('click', function (e) { if (e.target === overlay) cerrarModal(); });
  botonIngreso.addEventListener('click', function () { setTipo('INGRESO'); });
  botonGasto.addEventListener('click', function () { setTipo('GASTO'); });

  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    var descripcion = document.getElementById('mov-descripcion').value.trim();
    var monto = Number(document.getElementById('mov-monto').value);
    var idCategoria = Number(document.getElementById('mov-categoria').value);
    var idCuenta = Number(document.getElementById('mov-cuenta').value);
    var idMedio = Number(document.getElementById('mov-medio').value);

    var valido = true;
    document.getElementById('error-mov-descripcion').textContent = '';
    document.getElementById('error-mov-monto').textContent = '';
    if (!descripcion) {
      document.getElementById('error-mov-descripcion').textContent = 'Escribe una descripción.';
      valido = false;
    }
    if (!monto || monto <= 0) {
      document.getElementById('error-mov-monto').textContent = 'Ingresa un monto mayor a 0.';
      valido = false;
    }
    if (!idCategoria) { banner('No hay categorías de este tipo. Créala en “Cuentas y categorías”.', 'error'); valido = false; }
    if (!idCuenta || !idMedio) { valido = false; }
    if (!valido) return;

    var r = await SGFP.pedir('POST', '/api/movimientos', {
      fecha: document.getElementById('mov-fecha').value,
      descripcion: descripcion,
      montoTotal: monto,
      idCuenta: idCuenta,
      idMedioPago: idMedio,
      categorias: [{ idCategoria: idCategoria, montoAsignado: monto }]
    });
    if (!r.ok) {
      banner(SGFP.mensajeError(r), 'error');
      return;
    }
    cerrarModal();
    listaMsg.textContent = 'Movimiento guardado.';
    listaMsg.className = 'panel-msg ok';
    cargar();
  });

  cargar();
})();
