<script setup>
  import {ref, watchEffect} from "vue";
  import {apiUrlToken} from "@/constants/ApiUrl.js";
  import {useRoute, useRouter} from "vue-router";
  import {toast} from "vue3-toastify";

  const spaces = ref(null)
  const route = useRoute()
  const router = useRouter()
  const numberOfPages = ref(0)
  const pageRef = ref((route.query.page && route.query.page > 0 && route.query.page !== '') ? route.query.page : 1)
  const spacesImages = ref({})

  watchEffect(() => {
    apiUrlToken.get(`/space/get-all-spaces/${pageRef.value}`)
        .then((response) => {
          spaces.value = response.data.spaces
          numberOfPages.value = response.data.numberOfPages
          if (route.query.page < 1) {
            router.push("?page=1")
            pageRef.value = route.query.page
          } else if(route.query.page > numberOfPages.value) {
            router.push(`?page=${numberOfPages.value}`)
            pageRef.value = numberOfPages.value
          }
          spaces.value = spaces.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          spaces.value.map((space) => {
            const splits = space.image.split("\\")
            const imageName = splits[2]
            apiUrlToken.get(`/images/space/${space.id}/${imageName}`, {
              responseType: 'blob'
            })
              .then(async (response) => {
                spacesImages.value[space.id] = URL.createObjectURL(await response.data)
              })
              .catch((error) => {
                if (error.response.data.error) {
                  toast.error(err.response.data.error)
                }
                console.error(error)
              })
          })
        })
        .catch((error) => {
          console.error("Errore nella visualizzazione di tutti gli spazi" + error)
          toast.error("Errore nella visualizzazione degli spazi")
        })
  })
</script>

<template>
  <div class="space-container" v-if="spaces">
    <h1>Naviga gli spazi di AstroVerse:</h1>
    <RouterLink :to="`/astroverse/space/${space.id}`" v-for="space in spaces">
      <img :src="spacesImages[space.id]" v-if="spacesImages[space.id]" :alt="space.title"/>
      <p>{{space.title}}</p>
    </RouterLink>
  </div>
</template>