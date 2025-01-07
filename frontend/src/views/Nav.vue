<script setup>
  import logo from '@/assets/logo.png';
  import {faHouse, faMagnifyingGlass, faPenToSquare, faUser, faUsers} from "@fortawesome/free-solid-svg-icons";
  import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
  import "@/assets/styles/Nav.css";
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
        <RouterLink to="/astroverse">
          <FontAwesomeIcon :icon="faPenToSquare" />
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/astroverse/user">
          <FontAwesomeIcon :icon="faUser" />
        </RouterLink>
      </li>
      <li class="search-container">
        <FontAwesomeIcon :icon="faMagnifyingGlass" class="search-icon"/>
        <input type="text" placeholder="Cerca su AstroVerse" >
      </li>
    </ul>
  </nav>
  <RouterView />
  <footer>
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
      <div class="section">
        <h2>GitHub</h2>
        <ul>
          <li><a href="https://github.com/PellegrinoPiccolo/AstroVerse" target="__blank">AstroVerse</a></li>
        </ul>
      </div>
    </div>
    <p class="trademark">AstroVerse 2025© All Rights Reserved</p>
  </footer>
</template>