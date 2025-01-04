import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import Nav from "@/views/Nav.vue";
import AuthView from "@/views/AuthView.vue";
import Cookies from 'js-cookie';
import ServerUrl from "@/assets/ServerUrl.js";

const checkUser = async () => {
  const accessToken = Cookies.get('accessToken') || '';
  if (accessToken === '') {
    return false
  }
  const response = await fetch(`${ServerUrl}/api/auth/validate-token`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`
    },
    credentials: 'include'
  })
  const data = await response.json()
  console.log(response.status)
  if (response.ok) {
    return true
  } else {
    return false
  }
}

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
      beforeEnter: checkUser,
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

router.beforeEach(async (to, from, next) => {
  if (to.name !== 'AuthView' && !(await checkUser())) {
    history.pushState({}, null, '/')
    next({name: 'AuthView'});
  } else {
    next();
  }
})

export default router;