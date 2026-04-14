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

    const token = response.credential;

    const res = await axios.post("http://localhost:8080/auth/google", {
      token: token
    });

    const rol = res.data.rol.nombreRol;

    setTimeout(() => {
      if (rol === "ADMIN") router.push("/admin");
      else if (rol === "RECEPCIONISTA") router.push("/recepcionista");
      else if (rol === "EMPLEADO") router.push("/empleado");
    }, 1200);

  } catch (error) {
    setTimeout(() => {
      router.push("/cliente");
    }, 1200);
  }
};

onMounted(() => {
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
.login-container {
  display: flex;
  height: 100vh;
  font-family: 'Segoe UI', sans-serif;
  overflow: hidden;
}

/* 🔥 LOADING */
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

/* 🔥 BURBUJAS ANIMADAS */
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
  width: 600px; /* 🔥 MÁS GRANDE */
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

/* DERECHA */
.right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to bottom right, #ffffff, #f8f8f8);
}

/* 🔥 CARD GLASS */
.card {
  padding: 50px;
  border-radius: 20px;
  backdrop-filter: blur(15px);
  background: rgba(255,255,255,0.7);
  box-shadow: 0 20px 50px rgba(0,0,0,0.1);
  text-align: center;
  animation: fadeIn 1s ease;
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
}

/* BOTÓN */
.g_id_signin {
  display: flex;
  justify-content: center;
}

/* RESPONSIVE */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .left {
    height: 40%;
  }

  .right {
    height: 60%;
  }

  .logo-wrapper img {
    width: 350px;
  }
}
</style>