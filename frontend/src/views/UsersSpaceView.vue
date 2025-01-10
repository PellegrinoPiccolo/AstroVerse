<script setup>
  import {useRoute, useRouter} from "vue-router";
  import {ref, watchEffect} from "vue";
  import {apiTokenForm} from "@/constants/ApiUrl.js";
  import {toast} from 'vue3-toastify';
  import 'vue3-toastify/dist/index.css';
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import {faArrowLeft, faArrowRight} from "@fortawesome/free-solid-svg-icons";

  const route = useRoute()
  const router = useRouter()
  const users = ref(null)
  const numberOfPages = ref(0)
  const loading = ref(true)
  const pageRef = ref(route.query.page ? route.query.page : 1)

  watchEffect(() => {
    const id = route.params.id
    let page
    loading.value = true
    if (!route.query.page || route.query.page === '' || Number(route.query.page) < 1) {
      page = 1
      if (route.query.page) {
        router.push('?page=1')
      }
    } else if (numberOfPages.value > 0 && Number(route.query.page) > numberOfPages.value) {
      page = numberOfPages.value
      if (route.query.page) {
        router.push(`?page=${numberOfPages.value}`)
      }
    } else {
      page = route.query.page
    }
    pageRef.value = Number(page)
    apiTokenForm.get(`/space/get-all-users/${id}/${page}`)
      .then((response) => {
        users.value = response.data.users
        numberOfPages.value = response.data.numberOfPages
      })
      .catch((error) => {
        if(error.response) {
          toast.error(error.response.data.error)
        }
        console.log(error)
      })
    loading.value = false
  })

  const handleChange = (newPage) => {
    router.push(`?page=${newPage}`)
    pageRef.value = newPage
  }
</script>

<template>
  <div v-if="loading">
    <VaProgressCircle indeterminate color="#262626"/>
  </div>
  <div class="user-container" v-for="user in users" v-else>
    <p>{{user}}</p>
  </div>
  <div class="page-container">
    <div v-if="numberOfPages > 1">
      <button @click="handleChange(pageRef - 1)">
        <FontAwesomeIcon :icon="faArrowLeft" />
      </button>
      <button @click="handleChange(pageRef + 1)">
        <FontAwesomeIcon :icon="faArrowRight" />
      </button>
    </div>
  </div>
</template>