<script setup>
  import logo from '@/assets/logo.png';
  import {faHouse, faMagnifyingGlass, faUser, faUsers} from "@fortawesome/free-solid-svg-icons";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import "@/assets/styles/Nav.css";
  import {ref, watchEffect} from "vue";
  import {apiUrlToken} from "@/constants/ApiUrl.js";
  import {toast} from "vue3-toastify";
  import {useRouter} from "vue-router";

  const router = useRouter()
  const spaces = ref(null)
  const loading = ref(false)
  const inputValue = ref('')
  const currentYear = ref(new Date().getFullYear())
  const focus = ref(false)

  watchEffect(() => {
    loading.value = true
    if (inputValue.value === '') {
      spaces.value = {}
    } else {
      apiUrlToken.get(`/space/search/${inputValue.value}`)
          .then((response) => {
            spaces.value = response.data.message
          })
          .catch((error) => {
            console.error("Spazi non trovati: " + error)
            toast.error("Nessuno spazio trovato con questa ricerca")
          })
    }
    loading.value = false
  })

  const handleBlur = () => {
    setTimeout(() => {
      focus.value = false
      inputValue.value = ''
      spaces.value = null
    }, 300)
  }

  const handleFocus = () => {
    focus.value = true
  }
</script>

<style>
  @font-face {
    font-family: "Merienda";
    src: local("Merienda"),
    url(@/assets/fonts/Roboto-Regular.ttf) format("truetype");
  }
  @font-face {
    font-family: "Merienda";
    src: local("Merienda Bold"),
    url(@/assets/fonts/Roboto-Bold.ttf) format("truetype");
    font-weight: bold;
  }
</style>

<template>
  <div class="container">
    <div class="page-container">
      <nav>
        <div class="logo-container">
          <RouterLink to="/astroverse">
            <img class="logo" :src="logo" alt="AstroVerse logo"/>
          </RouterLink>
        </div>
        <ul class="link-container">
          <li>
            <RouterLink to="/astroverse">
              <FontAwesomeIcon :icon="faHouse" />
            </RouterLink>
          </li>
          <li>
            <RouterLink to="/astroverse">
              <FontAwesomeIcon :icon="faUsers" />
            </RouterLink>
          </li>
          <li>
            <RouterLink to="/astroverse/user">
              <FontAwesomeIcon :icon="faUser" />
            </RouterLink>
          </li>
          <li class="search-container">
            <FontAwesomeIcon :icon="faMagnifyingGlass" class="search-icon"/>
            <input type="text" placeholder="Cerca su AstroVerse" v-model="inputValue" @blur="handleBlur" @focus="handleFocus">
            <div v-if="focus && inputValue !== ''" class="space-search-container">
              <div v-if="loading">
                <VaProgressCircle indeterminate color="#fff"/>
              </div>
              <div v-else class="router-container">
                <div v-for="space in spaces" v-if="spaces && spaces.length > 0" :key="space.id" class="router-text-container">
                  <RouterLink :to="`/astroverse/space/${space.id}`">
                    {{space.title}}
                  </RouterLink>
                </div>
                <div v-else>
                  <p>Non ci sono spazi con la ricerca: "{{inputValue}}"</p>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </nav>
      <div class="main">
        <RouterView />
      </div>
    </div>
    <footer>
      <div class="top-container">
        <RouterLink to="/astroverse">
          <img class="footer-logo" :src="logo" alt="AstroVerse logo"/>
        </RouterLink>
        <div class="section-container">
          <div class="section">
            <h2>Contatti</h2>
            <ul>
              <li><a href="mailto:p.piccolo4@studenti.unisa.it"> p.piccolo4@studenti.unisa.it</a></li>
              <li><a href="mailto:a.devita40@studenti.unisa.it"> a.devita40@studenti.unisa.it</a></li>
              <li><a href="mailto:c.fontana7@studenti.unisa.it">c.fontana7@studenti.unisa.it</a></li>
              <li><a href="mailto:c.bianco9@studenti.unisa.it">c.bianco9@studenti.unisa.it</a></li>
            </ul>
          </div>
        </div>
        <div class="section-container">
          <div class="section">
            <h2>GitHub</h2>
            <ul>
              <li><a href="https://github.com/PellegrinoPiccolo/AstroVerse" target="_blank">AstroVerse</a></li>
            </ul>
            <h2>Sede</h2>
            <ul>
              <li><a href="https://maps.app.goo.gl/JXCRRe5Tcgj3B7Fh6" target="_blank">Via delle Stelle, 84084 Fisciano (SA)</a></li>
            </ul>
          </div>
        </div>
      </div>
      <p class="trademark">Copyright {{ currentYear }}© Astroverse. <a href="https://opensource.org/license/mit">Released under MIT license.</a></p>
    </footer>
  </div>
</template>