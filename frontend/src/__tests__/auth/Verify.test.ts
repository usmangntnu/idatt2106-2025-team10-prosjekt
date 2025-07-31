import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import VerifyPage from '@/components/auth/Verify.vue'
import { useRouter, useRoute } from 'vue-router'
import { AuthApi } from '@/services/authApi.ts'

// Mocks
vi.mock('vue-router', () => ({
  useRouter: vi.fn(),
  useRoute: vi.fn(),
}))

vi.mock('@/services/authApi', () => ({
  AuthApi: {
    verifyEmail: vi.fn(),
  },
}))

describe('VerifyPage.vue', () => {
  let mockPush: ReturnType<typeof vi.fn>

  beforeEach(() => {
    mockPush = vi.fn()
    ;(useRouter as unknown as vi.Mock).mockReturnValue({ push: mockPush })
  })

  it('shows error message when token is missing', async () => {
    ;(useRoute as unknown as vi.Mock).mockReturnValue({ query: {} })

    const wrapper = mount(VerifyPage)
    await new Promise((resolve) => setTimeout(resolve)) // wait for onMounted

    expect(wrapper.text()).toContain(
      'Ugyldig eller manglende epost-verifiseringstoken.'
    )
  })

  it('shows error when token is invalid or expired', async () => {
    ;(useRoute as unknown as vi.Mock).mockReturnValue({
      query: { token: 'bad-token' },
    })
    ;(AuthApi.verifyEmail as vi.Mock).mockRejectedValue(
      new Error('Invalid token')
    )

    const wrapper = mount(VerifyPage)
    await new Promise((resolve) => setTimeout(resolve)) // wait for onMounted

    expect(wrapper.text()).toContain('Verifisering av bruker feilet')
    expect(AuthApi.verifyEmail).toHaveBeenCalledWith('bad-token')
  })
})
