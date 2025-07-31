import axios from 'axios'
import { useUserStore } from '@/stores/user.ts'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL,
  withCredentials: true,
})

apiClient.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const userStore = useUserStore()

    if (
      (error.response?.status === 401 || error.response?.status === 403) &&
      userStore.isAuthenticated
    ) {
      console.log('Unauthorized or forbidden, logging out')
      await userStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
export default apiClient
