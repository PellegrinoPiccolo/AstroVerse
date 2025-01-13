<script setup>
  import {useRoute, useRouter} from "vue-router";
  import {ref, watchEffect} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {jwtDecode} from "jwt-decode";
  import {toast} from "vue3-toastify";
  import 'vue3-toastify/dist/index.css';
  import {isValidDescription, isValidTitle, isValidImageType, isValidText} from "@/constants/regexTest.js";
  import Cookies from "js-cookie";
  import "@/assets/styles/SpaceForm.css"

  const route = useRoute()
  const space = ref(null)
  const isSub = ref(false)
  const isAdmin = ref(false)
  const image = ref(null)
  const loadingImage = ref(true)
  const loading = ref(true)
  const router = useRouter()
  const users = ref(null)
  const file = ref(null)
  const inputSelection = ref(null)

  watchEffect(() => {
    const id = route.params.id
    const token = Cookies.get('accessToken')
    apiUrlToken.get(`/space/view/${id}/1`)
        .then((response) => {
          space.value = response.data.message
          const userData = response.data.users
          users.value = userData
          const actualUser = jwtDecode(token)
          userData.map((user) => {
            if (user.id === actualUser.id) {
              isSub.value = true
            }
          })
          if (response.data.idAdmin === actualUser.id) {
            isAdmin.value = true
          }
          loading.value = false
          const splits = space.value.image.split("\\")
          const imageName = splits[2]
          apiUrlToken.get(`/images/space/${space.value.id}/${imageName}`, {
            responseType: 'blob'
          })
              .then(async (response) => {
                image.value = URL.createObjectURL(await response.data)
                loadingImage.value = false
              })
              .catch((error) => {
                if (error.response.data.error) {
                  toast.error(err.response.data.error)
                }
                console.error(error)
                loadingImage.value = false
              })
        })
        .catch((error) => {
          if (error.response) {
            toast.error(err.response.data.error)
          }
          console.error(error)
        })
  })

  const handleChange = () => {
    const id = route.params.id
    if (isAdmin.value) {
      if (!isValidTitle(space.value.title)) {
        toast.error("Titolo non valido")
        return
      } else if (!isValidDescription(space.value.description)) {
        console.log(space.value.description)
        toast.error("Descrizione non valida")
        return
      } else if (file.value !== null && !isValidImageType(file.value)) {
        toast.error("Formato immagine non valido")
        return
      } else if (!isValidText(space.value.argument)) {
        toast.error("Argomento non valido")
        return
      }
      const body = new FormData()
      body.append("titolo", space.value.title)
      body.append("descrizione", space.value.description)
      body.append("argomento", space.value.argument)
      if (file.value) {
        body.append("file", file.value)
      }
      apiTokenForm.post(`/space/modify/${id}`, body)
          .then((response) => {
            toast.success(response.data.message)
          })
          .catch(e => {
            console.error("Errore modifica spazio " + e)
            toast.error("Errore nella modifica dello spazio")
          })
    } else {
      toast.error("Non sei autorizzato a modificare lo spazio")
    }
  }

  const openInputSelection = () => {
    inputSelection.value.click()
  }

  const changeImage = (e) => {
    const newFile = e.target.files[0]
    if (newFile) {
      file.value = newFile
      image.value = URL.createObjectURL(newFile)
    }
  }
</script>

<template>
  <div v-if="loading" style="display: flex; justify-content: center;">
    <VaProgressCircle indeterminate color="#fff"/>
  </div>
  <div v-else class="space-form-container">
    <div>
      <div v-if="loadingImage">
        <VaProgressCircle indeterminate color="#262626"/>
      </div>
      <div v-else class="space-image-form-container">
        <img :src="image" :alt="space.title" />
        <v-btn variant="outlined" color="white" @click="openInputSelection">
          Seleziona immagine
        </v-btn>
        <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
      </div>
    </div>
    <div class="input-container-form-space">
      <h1>Dati Spazio</h1>
      <v-text-field label="Titolo" v-model="space.title" width="400px" color="white" base-color="white"></v-text-field>
      <v-select
          label="Argomento"
          clearable
          :items="['Economia', 'Matematica', 'Fisica', 'Scienze della terra', 'Biologia', 'Geografia','Storia', 'Giornalismo', 'Politica', 'Sport', 'Programmazione', 'Intelligenza Artificiale', 'Web Design', 'Cybersecurity', 'Cloud Computing', 'Data Science', 'Crypto', 'Videogiochi', 'Teatro', 'Chitarra', 'Musica', 'Montagna',  'Fotografia', 'Cucina', 'Letteratura', 'Cinema', 'Anime', 'Filosofia']"
          variant="outlined"
          v-model="space.argument"
      ></v-select>
      <v-textarea label="Descrizione dello spazio" row-height="25" rows="5" variant="outlined" auto-grow shaped width="400px" v-model="space.description"></v-textarea>
      <v-btn color="white" variant="outlined" @click="handleChange">
        Salva Modifiche
      </v-btn>
    </div>
  </div>
</template>