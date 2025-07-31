import type { CurrentUser } from '@/types/UserDto.ts'
import apiClient from '@/services/apiClient.ts'
import type { AuthRequest } from '@/types/AuthDto.ts'
import type { UserCreate } from '@/types/types.ts'

export class AuthApi {
  static async login(credentials: AuthRequest): Promise<void> {
    await apiClient.post('/auth/login', credentials, { withCredentials: true })
  }

  static async getCurrentUser(): Promise<CurrentUser> {
    const response = await apiClient.get<CurrentUser>('/auth/me', {
      withCredentials: true,
    })
    return response.data
  }

  static async logout(): Promise<void> {
    await apiClient.post('/auth/logout', {}, { withCredentials: true })
  }

  static async register(data: UserCreate): Promise<void> {
    await apiClient.post('/auth/register', data)
  }

  static async verifyEmail(token: string): Promise<void> {
    await apiClient.get(`/auth/verify/${token}`, { withCredentials: true })
  }
}
