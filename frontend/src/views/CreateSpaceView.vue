<script setup>

import {ref, watchEffect} from "vue";
import {apiTokenForm} from "@/constants/ApiUrl.js";
import {toast} from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';
import {useRouter} from "vue-router";
import {isValidDescription, isValidImageType, isValidText, isValidTitle} from "@/constants/regexTest.js";

const space = ref({
    title: '',
    argument: '',
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
  <div>
    <img v-if="image" :src="space.image" alt="Immagine spazio"/>
    <button @click="openSelectionImage">
      Seleziona immagine
    </button>
    <input type="file" id="image" name="image" ref="selectionImage" style="display: none" @change="handleImage" accept="image/png, image/jpeg">
    <label for="title">Titolo</label>
    <input type="text" id="title" name="title" placeholder="Inserisci il titolo dello spazio" v-model="space.title">
    <label for="argument">Argomento</label>
    <select v-model="space.argument">
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
    <input type="text" id="description" placeholder="Inserisci una descrizione" v-model="space.description">
    <button @click="createSpace">
      Crea Spazio
    </button>
  </div>
</template>

<style scoped>

</style>