<script setup>
import {onMounted, ref, watchEffect} from "vue";
import {apiUrlToken} from "@/constants/ApiUrl.js";
import {toast} from "vue3-toastify";
import ServerUrl from "@/constants/ServerUrl.js";

  const user = ref(null)
  const loading = ref(true)
  const spaces = ref(null)
  const images = ref({})
  const orderedSpaces = ref(null)

  onMounted(async () => {
    await apiUrlToken.get('/auth/view-account')
        .then((response) => {
          user.value = response.data.user
          console.log(response.data.user.userSpaces)
          spaces.value = response.data.user.userSpaces
          spaces.value.map(async (space) => {
              const parts = space.space.image.split("\\")
              apiUrlToken.get(`/images/space/${space.space.id}/${parts[2]}`, {
                responseType: "blob"
              })
                  .then(async (response) => {
                    const imageURL = URL.createObjectURL(await response.data)
                    images.value[space.id] = imageURL
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
      orderedSpaces.value = spaces.value.sort((a, b) => new Date(b.space.createdAt) - new Date(a.space.createdAt))
    }
  })
</script>

<template>
  <div>
    <div>
      <div v-if="loading">
        <VaProgressCircle indeterminate color="#262626"/>
      </div>
      <RouterLink v-if="!loading" to="astroverse/create/space">
        <button>
          Crea Spazio
        </button>
      </RouterLink>
      <div v-for="space in orderedSpaces" :key="space.id">
        <img :src="images[space.space.id]" :alt="space.space.title">
        <p>
          {{ space.space.title }}
        </p>
      </div>
    </div>
    <div>

    </div>
  </div>
</template>