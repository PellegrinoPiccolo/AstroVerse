<script setup>
  import {ref, watchEffect} from "vue";
  import {useRoute, useRouter} from 'vue-router';
  import {apiTokenForm} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import {isNotOrNull, isValidEmail, isValidPassword, isValidText, isValidUsername} from "@/constants/regexTest.js";
  import Cookies from "js-cookie";

  const current = ref('login');
  const user = ref({
    email: '',
    username: '',
    nome: '',
    cognome: '',
    password: '',
    confermaPassword: ''
  })
  const route = useRoute()
  const router = useRouter()
  watchEffect(() => {
    if (route.query.page !== 'login' && route.query.page !== 'registration') {
      router.push('/')
    }
    current.value = (route.query.page || 'login').trim().toLowerCase();
  })

  const loginUser = async () => {
    if(isNotOrNull(user.value.email) || isNotOrNull(user.value.password)) {
      toast.error("Nessun campo può essere vuoto");
      return
    }
    const body = new URLSearchParams()
    body.append("email", user.value.email)
    body.append("password", user.value.password)
    await apiTokenForm.post('/auth/login', body)
        .then(async (response) => {
          const token = await response.data.accessToken
          await Cookies.set("accessToken", token)
          if(Cookies.get("accessToken")) {
            await router.push("/astroverse")
          }
        })
        .catch((error) => {
          console.error(error.response)
          if(error.response.data.error) {
            toast.error(error.response.data.error);
          }
        })
  }

  const registrationUser = async () => {
    if(!isValidText(user.value.nome) || !isValidText(user.value.cognome)) {
      toast.error("Nome o cognome non valido");
      return
    } else if(!isValidUsername(user.value.username)) {
      toast.error("Username non valido");
      return
    } else if(!isValidEmail(user.value.email)) {
      toast.error("Email non valida");
      return
    } else if(user.value.password !== user.value.confermaPassword) {
      toast.error("Le password devono coincidere");
      return
    } else if(!isValidPassword(user.value.password)) {
      toast.error("La password non è valida");
      return
    }
    const body = new URLSearchParams();
    body.append("nome", user.value.nome)
    body.append("cognome", user.value.cognome)
    body.append("username", user.value.username)
    body.append("email", user.value.email)
    body.append("password", user.value.password)
    body.append("confermaPassowrd", user.value.confermaPassword)
    await apiTokenForm.post('/auth/registration', body)
        .then(async (response) => {
          const token = await response.data.accessToken
          await Cookies.set('accessToken', token)
          await router.push("/astroverse")
        })
        .catch((error) => {
          console.error(error)
          toast.error("Registrazione non andata a buon fine");
        })
  }
</script>

<template>
  <div class="form-container">
    <div class="form" v-if="current === 'login'">
      <h1>Login</h1>
      <label for="email">Email</label>
      <input type="email" id="email" placeholder="Inserisci email" v-model="user.email">
      <label for="password">Password</label>
      <input type="password" id="password" placeholder="Inserisci password" v-model="user.password">
      <button @click="loginUser">Login</button>
    </div>
    <div class="form" v-else>
      <h1>Registrazione</h1>
      <label for="nome">Nome</label>
      <input type="text" id="nome" placeholder="Inserisci nome" v-model="user.nome">
      <label for="cognome">Cognome</label>
      <input type="text" id="cognome" placeholder="Inserisci cognome" v-model="user.cognome">
      <label for="username">Username</label>
      <input type="text" id="username" placeholder="Inserisci username" v-model="user.username">
      <label for="email">Email</label>
      <input type="email" id="email" placeholder="Inserisci email" v-model="user.email">
      <label for="password"></label>
      <input type="password" id="password" placeholder="Inserisci password" v-model="user.password">
      <label for="conferma-password"></label>
      <input type="password" id="conferma-password" placeholder="Conferma la password" v-model="user.confermaPassword">
      <button @click="registrationUser">Registrazione</button>
    </div>
    <p v-if="current === 'login'">Non hai un account? <RouterLink :to="{query: {page: 'registration'}}">Registrati</RouterLink></p>
    <p v-else>Hai già un account? <RouterLink :to="{query: {page: 'login'}}">Login</RouterLink></p>
  </div>
</template>

<style scoped>

</style>