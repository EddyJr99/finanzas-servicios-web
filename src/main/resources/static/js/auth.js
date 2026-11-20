// =========================================================
// SGFP - login y registro conectados a la API (/api/auth)
// =========================================================

function isValidEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

function setError(id, message) {
  var el = document.getElementById(id);
  if (el) el.textContent = message || '';
}

function showBanner(id, message, type) {
  var el = document.getElementById(id);
  if (!el) return;
  el.textContent = message;
  el.className = 'form-banner show ' + (type || '');
}

// En la version publicada en Render se muestra la cuenta de demostracion.
(function () {
  var pista = document.getElementById('demo-hint');
  if (pista && /onrender\.com$/.test(window.location.hostname)) pista.hidden = false;
})();

// ---------- Login ----------
(function () {
  var form = document.getElementById('login-form');
  if (!form) return;
  if (SGFP.sesion()) { window.location.replace('dashboard.html'); return; }

  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    var email = document.getElementById('login-email').value.trim();
    var password = document.getElementById('login-password').value;
    var valid = true;

    setError('error-login-email', '');
    setError('error-login-password', '');

    if (!email) {
      setError('error-login-email', 'Ingresa tu correo electrónico.');
      valid = false;
    } else if (!isValidEmail(email)) {
      setError('error-login-email', 'Ese correo no parece válido.');
      valid = false;
    }
    if (!password) {
      setError('error-login-password', 'Ingresa tu contraseña.');
      valid = false;
    }
    if (!valid) return;

    // El servicio responde 200 si la autenticacion es correcta y 401 si no.
    var r = await SGFP.pedir('POST', '/api/auth/login', { correo: email, clave: password });
    if (!r.ok) {
      showBanner('login-banner', SGFP.mensajeError(r), 'error');
      return;
    }
    SGFP.guardarSesion(r.datos);
    showBanner('login-banner', r.datos.mensaje + '. Redirigiendo a tu panel…', 'success');
    setTimeout(function () { window.location.href = 'dashboard.html'; }, 900);
  });
})();

// ---------- Registro ----------
(function () {
  var form = document.getElementById('registro-form');
  if (!form) return;

  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    var nombre = document.getElementById('reg-nombre').value.trim();
    var email = document.getElementById('reg-email').value.trim();
    var password = document.getElementById('reg-password').value;
    var password2 = document.getElementById('reg-password2').value;
    var valid = true;

    ['error-reg-nombre', 'error-reg-email', 'error-reg-password', 'error-reg-password2']
      .forEach(function (id) { setError(id, ''); });

    if (!nombre) { setError('error-reg-nombre', 'Ingresa tu nombre completo.'); valid = false; }
    if (!email) {
      setError('error-reg-email', 'Ingresa tu correo electrónico.'); valid = false;
    } else if (!isValidEmail(email)) {
      setError('error-reg-email', 'Ese correo no parece válido.'); valid = false;
    }
    if (!password) {
      setError('error-reg-password', 'Crea una contraseña.'); valid = false;
    } else if (password.length < 8) {
      setError('error-reg-password', 'Usa al menos 8 caracteres.'); valid = false;
    }
    if (!password2) {
      setError('error-reg-password2', 'Confirma tu contraseña.'); valid = false;
    } else if (password && password !== password2) {
      setError('error-reg-password2', 'Las contraseñas no coinciden.'); valid = false;
    }
    if (!valid) return;

    var r = await SGFP.pedir('POST', '/api/auth/registro', { nombre: nombre, correo: email, clave: password });
    if (!r.ok) {
      showBanner('registro-banner', SGFP.mensajeError(r), 'error');
      return;
    }
    showBanner('registro-banner', r.datos.mensaje, 'success');
    form.reset();
    setTimeout(function () { window.location.href = 'index.html'; }, 1400);
  });
})();
