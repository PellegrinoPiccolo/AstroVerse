import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import Nav from "@/views/Nav.vue";
import AuthView from "@/views/AuthView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'AuthView',
      components: AuthView
    },
    {
      path: '/astroverse',
      name: 'home',
      component: Nav,
      children: [
        {
          path: '',
          name: 'HomeView',
          component: HomeView
        }
      ]
    },
  ],
})

export default router;