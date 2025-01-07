import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Vue3Toastify from 'vue3-toastify';
import { createVuestic } from "vuestic-ui";

const app = createApp(App)

app.use(Vue3Toastify, {
    autoClose: 2500,
    theme: "dark"
})
app.use(createVuestic())

app.use(router)
app.mount('#app')