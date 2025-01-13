<script setup>
  import {ref, watchEffect} from "vue";
  import {useRoute, useRouter} from 'vue-router';
  import {apiTokenForm} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import {isNotOrNull, isValidEmail, isValidPassword, isValidText, isValidUsername} from "@/constants/regexTest.js";
  import Cookies from "js-cookie";
  import "@/assets/styles/Auth.css"

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
    Object.keys(user.value).forEach(key => {
      user.value[key] = '';
    });
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
          await Cookies.set("accessToken", token, {expires: 30})
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
          await Cookies.set('accessToken', token, {expires: 30})
          await router.push("/astroverse")
        })
        .catch((error) => {
          console.error(error)
          if (error.response.data.error) {
            toast.error(error.response.data.error);
          }
        })
  }
</script>

<template>
  <div class="form-container">
    <div class="form" v-if="current === 'login'">
      <h1>Login</h1>
      <v-text-field label="Email" v-model="user.email" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field type="password" label="Password" v-model="user.password" width="400px" color="white" base-color="white"></v-text-field>
      <v-btn prepend-icon="astroverse-icon" variant="outlined" color="white" @click="loginUser">
        Accedi
      </v-btn>
      <p class="link-container">Non hai un account? <RouterLink :to="{query: {page: 'registration'}}" class="routerLink">Registrati</RouterLink></p>
    </div>
    <div class="form" v-else>
      <h1>Registrazione</h1>
      <v-text-field label="Nome" v-model="user.nome" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field label="Cognome" v-model="user.cognome" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field label="Username" v-model="user.username" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field label="Email" v-model="user.email" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field type="password" label="Password" v-model="user.password" width="400px" color="white" base-color="white"></v-text-field>
      <v-text-field type="password" label="Conferma Password" v-model="user.confermaPassword" width="400px" color="white" base-color="white"></v-text-field>
      <v-btn prepend-icon="astroverse-icon" variant="outlined" color="white" @click="registrationUser">
        Registrati
      </v-btn>
      <p class="link-container">Hai già un account? <RouterLink :to="{query: {page: 'login'}}" class="routerLink">Accedi</RouterLink></p>
    </div>
  </div>
</template>