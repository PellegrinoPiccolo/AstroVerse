<script setup>
  import {onMounted, ref} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import Cookies from "js-cookie";
  import {isNotOrNull, isValidEmail, isValidPassword, isValidText, isValidUsername} from "@/constants/regexTest.js";
  import router from "@/index.js";

  const loading = ref(true)
  const user = ref(null)
  const passwords = ref({
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  })

  onMounted(async () => {
    await apiUrlToken.get('/auth/view-account')
        .then((response) => {
            user.value = response.data.user
          console.log(response.data.user)
        })
        .catch((error) => {
          console.error(error.response)
          if(error.response.data.error) {
            toast.error(error.response.data.error)
          }
        })
    loading.value = false
  })

  const changeUserData = () => {
    if (!isValidText(user.value.nome)) {
      toast.error("Nome non valido");
      return
    } else if (!isValidText(user.value.cognome)) {
      toast.error("Cognome non valido")
      return
    } else if (!isValidEmail(user.value.email)) {
      toast.error("Email non valida")
      return
    } else if (!isValidUsername(user.value.username)) {
      toast.error("Username non valido")
      return
    } else if (!isNotOrNull(passwords.value.newPassword) && !isValidPassword(passwords.value.newPassword)) {
      toast.error("Password non valida")
      return
    } else if (passwords.value.newPassword !== passwords.value.confirmNewPassword) {
      toast.error("Le password non coincidono")
      return
    }
    const body = new FormData();
    body.append("nome", user.value.nome);
    body.append("cognome", user.value.cognome);
    body.append("email", user.value.email);
    body.append("username", user.value.username);
    body.append("psw", passwords.value.newPassword);
    body.append("confirmPassword", passwords.value.confirmNewPassword);
    body.append("vecchiaPassword", passwords.value.oldPassword);
    apiTokenForm.post('/auth/change-user-data', body)
        .then((response) => {
          const token = response.data.accessToken
          const message = response.data.message
          toast.success(message)
          Cookies.set("accessToken", token, {expires: 30})
        })
        .catch((error) => {
          console.error(error)
          if (error.response.data.error) {
            toast.error(error.response.data.error);
          }
        })
  }

  const logout = () => {
    apiUrlToken.post('/auth/logout')
        .then((response) => {
          Cookies.remove("accessToken")
          router.push("/")
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
  <div v-if="loading">
    <VaProgressCircle indeterminate color="#262626"/>
  </div>
  <div v-if="user">
    <h1>Dati Personali</h1>
    <label for="nome">Nome</label>
    <input type="text" id="nome" placeholder="Il tuo nome" v-model="user.nome">
    <label for="cognome">Cognome</label>
    <input type="text" id="cognome" placeholder="Il tuo cognome" v-model="user.cognome">
    <label for="email">Email</label>
    <input type="email" id="email" placeholder="La tua email" v-model="user.email">
    <label for="username">Username</label>
    <input type="text" id="username" placeholder="Il tuo username" v-model="user.username">
    <div>
      <div>
        <p>Numero di spazi a cui partecipi</p>
        <p>{{user.userSpaces.length}}</p>
      </div>
      <div>
        <p>Numero di post a cui partecipi</p>
        <p>{{user.userPosts.length}}</p>
      </div>
    </div>
    <div>
      <h1>Cambia Password</h1>
      <label id="oldPassword">Password Corrente</label>
      <input type="password" id="oldPassword" placeholder="Inserisci vecchia password" v-model="passwords.oldPassword">
      <label id="nuovaPassword">Nuova Password</label>
      <input type="password" id="nuovaPassword" placeholder="Inserisci nuova password" v-model="passwords.newPassword">
      <label id="confermaNuovaPassword">Conferma nuova Password</label>
      <input type="password" placeholder="Inserisci conferma nuova password" id="confermaNuovaPassword" v-model="passwords.confirmNewPassword">
    </div>
    <button @click="changeUserData">
      Salva
    </button>
    <div>
      <p>Effettua il </p>
      <button @click="logout">Logout</button>
    </div>
  </div>
</template>