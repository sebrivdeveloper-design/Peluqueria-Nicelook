<template>
  <div class="login-container">

    <!-- LOADING -->
    <div v-if="loading" class="loading-overlay">
      <div class="loader"></div>
      <p>Ingresando...</p>
    </div>

    <!-- IZQUIERDA -->
    <div class="left">

      <div class="logo-wrapper">
        <img
          src="@/assets/logo.png"
          alt="NiceLook Logo"
        />
      </div>

    </div>

    <!-- DERECHA -->
    <div class="right">

      <div class="card">

        <h2 class="access">
          Accede
        </h2>

        <h1>
          Iniciar sesión
        </h1>

        <p class="description">
          Ingresa con tu cuenta de Google para acceder a NiceLook.
        </p>

        <!-- BOTÓN -->
        <div class="g_id_signin"></div>

      </div>

    </div>

  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const loading = ref(false);

const CLIENT_ID =
  "1055219399395-41dgigof08dichfdip9uf0f5affo5vcp.apps.googleusercontent.com";

let tokenClient;

// ==========================================
// LOGIN GOOGLE
// ==========================================

window.handleCredentialResponse = async (response) => {

  try {

    loading.value = true;

    const tokenGoogle = response.credential;

    const res = await axios.post(
      "http://localhost:8080/auth/google",
      {
        token: tokenGoogle
      }
    );

    const tokenJWT = res.data;

    // GUARDAR JWT
    localStorage.setItem("token", tokenJWT);

    // DECODIFICAR
    const payload = JSON.parse(
      atob(tokenJWT.split(".")[1])
    );

    const rol = payload.rol;

    // ==========================================
    // SI ES EMPLEADO → PEDIR CALENDAR ACCESS
    // ==========================================

    if (rol === "EMPLEADO") {

      tokenClient.requestAccessToken();

    }

    // REDIRECCIONES
    setTimeout(() => {

      if (rol === "ADMIN") {
        router.push("/admin");
      }

      else if (rol === "RECEPCIONISTA") {
        router.push("/recepcionista");
      }

      else if (rol === "EMPLEADO") {
        router.push("/empleado");
      }

      else {
        router.push("/cliente");
      }

      loading.value = false;

    }, 1000);

  }

  catch (error) {

    console.error(error);

    loading.value = false;

    alert("Error iniciando sesión");

  }

};

// ==========================================
// MOUNTED
// ==========================================

onMounted(() => {

  localStorage.removeItem("token");

  if (!window.googleInitialized) {

  google.accounts.id.initialize({
    client_id: CLIENT_ID,
    callback: handleCredentialResponse
  })

  window.googleInitialized = true
}

  google.accounts.id.renderButton(
    document.querySelector(".g_id_signin"),
    {
      theme: "outline",
      size: "large",
      width: 280
    }
  );

  // GOOGLE CALENDAR TOKEN
  tokenClient =
    google.accounts.oauth2.initTokenClient({

      client_id: CLIENT_ID,

      scope:
        "https://www.googleapis.com/auth/calendar",

      callback: (tokenResponse) => {

        console.log(
          "ACCESS TOKEN:",
          tokenResponse.access_token
        );

        localStorage.setItem(
          "google_access_token",
          tokenResponse.access_token
        );

      }

    });

});
</script>

<style scoped>

/* ==========================================
   CONTENEDOR
========================================== */

.login-container {
  display: flex;
  min-height: 100dvh;
  overflow: hidden;
  font-family: 'Segoe UI', sans-serif;
}

/* ==========================================
   LOADING
========================================== */

.loading-overlay {
  position: fixed;
  inset: 0;

  background: rgba(0,0,0,0.6);

  backdrop-filter: blur(8px);

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  color: white;

  z-index: 1000;
}

.loader {
  width: 50px;
  height: 50px;

  border-radius: 50%;

  border: 5px solid #ffffff;
  border-top: 5px solid #4e6f52;

  animation: spin 1s linear infinite;

  margin-bottom: 15px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ==========================================
   PANEL IZQUIERDO
========================================== */

.left {
  flex: 1;

  background:
    linear-gradient(
      135deg,
      #4e6f52,
      #2e5e3f
    );

  display: flex;
  justify-content: center;
  align-items: center;

  position: relative;

  overflow: hidden;
}

/* BURBUJAS */

.left::before,
.left::after {

  content: "";

  position: absolute;

  border-radius: 50%;

  background: rgba(255,255,255,0.08);

  animation: float 6s infinite ease-in-out;

}

.left::before {

  width: 400px;
  height: 400px;

  top: -100px;
  left: -100px;

}

.left::after {

  width: 300px;
  height: 300px;

  bottom: -80px;
  right: -80px;

}

@keyframes float {

  0%,100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(20px);
  }

}

/* ==========================================
   LOGO
========================================== */

.logo-wrapper img {

  width: 450px;

  max-width: 90%;

  animation:
    fadeInScale 1.2s ease,
    floatLogo 4s infinite ease-in-out;

  filter:
    drop-shadow(
      0 20px 40px rgba(0,0,0,0.5)
    );

}

@keyframes fadeInScale {

  from {
    opacity: 0;
    transform: scale(0.8);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }

}

@keyframes floatLogo {

  0%,100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-15px);
  }

}

/* ==========================================
   PANEL DERECHO
========================================== */

.right {

  flex: 1;

  display: flex;
  justify-content: center;
  align-items: center;

  background: #ffffff;

}

/* ==========================================
   CARD
========================================== */

.card {

  padding: 60px 40px;

  min-height: 320px;

  border-radius: 25px;

  background:
    rgba(255,255,255,0.92);

  backdrop-filter: blur(20px);

  box-shadow:
    0 25px 60px rgba(0,0,0,0.2);

  text-align: center;

  animation: fadeIn 1s ease;

  transition: 0.3s ease;

}

.card:hover {
  transform: translateY(-5px);
}

@keyframes fadeIn {

  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }

}

.access {

  color: #2e5e3f;

  letter-spacing: 1px;

}

h1 {

  font-size: 42px;

  margin: 10px 0 20px;

  font-weight: 700;

  color: #1f3d2b;

}

.description {

  color: #666;

  margin-bottom: 30px;

  line-height: 1.5;

}

.g_id_signin {

  display: flex;
  justify-content: center;

}

/* ==========================================
   MOBILE
========================================== */

@media (max-width: 768px) {

  .login-container {

    flex-direction: column;

    min-height: 100dvh;

    background:
      linear-gradient(
        135deg,
        #4e6f52,
        #2e5e3f
      );

  }

  .left {

    height: 25%;

    min-height: 140px;

  }

  .right {

    background: transparent;

    flex: 1;

    display: flex;
    align-items: flex-start;

  }

  .card {

    margin:
      -20px
      20px
      20px;

    padding: 40px 20px;

    border-radius: 25px;

    min-height: 280px;

  }

  .logo-wrapper img {
    width: 190px;
  }

  h1 {
    font-size: 30px;
  }

}

</style>