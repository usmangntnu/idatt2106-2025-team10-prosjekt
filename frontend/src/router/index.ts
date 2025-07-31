import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/auth/LoginView.vue'
import RegisterView from '@/views/auth/RegisterView.vue'
import VerifyEmailView from '@/views/VerifyEmailView.vue'
import JoinHousehold from '@/components/JoinHousehold.vue'
import InviteToHousehold from '@/components/InviteToHousehold.vue'
import Verify from '@/components/auth/Verify.vue'
import { useUserStore } from '@/stores/user.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home-page',
      component: () => import('../views/HomePageView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/map',
      name: 'map',
      component: () => import('../views/MapView.vue'),
    },
    {
      path: '/storage',
      name: 'storage',
      component: () => import('../views/EmergencyStorageView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/check-email',
      name: 'check-email',
      component: VerifyEmailView,
    },
    {
      path: '/verify',
      name: 'Verify',
      component: Verify,
    },
    {
      path: '/join-household',
      name: 'join-household',
      component: JoinHousehold, //should be protected, i.e. only for logged in users
    },
    {
      path: '/invite',
      name: 'invite',
      component: InviteToHousehold, //should be protected, i.e. only for logged in users
    },
    {
      path: '/under-crisis',
      name: 'under-crisis',
      component: () => import('../views/UnderCrisisView.vue'),
    },
    {
      path: '/before-crisis',
      name: 'before-crisis',
      component: () => import('../views/BeforeCrisisView.vue'),
    },
    {
      path: '/after-crisis',
      name: 'after-crisis',
      component: () => import('../views/AfterCrisisView.vue'),
    },
    {
      path: '/reflections',
      name: 'reflection',
      component: () => import('../views/ReflectionNotesView.vue'),
    },
    {
      path: '/reflections/my',
      name: 'my-reflections',
      component: () => import('../views/MyReflectionsView.vue'),
    },
    {
      path: '/personvern',
      name: 'personvern',
      component: () => import('../components/footer/Privacy.vue'),
    },
    {
      path: '/household',
      name: 'household',
      component: () => import('../views/HouseholdView.vue'),
    },
    {
      path: '/om-oss',
      name: 'om-oss',
      component: () => import('../components/footer/AboutUs.vue'),
    },
    {
      path: '/quiz',
      name: 'quiz',
      component: () => import('../views/QuizView.vue'),
    },

    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('../views/admin/AdminLoginView.vue'),
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('../views/admin/AdminDashboardView.vue'),
      meta: { requiresAdmin: true },
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  await userStore.fetchCurrentUser()

  // innloggede skal bli sendt til /
  if (
    (to.name === 'login' || to.name === 'register') &&
    userStore.isAuthenticated
  ) {
    return next('/')
  }

  // for innloggede sider
  const requiresAuth = ['join-household', 'invite', 'household', 'storage']
  if (requiresAuth.includes(to.name as string) && !userStore.isAuthenticated) {
    return next('/login')
  }

  // admin sjekk
  if (to.meta.requiresAdmin) {
    if (!userStore.isAuthenticated) return next('/admin/login')
    if (!userStore.isAdmin) return next('/')
  }

  next()
})

export default router
