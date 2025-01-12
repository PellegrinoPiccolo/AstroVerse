<script setup>
  import {useRoute, useRouter} from "vue-router";
  import {ref, watchEffect} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {jwtDecode} from "jwt-decode";
  import {toast} from "vue3-toastify";
  import 'vue3-toastify/dist/index.css';
  import {isValidDescription, isValidTitle, isValidImageType, isValidText} from "@/constants/regexTest.js";
  import Cookies from "js-cookie";

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
  <div v-if="loading">
    <VaProgressCircle indeterminate color="#262626"/>
  </div>
  <div v-else>
    <div>
      <div v-if="loadingImage">
        <VaProgressCircle indeterminate color="#262626"/>
      </div>
      <div v-else>
        <img :src="image" :alt="space.title" />
        <button @click="openInputSelection">
          Seleziona immagine
        </button>
        <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
      </div>
    </div>
    <div>
      <h1>Dati Spazio</h1>
      <label for="title">Titolo</label>
      <input type="text" id="title" name="title" placeholder="Inserisci il titolo dello spazio" v-model="space.title">
      <label for="argument">Argomento</label>
      <select v-model="space.argument">
        <option selected :value="space.argument">{{space.argument.charAt(0).toUpperCase() + space.argument.slice(1)}}</option>
        <option value="Economia">Economia</option>
        <option value="Matematca">Matematica</option>
        <option value="Fisica">Fisica</option>
        <option value="Scienze-della-terra">Scienze della terra</option>
        <option value="Biologia">Biologia</option>
        <option value="Geografia">Geografia</option>
        <option value="Storia">Storia</option>
        <option value="Giornalismo">Giornalismo</option>
        <option value="Politica">Politica</option>
        <option value="Sport">Sport</option>
        <option value="Programmazione">Programmazione</option>
        <option value="Intelligenza-artificiale">Intelligenza Artificiale</option>
        <option value="Web-design">Web Design</option>
        <option value="Cybersecurity">Cybersecurity</option>
        <option value="Cloud-computing">Cloud Computing</option>
        <option value="Data-science">Data Science</option>
        <option value="Crypto">Crypto</option>
        <option value="Videogiochi">Videogiochi</option>
        <option value="Teatro">Teatro</option>
        <option value="Chitarra">Chitarra</option>
        <option value="Musica">Musica</option>
        <option value="Montagna">Montagna</option>
        <option value="Fotografia">Fotografia</option>
        <option value="Cucina">Cucina</option>
        <option value="Letteratura">Letteratura</option>
        <option value="Cinema">Cinema</option>
        <option value="Anime">Anime</option>
        <option value="Filosofia">Filosofia</option>
      </select>
      <label for="description">Descrizione</label>
      <input type="text" id="description" placeholder="Inserisci la descrizione dello spazio" v-model="space.description">
      <button @click="handleChange">
        Salva Modifiche
      </button>
    </div>
  </div>
</template>