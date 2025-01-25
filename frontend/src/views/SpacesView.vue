<script setup>
  import {ref, watchEffect} from "vue";
  import {apAiToken, apiUrlToken} from "@/constants/ApiUrl.js";
  import {useRoute, useRouter} from "vue-router";
  import {toast} from "vue3-toastify";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import {faArrowLeft, faArrowRight, faChevronDown, faChevronUp} from "@fortawesome/free-solid-svg-icons";
  import "@/assets/styles/SpacesView.css"

  const spacesSuggested = ref([])
  const spacesNotSuggested = ref([])
  const spaces = ref([])
  const route = useRoute()
  const router = useRouter()
  const numberOfPages = ref(0)
  const pageRef = ref((route.query.page && route.query.page > 0 && route.query.page !== '') ? route.query.page : 1)
  const spacesImages = ref({})
  const show = ref({})

  watchEffect(() => {
    apAiToken.get(`/get-spaces/${(pageRef.value < 1 || pageRef.value > numberOfPages.value) ? 1 : pageRef.value}`)
        .then((response) => {
          spacesSuggested.value = []
          spacesNotSuggested.value = []
          spaces.value = []
          response.data.spaces.forEach((space) => {
            if (space.consigliato) {
              spacesSuggested.value = [...spacesSuggested.value, space];
            } else {
              spacesNotSuggested.value = [...spacesNotSuggested.value, space]
            }
            spaces.value = [...spaces.value, space]
          });
          numberOfPages.value = response.data.numberOfPages
          if (route.query.page < 1) {
            router.push("?page=1")
            pageRef.value = 1
          } else if(route.query.page > numberOfPages.value) {
            router.push(`?page=${numberOfPages.value}`)
            pageRef.value = numberOfPages.value
          }
          spaces.value = spaces.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          spaces.value.map((space) => {
            show.value[space.id] = false
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

  const handleViewSpace = (id) => {
    router.push(`/astroverse/space/${id}`)
  }

  const handleChangePage = (page) => {
    router.push(`?page=${page}`)
    pageRef.value = page
  }
</script>

<template>
  <div class="spaces-container" v-if="spaces.length > 0">
    <h1 v-if="spacesSuggested.length > 0">Spazi consigliati di AstroVerse:</h1>
    <div class="spaces-card-container" v-if="spacesSuggested.length > 0">
      <v-card class="mx-auto" max-width="344" v-for="space in spacesSuggested" :key="space.id" :style="{overflow: show[space.id] ? 'visible' : 'hidden', height: show[space.id] ? 'auto' : '19.8em', width: '220px'}" style="background: #262626">
        <v-img height="200px" :src="spacesImages[space.id]" cover v-if="spacesImages[space.id]"></v-img>
        <v-card-title>
          {{space.title}}
        </v-card-title>
        <v-card-subtitle>
          {{space.argument}}
        </v-card-subtitle>
        <v-card-actions>
          <v-btn color="white" variant="outlined" text="Mostra di più" @click="handleViewSpace(space.id)"></v-btn>
          <v-spacer></v-spacer>
          <v-btn @click="show[space.id] = !show[space.id]">
            <FontAwesomeIcon :icon="show[space.id] ? faChevronUp : faChevronDown" class="arrow-icon-space-card"/>
          </v-btn>
        </v-card-actions>
          <v-expand-transition>
            <div v-if="show[space.id]">
              <v-divider></v-divider>
              <v-card-text>
                {{space.description.length > 180 ? space.description.slice(0, 180) + '...' : space.description}}
              </v-card-text>
            </div>
          </v-expand-transition>
      </v-card>
    </div>
    <h1 v-if="spacesNotSuggested.length > 0">Altri Spazi di AstroVerse:</h1>
    <div class="spaces-card-container" v-if="spacesNotSuggested.length > 0">
      <v-card class="mx-auto" max-width="344" v-for="space in spacesNotSuggested" :key="space.id" :style="{overflow: show[space.id] ? 'visible' : 'hidden', height: show[space.id] ? 'auto' : '19.8em', width: '220px'}" style="background: #262626">
        <v-img height="200px" :src="spacesImages[space.id]" cover v-if="spacesImages[space.id]"></v-img>
        <v-card-title>
          {{space.title}}
        </v-card-title>
        <v-card-subtitle>
          {{space.argument}}
        </v-card-subtitle>
        <v-card-actions>
          <v-btn color="white" variant="outlined" text="Mostra di più" @click="handleViewSpace(space.id)"></v-btn>
          <v-spacer></v-spacer>
          <v-btn @click="show[space.id] = !show[space.id]">
            <FontAwesomeIcon :icon="show[space.id] ? faChevronUp : faChevronDown" class="arrow-icon-space-card"/>
          </v-btn>
        </v-card-actions>
        <v-expand-transition>
          <div v-if="show[space.id]">
            <v-divider></v-divider>
            <v-card-text>
              {{space.description.length > 180 ? space.description.slice(0, 180) + '...' : space.description}}
            </v-card-text>
          </div>
        </v-expand-transition>
      </v-card>
    </div>
    <div v-if="numberOfPages > 1" class="arrow-spaces-view-container">
      <v-btn variant="outlined" color="white" @click="handleChangePage(Number(pageRef) - 1)">
        <FontAwesomeIcon :icon="faArrowLeft" />
      </v-btn>
      <v-btn variant="outlined" color="white" @click="handleChangePage(Number(pageRef) + 1)">
        <FontAwesomeIcon :icon="faArrowRight" />
      </v-btn>
    </div>
  </div>
</template>