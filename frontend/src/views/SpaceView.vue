<script setup>
  import {useRoute} from "vue-router";
  import {onMounted, ref} from "vue";
  import {apiUrlToken} from "@/constants/ApiUrl.js";
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

  onMounted(() => {
    const id = route.params.id
    apiUrlToken.get(`/space/view/${id}`)
        .then((response) => {
          space.value = response.data.message
          const users = response.data.users
          posts.value = response.data.posts
          console.log(response)
          const actualUser = jwtDecode(token)
          users.map((user) => {
            if (user.id === actualUser.id) {
              isSub.value = true
            }
          })
          if (response.data.idAdmin === actualUser.id) {
            isAdmin.value = true
          }
          console.log(response)
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
                console.log(error)
                loadingImage.value = false
              })
        })
        .catch((error) => {
          if (error.response.data.error) {
            toast.error(err.response.data.error)
          }
          console.log(error)
        })
  })
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
      <button v-if="isSub && !isAdmin">
        Disiscriviti
      </button>
      <button v-else-if="!isSub && !isAdmin">
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
          <!--Inserire immagine del post-->
          <p>{{post.testo}}</p>
          <p>{{post.userData.username}}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>