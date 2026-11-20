// =========================================================
// SGFP - funciones comunes: sesion del usuario y llamadas a la API
// =========================================================

var SGFP = (function () {
  var CLAVE_SESION = 'sgfp.sesion';

  // La sesion se guarda en el navegador despues de un login correcto
  // (id, nombre y correo del usuario; nunca la contrasena).
  function sesion() {
    try { return JSON.parse(localStorage.getItem(CLAVE_SESION)); } catch (e) { return null; }
  }
  function guardarSesion(usuario) {
    localStorage.setItem(CLAVE_SESION, JSON.stringify({
      idUsuario: usuario.idUsuario, nombre: usuario.nombre, correo: usuario.correo
    }));
  }
  function cerrarSesion() {
    localStorage.removeItem(CLAVE_SESION);
    window.location.href = 'index.html?salir=1';
  }
  // Las paginas internas llaman esto: si no hay sesion, vuelven al login.
  function exigirSesion() {
    var s = sesion();
    if (!s) { window.location.replace('index.html'); }
    return s;
  }

  // Hace la peticion HTTP a la API y devuelve { ok, estado, datos }.
  async function pedir(metodo, ruta, cuerpo) {
    var opciones = { method: metodo, headers: { 'Content-Type': 'application/json' } };
    if (cuerpo !== undefined) opciones.body = JSON.stringify(cuerpo);
    var respuesta = await fetch(ruta, opciones);
    var texto = await respuesta.text();
    var datos = texto ? JSON.parse(texto) : null;
    return { ok: respuesta.ok, estado: respuesta.status, datos: datos };
  }

  // Convierte un error de la API en un texto para mostrar.
  function mensajeError(r) {
    if (!r.datos) return 'Ocurrio un error (' + r.estado + ')';
    var detalles = (r.datos.detalles || []).join(' · ');
    return r.datos.mensaje + (detalles ? ': ' + detalles : '');
  }

  function moneda(n) {
    return '$' + Number(n || 0).toLocaleString('es-CO', { maximumFractionDigits: 0 });
  }
  function fecha(iso) {
    var d = new Date(iso + 'T00:00:00');
    return d.toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }).replace('.', '');
  }
  function escapar(t) {
    return String(t == null ? '' : t).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  // Pinta el nombre del usuario en la barra lateral y activa "Cerrar sesion" y el menu movil.
  function prepararMenu(s) {
    var iniciales = s.nombre.split(' ').filter(Boolean).slice(0, 2)
      .map(function (p) { return p[0].toUpperCase(); }).join('');
    document.getElementById('user-avatar').textContent = iniciales;
    document.getElementById('user-name').textContent = s.nombre;
    document.getElementById('user-mail').textContent = s.correo;
    document.getElementById('logout').addEventListener('click', function (e) {
      e.preventDefault(); cerrarSesion();
    });
    var boton = document.getElementById('menu-toggle');
    var lateral = document.getElementById('sidebar');
    if (boton && lateral) {
      boton.addEventListener('click', function () {
        var abierto = lateral.classList.toggle('open');
        boton.setAttribute('aria-expanded', abierto ? 'true' : 'false');
      });
    }
  }

  return {
    sesion: sesion, guardarSesion: guardarSesion, cerrarSesion: cerrarSesion,
    exigirSesion: exigirSesion, pedir: pedir, mensajeError: mensajeError,
    moneda: moneda, fecha: fecha, escapar: escapar, prepararMenu: prepararMenu
  };
})();
