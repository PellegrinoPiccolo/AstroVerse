<script setup>
  import {ref} from "vue";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import {isNotOrNull, isValidImageType, isValidPostText} from "@/constants/regexTest.js";
  import {apiTokenForm} from "@/constants/ApiUrl.js";
  import {useRoute, useRouter} from "vue-router";

  const post = ref({
    text: '',
    file: null,
  })

  const route = useRoute()
  const inputSelection = ref(null)
  const router = useRouter()
  const image = ref(null)

  const handleImage = (e) => {
    const file = e.target.files[0]
    if (file) {
      post.value.image = URL.createObjectURL(file)
      image.value = file
    }
  }

  const openSelectionImage = () =>{
    inputSelection.value.click()
  }
  const handleCreate = () => {
    if (!isValidPostText(post.value.text)) {
      toast.error("Testo del post non valido")
      return
    } else if (!isNotOrNull(post.value.file) && !isValidImageType(post.value.file)) {
      toast.error("Immagine non valida")
      return
    }
    const body = new FormData()
    body.append("testo", post.value.text)
    if (post.value.file !== null) {
      body.append("file", post.value.file)
    }
    apiTokenForm.post(`/post/create/${route.params.id}`, body)
        .then((response) => {
          router.push(`/astroverse/space/${route.params.id}`)
        })
        .catch((error) => {
          console.log("Errore creazione post " + error)
          toast.error("Errore nella creazione del post")
        })
  }

  const changeImage = (e) => {
    const file = e.target.files[0]
    if (file) {
      image.value = URL.createObjectURL(file)
      post.value.file = file
    }
  }
</script>

<template>
  <div>
    <h1>Crea il tuo post:</h1>
    <img v-if="image" :src="image" alt="Immagine post"/>
    <button @click="openSelectionImage">Seleziona un immagine per il tuo post</button>
    <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
    <label for="text">Testo del post</label>
    <input type="text" id="text" name="text" placeholder="Inserisci il testo per questo post" v-model="post.text">
    <button @click="handleCreate">
      Crea Post
    </button>
  </div>
</template>