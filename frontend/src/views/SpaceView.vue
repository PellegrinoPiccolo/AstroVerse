<script setup>
  import {useRoute, useRouter} from "vue-router";
  import {ref, watchEffect} from "vue";
  import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import Cookies from "js-cookie";
  import {jwtDecode} from "jwt-decode";

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

  watchEffect(() => {
    const id = route.params.id
    apiUrlToken.get(`/space/view/${id}`)
        .then((response) => {
          space.value = response.data.message
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
          if (error.response) {
            toast.error(err.response.data.error)
          }
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
                console.log(p.id, postImages.value[p.id])
              })
              .catch((error) => {
                console.log(error)
              })
        }
      })
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
          isSub.value = false
          if(error.response) {
            toast.error(error.response.data.error)
          }
        })
  }
</script>

<template>
  <div>
    <div v-if="loadingImage">
      <VaProgressCircle indeterminate color="#262626"/>
    </div>
    <div v-else>
      <img :src="image" :alt="space.title" />
    </div>
    <div v-if="loading">
      <VaProgressCircle indeterminate color="#262626"/>
    </div>
    <div v-else>
      <button v-if="isAdmin" @click="router.push(`/astroverse/space/modify/${space.id}`)">
        Modifica spazio
      </button>
      <button v-if="isSub" @click="router.push(`/astroverse/space/${space.id}/create-post`)">
        Crea Post
      </button>
      <RouterLink :to="`${space.id}/users`">Numero di utenti: {{users.length}}</RouterLink>
      <button v-if="isSub && !isAdmin" @click="handleSubmit">
        Disiscriviti
      </button>
      <button v-else-if="!isSub && !isAdmin" @click="handleSubmit">
        Iscriviti
      </button>
      <h1>{{space.title}}</h1>
      <p>{{space.description}}</p>
      <div>
        <p>Argomento</p>
        <p>{{space.argument}}</p>
      </div>
      <div v-if="posts.length > 0">
        <h2>Post</h2>
        <div v-for="post in posts" :key="post.id">
          <img v-if="postImages[post.id]" :src="postImages[post.id]" :alt="post.testo">
          <p @click="console.log(postImages[post.id], post.id)">{{post.testo}}</p>
          <p>{{post.userData.username}}</p>
        </div>
      </div>
    </div>
  </div>
</template>