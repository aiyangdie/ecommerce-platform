import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import MainLayout from '../layout/MainLayout.vue'
import Dashboard from '../views/Dashboard.vue'
import Products from '../views/Products.vue'
import Orders from '../views/Orders.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'login', component: Login },
    {
      path: '/',
      component: MainLayout,
      meta: { auth: true },
      children: [
        { path: '', name: 'dashboard', component: Dashboard },
        { path: 'products', name: 'products', component: Products },
        { path: 'orders', name: 'orders', component: Orders },
      ],
    },
  ],
})

router.beforeEach((to) => {
  if (to.meta.auth && !localStorage.getItem('admin_token')) {
    return { name: 'login' }
  }
})

export default router
