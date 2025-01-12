<script setup>
import {defineProps, onMounted, ref} from "vue";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import {faChevronDown, faChevronUp, faPenToSquare, faX} from "@fortawesome/free-solid-svg-icons";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from "vue3-toastify";
  import 'vue3-toastify/dist/index.css';
  import Cookies from "js-cookie";
  import {jwtDecode} from "jwt-decode";
  import {isValidImageType, isValidPostText} from "@/constants/regexTest.js";

  const {post, src} = defineProps({
    post: {
      type: Object,
      required: true,
      default: null
    },
    src: {
      type: String,
      required: false,
      default: null
    },
  })

  const isVoted = ref(null)
  const isOpen = ref(false)
  const isCreator = ref(false)
  const newFile = ref(null)
  const inputSelection = ref(null)
  const postRef = ref(post)
  let newPost = {...post, image: null};
  const localPost = ref(newPost)
  const srcRef = ref(null)
  const imageDeleted = ref(false)

  onMounted(() => {
    newPost = {...postRef.value}
    localPost.value = {...newPost}
    const token = Cookies.get("accessToken")
    const user = jwtDecode(token)
    if (post.userData.id === user.id) {
      isCreator.value = true
    }
    apiUrlToken.get(`/post/get-vote/${post.id}`)
        .then((response) => {
          isVoted.value = response.data.message
        })
        .catch((error) => {
          console.error(error)
        })
  })

  const vote = (voteType) => {
    const body = new FormData()
    body.append("vote", voteType)
    apiTokenForm.post(`/post/vote/${post.id}`, body)
        .then((response) => {
          if (isVoted.value !== voteType) {
            isVoted.value = voteType
          } else {
            isVoted.value = null
          }
        })
        .catch((error) => {
          console.error("Errore votazione post " + post.id + " codice di errore: " + JSON.stringify(error.response))
          toast.error("Errore nella votazione del post");
        })
  }

  const handleChangePost = () => {
    if(!isValidPostText(localPost.value.testo)) {
      toast.error('Testo del post non valido')
      return
    } if(newFile.value !== null && newFile.value !== undefined && !isValidImageType(newFile.value)) {
      toast.error('Immagine del post non valida')
      return
    }
    const body = new FormData()
    body.append("testo", localPost.value.testo)
    if(newFile.value !== null && newFile.value !== undefined) {
      body.append("file", newFile.value)
    } else if(imageDeleted.value) {
      body.append("imageDeleted", imageDeleted.value)
    }
    apiTokenForm.post(`/post/modify/${post.id}`, body)
        .then((response) => {
          isOpen.value = false
          srcRef.value = imageDeleted.value ? '' : localPost.value.image
          postRef.value.testo = localPost.value.testo
          postRef.value.image = localPost.value.image
        })
        .catch((error) => {
          console.error(error)
        })
  }
  const openInputSelection = () => {
    inputSelection.value.click()
  }

  const changeImage = (e) => {
    const file = e.target.files[0]
    if (file && isValidImageType(file)) {
      localPost.value.image = URL.createObjectURL(file)
      newFile.value = file
      imageDeleted.value = false
    } else {
      toast.error('Immagine del post non valida')
    }
  }

  const cancelImage = () => {
    newFile.value = undefined
    localPost.value.image = ''
    imageDeleted.value = true
  }
</script>

<template>
  <div>
    <button @click="isOpen = true" v-if="isCreator">
      <FontAwesomeIcon :icon="faPenToSquare"/>
    </button>
    <img v-if="(srcRef !== null && srcRef !== undefined) ? (srcRef !== '' ? srcRef : false) : src" :src="srcRef ? srcRef : src" :alt="post.testo">
    <p>{{post.testo}}</p>
    <p>{{post.userData.username}}</p>
    <div>
      <button @click="vote(true)">
        <FontAwesomeIcon :icon="faChevronUp" :style="{color: isVoted !== null && isVoted === true ? 'red' : 'black'}"></FontAwesomeIcon>
      </button>
      <button @click="vote(false)">
        <FontAwesomeIcon :icon="faChevronDown" :style="{color: isVoted !== null && isVoted === false ? 'red' : 'black'}"></FontAwesomeIcon>
      </button>
    </div>
  </div>
  <VaModal v-model="isOpen" ok-text="Salva modifiche" cancel-text="Annulla" @ok="handleChangePost">
    <h1>Modifica il tuo post:</h1>
    <div>
      <button @click="cancelImage">
        <FontAwesomeIcon :icon="faX"/>
      </button>
      <img :src="(localPost.image !== null && localPost.image !== undefined) ? localPost.image : src" :alt="post.testo" v-if="(localPost.image === null || localPost.image === undefined) ? src : (localPost.image !== '' ? localPost.image : false)">
      <button @click="openInputSelection">
        Seleziona immagine
      </button>
    </div>
    <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
    <label for="testo">Testo del post</label>
    <input type="text" id="testo" name="testo" placeholder="Inserisci il testo per questo post" v-model="localPost.testo">
  </VaModal>
</template>