<script setup>

import {ref} from "vue";
import {apiTokenForm} from "@/constants/ApiUrl.js";
import {toast} from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';
import {useRouter} from "vue-router";
import {isValidDescription, isValidImageType, isValidText, isValidTitle} from "@/constants/regexTest.js";
import "@/assets/styles/SpaceForm.css"

const space = ref({
    title: '',
    argument: null,
    description: '',
    image: null
  })

  const image = ref(null)
  const selectionImage = ref(null)

  const handleImage = (e) => {
    const file = e.target.files[0]
    if (file) {
      space.value.image = URL.createObjectURL(file)
      image.value = file
    }
  }

  const openSelectionImage = () =>{
    selectionImage.value.click()
  }

  const router = useRouter()

  const createSpace = () => {
    if (!isValidTitle(space.value.title)) {
      toast.error("Titolo non valido")
      return
    } else if (!isValidText(space.value.argument)) {
      toast.error("Argomento non valido")
      return
    } else if (!isValidDescription(space.value.description)) {
      toast.error("Descrizione non valida")
      return
    } else if (!isValidImageType(image.value)) {
      toast.error("Immagine non valida")
      return
    }
    const body = new FormData()
    body.append("titolo", space.value.title)
    body.append("argomento", space.value.argument)
    body.append("descrizione", space.value.description)
    if (image.value !== null) {
      body.append("file", image.value)
    }
    apiTokenForm.post('/space/create', body)
        .then((response) => {
          router.push("/astroverse")
        })
        .catch((error) => {
          if(error.response.data.error) {
            toast.error(error.response.data.error)
          }
          console.error(error)
        })
  }
</script>

<template>
  <div class="space-form-container">
    <div class="space-image-form-container">
      <img v-if="image" :src="space.image" alt="Immagine spazio"/>
      <v-btn variant="outlined" color="white" @click="openSelectionImage">
        Seleziona immagine
      </v-btn>
    </div>
    <input type="file" id="image" name="image" ref="selectionImage" style="display: none" @change="handleImage" accept="image/png, image/jpeg">
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
      <v-btn color="white" variant="outlined" @click="createSpace">
        Crea Spazio
      </v-btn>
    </div>
  </div>
</template>