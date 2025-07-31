import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import EmailVerification from '@/components/auth/VerificationSent.vue'
import { useUserStore } from '@/stores/user.ts'

// Mock vue-router
vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRoute: vi.fn(() => ({
      path: '/',
      name: undefined,
      params: {},
      query: {},
      hash: '',
      fullPath: '/',
      matched: [],
      meta: {},
      redirectedFrom: undefined,
    })),
  }
})

describe('EmailVerification Component', () => {
  beforeEach(() => {
    // Create a fresh Pinia instance for each test
    setActivePinia(createPinia())
  })

  it('renders the component correctly', () => {
    const wrapper = mount(EmailVerification)

    // Test heading presence
    expect(wrapper.find('h1').text()).toBe('Verifiser e-posten din')

    // Test buttons presence
    expect(wrapper.findAll('a')).toHaveLength(1)
    expect(wrapper.html()).toContain('Åpne Gmail')
  })

  it('displays fallback text when no email is provided', async () => {
    // Mock the route with empty query parameters
    const useRoute = vi.mocked(await import('vue-router')).useRoute
    useRoute.mockReturnValue({
      path: '/',
      name: undefined,
      params: {},
      query: {},
      hash: '',
      fullPath: '/',
      matched: [],
      meta: {},
      redirectedFrom: undefined,
    })

    const userStore = useUserStore()
    userStore.pendingEmail = ''

    const wrapper = mount(EmailVerification)

    expect(wrapper.html()).toContain('Åpne Gmail')
    expect(wrapper.find('a').attributes('href')).toBe('https://mail.google.com')
  })

  it('has correct href attributes for email client links', () => {
    const wrapper = mount(EmailVerification)
    const links = wrapper.findAll('a')

    // Second link should be Gmail
    expect(links[0].attributes('href')).toBe('https://mail.google.com')
    expect(links[0].attributes('target')).toBe('_blank')
  })
})
