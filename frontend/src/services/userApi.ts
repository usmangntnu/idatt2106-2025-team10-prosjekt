import apiClient from '@/services/apiClient.ts'
import type { CurrentUser } from '@/types/UserDto.ts'

export const getCurrentUser = async (): Promise<CurrentUser> => {
  const response = await apiClient.get<CurrentUser>('/users/profile')
  return response.data
}
