<script setup>
  import logo from '@/assets/logo.png';
</script>

<template>
  <nav>
    <div class="logo-container">
      <a href="@/views/HomeView.vue">
        <img class="logo" :src="logo" alt="AstroVerse logo"/>
      </a>
    </div>
    <div class="link-container">
      <div id="about">
        <a href="@/views/AboutView.vue">
          <img src="" alt="Chi siamo"/>
        </a>
      </div>
      <div id="post">
        <a href="">
          <img src="" alt="Scrittura Post"/>
        </a>
      </div>
      <div id="search">

      </div>
      <div id="account">
        <a href="">
          <img src="" alt="Account utente"/>
        </a>
      </div>
    </div>
  </nav>
  <RouterView />
</template>

<style scoped>
  nav {

  }

  .logo {
    width: 100px;
    height: 100px;
    cursor: pointer;
  }
</style>