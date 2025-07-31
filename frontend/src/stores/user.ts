import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { AuthRequest } from '@/types/AuthDto.ts'
import { AuthApi } from '@/services/authApi.ts'
import type { CurrentUser } from '@/types/UserDto.ts'
import { handleApiError } from '@/utils/handleApiError.ts'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<CurrentUser | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pendingEmail = ref<string | null>(null)

  const isAuthenticated = computed(() => !!currentUser.value)
  const isAdmin = computed(
    () => currentUser.value?.roles?.includes('ROLE_ADMIN') ?? false
  )
  const isSuperAdmin = computed(
    () => currentUser.value?.roles.includes('ROLE_SUPER_ADMIN') ?? false
  )

  const displayName = computed(
    () => currentUser.value?.email ?? currentUser.value?.username ?? ''
  )

  async function login(credentials: AuthRequest) {
    loading.value = true
    error.value = null
    try {
      await AuthApi.login(credentials)
      currentUser.value = await AuthApi.getCurrentUser()
      console.log(currentUser.value)
    } catch (err) {
      console.error('Login failed:', err)
      error.value = handleApiError(err)
      currentUser.value = null
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchCurrentUser() {
    loading.value = true
    error.value = null
    try {
      currentUser.value = await AuthApi.getCurrentUser()
    } catch (err) {
      console.error('Fetching current user failed:', err)
      error.value = handleApiError(err)
      currentUser.value = null
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    loading.value = true
    error.value = null
    try {
      await AuthApi.logout()
      currentUser.value = null
    } catch (err) {
      console.error('Logout failed:', err)
      error.value = handleApiError(err)
    } finally {
      loading.value = false
    }
  }

  function setUser(user: CurrentUser | null) {
    currentUser.value = user
  }

  return {
    currentUser,
    loading,
    error,
    isAuthenticated,
    isAdmin,
    isSuperAdmin,
    displayName,
    login,
    fetchCurrentUser,
    logout,
    pendingEmail,
  }
})
