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
  import "@/assets/styles/Post.css";
  import "@/assets/styles/Modal.css"


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
  const upVotes = ref(0)
  const downVotes = ref(0)

  onMounted(() => {
    newPost = {...postRef.value}
    localPost.value = {...newPost}
    const token = Cookies.get("accessToken")
    const user = jwtDecode(token)
    if (post.userData.id === user.id) {
      isCreator.value = true
    }
    upVotes.value = post.votes.filter((vote) => vote.vote === true).length
    downVotes.value = post.votes.filter((vote) => vote.vote !== true).length
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
            if (isVoted.value) {
              upVotes.value = upVotes.value - 1
            } else if (isVoted.value === false) {
              downVotes.value = downVotes.value - 1
            }
            isVoted.value = voteType
            if (voteType) {
              upVotes.value = upVotes.value + 1
            } else {
              downVotes.value = downVotes.value + 1
            }
          } else {
            if (isVoted.value) {
              upVotes.value = upVotes.value - 1
            } else {
              downVotes.value = downVotes.value - 1
            }
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

  const handleCloseModal = () => {
    localPost.value.image = undefined
    localPost.value.testo = post.testo
  }
</script>

<template>
  <VaCard :style="`background-color: #262626;`">
    <VaCardTitle class="post-content">
      <p>{{post.userData.username}}</p>
      <button @click="isOpen = true" v-if="isCreator">
        <FontAwesomeIcon :icon="faPenToSquare" class="icon-post"/>
      </button>
    </VaCardTitle>
    <VaCardContent class="post-content">
      {{post.testo}}
    </VaCardContent>
    <img class="post-image" v-if="(srcRef !== null && srcRef !== undefined) ? (srcRef !== '' ? srcRef : false) : src" :src="srcRef ? srcRef : src" :alt="post.testo">
    <VaCardContent class="vote-container">
      <div>
        <button @click="vote(true)">
          <FontAwesomeIcon :icon="faChevronUp" class="icon-vote" :style="{color: isVoted !== null && isVoted === true ? '#429172' : 'white'}"></FontAwesomeIcon>
        </button>
        <p>{{upVotes}}</p>
      </div>
      <div>
        <button @click="vote(false)">
          <FontAwesomeIcon :icon="faChevronDown" class="icon-vote" :style="{color: isVoted !== null && isVoted === false ? 'red' : 'white'}"></FontAwesomeIcon>
        </button>
        <p>{{downVotes}}</p>
      </div>
    </VaCardContent>
  </VaCard>
  <VaModal v-model="isOpen" ok-text="Salva modifiche" cancel-text="Annulla" @ok="handleChangePost" class="modal" @cancel="handleCloseModal">
    <h1>Modifica il tuo post:</h1>
    <div class="image-section">
      <button @click="cancelImage" class="close-icon" >
        <FontAwesomeIcon :icon="faX"/>
      </button>
      <img :src="(localPost.image !== null && localPost.image !== undefined) ? localPost.image : src" :alt="post.testo" v-if="(localPost.image === null || localPost.image === undefined) ? src : (localPost.image !== '' ? localPost.image : false)">
      <v-btn variant="outlined" color="black" @click="openInputSelection">
        Seleziona un immagine per il tuo post
      </v-btn>
    </div>
    <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
    <v-textarea
        label="Testo del post"
        row-height="25"
        rows="5"
        variant="outlined"
        auto-grow
        shaped
        width="400px"
        v-model="localPost.testo"
    ></v-textarea>
  </VaModal>
</template>