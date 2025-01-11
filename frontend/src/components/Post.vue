<script setup>
import {defineProps, onMounted, ref, watchEffect} from "vue";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import {faChevronDown, faChevronUp} from "@fortawesome/free-solid-svg-icons";
import {apiTokenForm, apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from "vue3-toastify";
import Cookies from "js-cookie";

  const {post} = defineProps({
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

  onMounted(() => {
    const token = Cookies.get
    apiUrlToken.get(`/post/get-vote/${post.id}`)
        .then((response) => {
          isVoted.value = response.data.message
        })
        .catch((error) => {
          console.log(error)
        })
  })

  const vote = (voteType) => {
    const body = new FormData()
    body.append("vote", voteType)
    apiTokenForm.post(`/post/vote/${post.id}`, body)
        .then((response) => {
          if (isVoted.value !== voteType) {
            isVoted.value = voteType
          } else {
            isVoted.value = null
          }
        })
        .catch((error) => {
          console.error("Errore votazione post " + post.id + " codice di errore: " + JSON.stringify(error.response))
          toast.error("Errore nella votazione del post");
        })
  }
</script>

<template>
  <div>
    <img v-if="src" :src="src" :alt="post.testo">
    <p>{{post.testo}}</p>
    <p>{{post.userData.username}}</p>
    <div>
      <button @click="vote(true)">
        <FontAwesomeIcon :icon="faChevronUp" :style="{color: isVoted !== null && isVoted === true ? 'red' : 'black'}"></FontAwesomeIcon>
      </button>
      <button @click="vote(false)">
        <FontAwesomeIcon :icon="faChevronDown" :style="{color: isVoted !== null && isVoted === false ? 'red' : 'black'}"></FontAwesomeIcon>
      </button>
    </div>
  </div>
</template>