<template>
  <div class="login-container">

    <!-- LOADING OVERLAY -->
    <div v-if="loading" class="loading-overlay">
      <div class="loader"></div>
      <p>Ingresando...</p>
    </div>

    <!-- IZQUIERDA -->
    <div class="left">
      <div class="logo-wrapper">
        <img src="@/assets/logo.png" alt="NiceLook Logo" />
      </div>
    </div>

    <!-- DERECHA -->
    <div class="right">
      <div class="card">
        <h2 class="access">Accede</h2>
        <h1>Iniciar sesión</h1>

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

window.handleCredentialResponse = async (response) => {
  try {
    loading.value = true;

    const tokenGoogle = response.credential;

    const res = await axios.post("http://localhost:8080/auth/google", {
      token: tokenGoogle
    });

    const tokenJWT = res.data;

    // GUARDAR TOKEN
    localStorage.setItem("token", tokenJWT);

    // DECODIFICAR TOKEN
    const payload = JSON.parse(atob(tokenJWT.split('.')[1]));
    const rol = payload.rol;

    // REDIRECCIÓN LIMPIA
    setTimeout(() => {
      if (rol === "ADMIN") router.push("/admin");
      else if (rol === "RECEPCIONISTA") router.push("/recepcionista");
      else if (rol === "EMPLEADO") router.push("/empleado");
      else router.push("/cliente");
    }, 1200);

  } catch (error) {
      console.error(error);

    setTimeout(() => {
      loading.value = false;
      router.push("/cliente");
    }, 1200);
  }
};

onMounted(() => {
  // LIMPIAR SESIÓN SI ENTRA AL LOGIN
  localStorage.removeItem("token");

  google.accounts.id.initialize({
    client_id: "1055219399395-41dgigof08dichfdip9uf0f5affo5vcp.apps.googleusercontent.com",
    callback: handleCredentialResponse
  });

  google.accounts.id.renderButton(
    document.querySelector(".g_id_signin"),
    {
      theme: "outline",
      size: "large",
      width: 280
    }
  );
});
</script>

<style scoped>
/* FIX REAL PARA MOBILE HEIGHT */
.login-container {
  display: flex;
  min-height: 100dvh; /* arregla el bug del 100vh */
  font-family: 'Segoe UI', sans-serif;
  overflow: hidden;
}

/* LOADING */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
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
  border: 5px solid #fff;
  border-top: 5px solid #4e6f52;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* IZQUIERDA */
.left {
  flex: 1;
  background: linear-gradient(135deg, #4e6f52, #2e5e3f);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
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
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(20px); }
}

/* LOGO */
.logo-wrapper img {
  width: 450px;
  max-width: 90%;
  animation: fadeInScale 1.2s ease, floatLogo 4s infinite ease-in-out;
  filter: drop-shadow(0 20px 40px rgba(0,0,0,0.5));
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
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #ffffff; /* BLANCO en PC */
}

/* CARD */
.card {
  padding: 60px 40px;
  min-height: 320px;
  border-radius: 25px;
  backdrop-filter: blur(20px);
  background: rgba(255,255,255,0.92);
  box-shadow: 0 25px 60px rgba(0,0,0,0.2);
  text-align: center;
  animation: fadeIn 1s ease;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.access {
  color: #2e5e3f;
  letter-spacing: 1px;
}

h1 {
  font-size: 42px;
  margin: 10px 0 30px;
  font-weight: 700;
  color: #1f3d2b;
}

/* BOTÓN GOOGLE */
.g_id_signin {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

/* MOBILE PERFECTO */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    min-height: 100dvh;
    background: linear-gradient(135deg, #4e6f52, #2e5e3f);
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
    margin: -20px 20px 20px;
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