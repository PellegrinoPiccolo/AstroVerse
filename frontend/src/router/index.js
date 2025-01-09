import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import Nav from "@/views/Nav.vue";
import AuthView from "@/views/AuthView.vue";
import Cookies from 'js-cookie';
import {apiUrlToken} from "@/constants/ApiUrl.js";
import UserView from "@/views/UserView.vue";
import CreateSpaceView from "@/views/CreateSpaceView.vue";

const checkUser = async () => {
  const accessToken = Cookies.get('accessToken') || '';
  if (accessToken === '') {
    return false
  }
  try {
    await apiUrlToken.post('/auth/validate-token');
    return true;
  } catch (error) {
    console.log(error.message);
    return false;
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'AuthView',
      component: AuthView
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
        },
        {
          path: 'user',
          name: 'UserView',
          component: UserView
        },
        {
          path: 'create/space',
          name: 'CreateSpaceView',
          component: CreateSpaceView
        }
      ]
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const isValid = await checkUser()
  if (to.name === 'AuthView' && isValid) {
    history.pushState({}, null, '/astroverse')
    next({name: 'home'})
  } else if (to.name !== 'AuthView' && !isValid) {
    history.pushState({}, null, '/')
    next({name: 'AuthView'});
  } else {
    next();
  }
})

export default router;