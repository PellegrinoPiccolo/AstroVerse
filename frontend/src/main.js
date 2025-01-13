import { createApp } from 'vue'
import App from './App.vue'
import router from './index.js'
import Vue3Toastify from 'vue3-toastify';
import { createVuestic } from "vuestic-ui";
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const app = createApp(App)
const vuetify = createVuetify({
    components,
    directives,
})

app.use(Vue3Toastify, {
    autoClose: 2500,
    theme: "dark"
})
app.use(createVuestic())
app.use(vuetify)
app.use(router)
app.mount('#app')