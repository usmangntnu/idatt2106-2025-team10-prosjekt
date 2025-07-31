import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import UserLogin from '@/components/auth/Login.vue'
import { useRouter } from 'vue-router'

// Mocks
const pushMock = vi.fn()
const loginMock = vi.fn().mockResolvedValue(undefined)

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: pushMock }),
}))

vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    login: loginMock,
  }),
}))

describe('User Login', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    loginMock.mockClear()
    pushMock.mockClear()
  })

  it('renders fields and buttons correctly', () => {
    const wrapper = mount(UserLogin)
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').text()).toContain('Logg inn')
    expect(wrapper.find('button#create-account').text()).toContain(
      'Opprett en bruker'
    )
  })

  it('shows validation errors on empty submit', async () => {
    const wrapper = mount(UserLogin)
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(wrapper.html()).toContain('Epost må fylles ut')
    expect(wrapper.html()).toContain('Passord må fylles ut')
  })

  it('toggles password visibility', async () => {
    const wrapper = mount(UserLogin)
    const toggle = wrapper.find('button[type="button"]')
    expect(wrapper.find('input#password').attributes('type')).toBe('password')
    await toggle.trigger('click')
    expect(wrapper.find('input#password').attributes('type')).toBe('text')
  })

  it('submits valid form and navigates', async () => {
    const wrapper = mount(UserLogin)
    await wrapper.find('input#email').setValue('test@example.com')
    await wrapper.find('input#password').setValue('123456')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(loginMock).toHaveBeenCalled()
    expect(pushMock).toHaveBeenCalledWith('/')
  })

  it('shows error message on login failure', async () => {
    loginMock.mockRejectedValueOnce(new Error('Invalid'))
    const wrapper = mount(UserLogin)
    await wrapper.find('input#email').setValue('fail@example.com')
    await wrapper.find('input#password').setValue('wrong')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(wrapper.text()).toContain('Feil epost eller passord')
  })

  it('navigates to register', async () => {
    const wrapper = mount(UserLogin)
    await wrapper.find('#create-account').trigger('click')
    expect(pushMock).toHaveBeenCalledWith('/register')
  })
})
