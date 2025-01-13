<script setup>
  import {onMounted, ref} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import Cookies from "js-cookie";
  import {isNotOrNull, isValidEmail, isValidPassword, isValidText, isValidUsername} from "@/constants/regexTest.js";
  import router from "@/index.js";
  import "@/assets/styles/User.css"

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
  <div v-if="user" class="user-data-section-container">
    <h1>Dati Personali</h1>
    <v-text-field label="Nome" v-model="user.nome" width="400px" color="white" base-color="white"></v-text-field>
    <v-text-field label="Cognome" v-model="user.cognome" width="400px" color="white" base-color="white"></v-text-field>
    <v-text-field label="Username" v-model="user.username" width="400px" color="white" base-color="white"></v-text-field>
    <v-text-field label="Email" v-model="user.email" width="400px" color="white" base-color="white"></v-text-field>
    <div class="user-data-space-post-container">
      <div>
        <p>Numero di spazi a cui partecipi:</p>
        <p>{{user.userSpaces.length}}</p>
      </div>
      <div>
        <p>Numero di post creati:</p>
        <p>{{user.userPosts.length}}</p>
      </div>
    </div>
    <div class="user-data-section-container">
      <h1>Cambia Password</h1>
      <v-text-field label="Password Corrente" v-model="passwords.oldPassword" width="400px" color="white" base-color="white" type="password"></v-text-field>
      <v-text-field label="Nuova Password" v-model="passwords.newPassword" width="400px" color="white" base-color="white" type="password"></v-text-field>
      <v-text-field label="Conferma Password" v-model="passwords.confirmNewPassword" width="400px" color="white" base-color="white" type="password"></v-text-field>
    </div>
    <v-btn variant="outlined" color="white" @click="changeUserData">
      Salva
    </v-btn>
    <div class="logout-section">
      <p>Effettua il </p>
      <v-btn variant="tonal" color="red" @click="logout">
        Logout
      </v-btn>
    </div>
  </div>
</template>