<script setup>
  import {useRoute, useRouter} from "vue-router";
  import {ref, watchEffect} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import Cookies from "js-cookie";
  import {jwtDecode} from "jwt-decode";
  import Post from "@/components/Post.vue";
  import {faArrowLeft, faArrowRight} from "@fortawesome/free-solid-svg-icons";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import "@/assets/styles/Space.css"
  import {isNotOrNull, isValidImageType, isValidPostText} from "@/constants/regexTest.js";

  const route = useRoute()
  const space = ref(null)
  const isSub = ref(false)
  const isAdmin = ref(false)
  const token = Cookies.get("accessToken")
  const image = ref(null)
  const loadingImage = ref(true)
  const loading = ref(true)
  const posts = ref(null)
  const users = ref(null)
  const router = useRouter()
  const postImages = ref({})
  const numberOfPages = ref(0)
  const pageRef = ref((route.query.page && route.query.page > 0 && route.query.page !== '') ? route.query.page : 1)
  const isOpen = ref(false)
  const imageCreate = ref(null)
  const newPost = ref({
    text: '',
    argument: '',
    file: null
  })
  const inputSelection = ref(null)

  watchEffect(() => {
    const id = route.params.id
    apiUrlToken.get(`/space/view/${id}/${pageRef.value}`)
        .then((response) => {
          space.value = response.data.message
          numberOfPages.value = response.data.numberOfPages
          if (route.query.page < 1) {
            router.push("?page=1")
            pageRef.value = route.query.page
          } else if(route.query.page > numberOfPages.value) {
            router.push(`?page=${numberOfPages.value}`)
            pageRef.value = numberOfPages.value
          }
          const userData = response.data.users
          users.value = userData
          posts.value = response.data.posts
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
          console.error(error)
        })
  })

  watchEffect(() => {
    if (posts.value !== null) {
      posts.value.map((p) => {
        if (p.file !== null) {
          const splits = p.file.split("\\")
          const imageName = splits[2]
          apiUrlToken.get(`/images/post/${p.id}/${imageName}`, {
            responseType: 'blob'
          })
              .then(async (response) => {
                if (!postImages.value[p.id]) {
                  postImages.value[p.id] = ref(URL.createObjectURL(await response.data));
                }
              })
              .catch((error) => {
                console.log(error)
              })
        }
      })
      posts.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    }
  })

  const handleSubmit = () => {
    const body = new FormData()
    body.append("idSpazio", space.value.id)
    apiTokenForm.post('/space/subscribe', body)
        .then((response) => {
          isSub.value = !isSub.value
          toast.success(response.data.message)
          const actualUser = jwtDecode(token);
          if (isSub.value) {
            users.value.push(actualUser)
          } else {
            users.value = users.value.filter(user => user.id !== actualUser.id)
          }
        })
        .catch((error) => {
          console.error("Errore nell'iscrizione allo spazio " + error)
          isSub.value = false
        })
  }

  const handleChange = (newPage) => {
    router.push(`?page=${newPage}`)
    pageRef.value = newPage
  }

  const changeImage = (e) => {
    const file = e.target.files[0]
    if (file) {
      imageCreate.value = URL.createObjectURL(file)
      newPost.value.file = file
    }
  }

  const openSelectionImage = () =>{
    inputSelection.value.click()
  }

  const handleCreatePost = () => {
    if (!isValidPostText(newPost.value.text)) {
      toast.error("Testo del post non valido")
      isOpen.value = true
      return
    } else if (!isNotOrNull(newPost.value.file) && !isValidImageType(newPost.value.file)) {
      isOpen.value = true
      toast.error("Immagine non valida")
      return
    }
    const body = new FormData()
    body.append("testo", newPost.value.text)
    if (newPost.value.file !== null) {
      body.append("file", newPost.value.file)
    }
    apiTokenForm.post(`/post/create/${route.params.id}`, body)
        .then((response) => {
          const token = Cookies.get("accessToken")
          posts.value.push({
            testo: newPost.value.text,
            argument: newPost.value.argument,
            file: response.data.newPost.file,
            userData: jwtDecode(token),
            id: response.data.newPost.id,
            createdAt: response.data.newPost.createdAt
          });
          posts.value = posts.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          if (imageCreate.value !== null) {
            postImages.value[response.data.newPost.id] = imageCreate.value;
          }
          newPost.value = {
            text: '',
            argument: '',
            file: null
          }
        })
        .catch((error) => {

          console.error("Errore creazione post " + error)
          toast.error("Errore nella creazione del post")
        })
  }

  const handleCloseModal = () => {
    imageCreate.value = null
    newPost.value = {
      text: '',
      argument: '',
      file: null
    }
  }
</script>

<template>
  <div class="container-space-view">
    <div class="top-space-container">
      <div class="image-space-container">
        <div v-if="loadingImage" class="loading-container">
          <VaProgressCircle indeterminate color="#fff"/>
        </div>
        <div v-else>
          <img :src="image" :alt="space.title" />
        </div>
      </div>
      <div v-if="loading">
        <VaProgressCircle indeterminate color="#262626"/>
      </div>
      <div v-else>
        <h1>{{space.title}}</h1>
        <p class="description-space">{{space.description}}</p>
        <div class="argument-section">
          <h2>Argomento</h2>
          <p>{{space.argument}}</p>
        </div>
        <div class="button-section">
          <button v-if="isAdmin" @click="router.push(`/astroverse/space/modify/${space.id}`)">
            Modifica spazio
          </button>
          <button v-if="isSub" @click="isOpen = true">
            Crea Post
          </button>
          <RouterLink :to="`${space.id}/users`">Numero di utenti: {{users.length}}</RouterLink>
          <button v-if="isSub && !isAdmin" @click="handleSubmit">
            Disiscriviti
          </button>
          <button v-else-if="!isSub && !isAdmin" @click="handleSubmit">
            Iscriviti
          </button>
        </div>
      </div>
    </div>
    <div>
      <div v-if="posts !== null && posts.length > 0">
        <h2>Post</h2>
        <div v-for="post in posts" :key="post.id">
          <Post :post="post" :src="postImages[post.id]" />
        </div>
      </div>
    </div>
  </div>
  <div v-if="numberOfPages > 1">
    <button @click="handleChange(pageRef - 1)">
      <FontAwesomeIcon :icon="faArrowLeft" />
    </button>
    <button @click="handleChange(pageRef + 1)">
      <FontAwesomeIcon :icon="faArrowRight" />
    </button>
  </div>
  <VaModal v-model="isOpen" ok-text="Salva post" cancel-text="Annulla" @ok="handleCreatePost" @cancel="handleCloseModal">
    <div>
      <h1>Crea il tuo post:</h1>
      <img v-if="imageCreate" :src="imageCreate" alt="Immagine post"/>
      <button @click="openSelectionImage">Seleziona un immagine per il tuo post</button>
      <input type="file" ref="inputSelection" style="display: none" @change="changeImage" accept="image/png, image/jpeg"/>
      <label for="text">Testo del post</label>
      <input type="text" id="text" name="text" placeholder="Inserisci il testo per questo post" v-model="newPost.text">
    </div>
  </VaModal>
</template>