<script setup>
import {onMounted, ref, watchEffect} from "vue";
import {apiUrlToken} from "@/constants/ApiUrl.js";
import {toast} from "vue3-toastify";

  const user = ref(null)
  const loading = ref(true)
  const spaces = ref(null)
  const images = ref({})
  const orderedSpaces = ref(null)

  onMounted(async () => {
    await apiUrlToken.get('/auth/view-account')
        .then((response) => {
          user.value = response.data.user
          console.log(response)
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
        <RouterLink :to="`astroverse/space/${space.id}`">
          <img :src="images[space.id]" :alt="space.title">
          <p>
            {{ space.title }}
          </p>
        </RouterLink>
      </div>
    </div>
    <div>

    </div>
  </div>
</template>