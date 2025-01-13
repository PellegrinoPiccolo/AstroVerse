<script setup>
  import {onMounted, ref, watchEffect} from "vue";
  import {apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from "vue3-toastify";
  import {useRoute, useRouter} from "vue-router";
  import Post from "@/components/Post.vue";
  import {faArrowLeft, faArrowRight, faPlus} from "@fortawesome/free-solid-svg-icons";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import "@/assets/styles/Home.css"

  const user = ref(null)
  const loading = ref(true)
  const spaces = ref(null)
  const images = ref({})
  const orderedSpaces = ref(null)
  const route = useRoute()
  const router = useRouter()
  const posts = ref(null)
  const numberOfPages = ref(0)
  const pageRef = ref((route.query.page && route.query.page > 0 && route.query.page !== '') ? route.query.page : 1)
  const postImages = ref({})


  onMounted(async () => {
    await apiUrlToken.get('/auth/view-account')
        .then((response) => {
          user.value = response.data.user
          spaces.value = response.data.spaces
          spaces.value.map(async (space) => {
              const parts = space.image.split("\\")
              apiUrlToken.get(`/images/space/${space.id}/${parts[2]}`, {
                responseType: "blob"
              })
              .then(async (response) => {
                images.value[space.id] = URL.createObjectURL(await response.data)
              })
              .catch((error) => {
                console.error(error)
              })
          })
        })
        .catch((error) => {
          console.error(error.response)
          if (error.response.data.error) {
            toast.error(error.response.data.error)
          }
        })
    loading.value = false
  })

  watchEffect(() => {
    if (spaces.value != null) {
      orderedSpaces.value = spaces.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    }
  })

  watchEffect(() => {
    apiUrlToken.get(`/post/get-posts/${pageRef.value}`)
        .then((response) => {
          posts.value = response.data.posts
          numberOfPages.value = response.data.numberOfPages
          if (route.query.page < 1) {
            router.push("?page=1")
            pageRef.value = route.query.page
          } else if (route.query.page > numberOfPages.value) {
            router.push(`?page=${numberOfPages.value}`)
            pageRef.value = numberOfPages.value
          }
        })
        .catch((error) => {
          toast.error("Errore nel caricamento dei post")
          console.error(error)
        })
  })

  watchEffect(() => {
    if(posts.value !== null) {
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
    }
  })

  const handleChange = (newPage) => {
    router.push(`?page=${newPage}`)
    pageRef.value = newPage
  }
</script>

<template>
  <div class="home-container">
    <div class="home-left-section">
      <div v-if="loading" class="loading-bar-home">
        <VaProgressCircle indeterminate color="#262626"/>
      </div>
      <RouterLink v-if="!loading" to="astroverse/create/space" class="button-create-space">
        <button>
          <FontAwesomeIcon :icon="faPlus"/>
          Crea Spazio
        </button>
      </RouterLink>
      <div v-for="space in orderedSpaces" :key="space.id" style="width: 100%">
        <RouterLink :to="`astroverse/space/${space.id}`" class="space-card-container">
          <div>
            <img :src="images[space.id]" :alt="space.title">
          </div>
          <p>
            {{ space.title }}
          </p>
        </RouterLink>
      </div>
    </div>
    <div class="home-right-section">
      <div v-for="post in posts" :key="post.id" style="width: 100%">
        <Post :post="post" :src="postImages[post.id]"/>
      </div>
      <div v-if="numberOfPages > 1" class="arrow-post-container">
        <v-btn variant="outlined" color="white" @click="handleChange(pageRef - 1)">
          <FontAwesomeIcon :icon="faArrowLeft" class="icon-post"/>
        </v-btn>
        <v-btn variant="outlined" color="white" @click="handleChange(pageRef + 1)">
          <FontAwesomeIcon :icon="faArrowRight" class="icon-post"/>
       </v-btn>
      </div>
    </div>
  </div>
</template>