import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { mount } from '@vue/test-utils'
import Register from '@/components/auth/Register.vue'

// Mock user‐store (komponenten bruker useUserStore, ikke useAuthStore)
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    pendingEmail: '',
    // eventuelle andre properties/metoder om nødvendig
  }),
}))

describe('Register.vue', () => {
  beforeEach(() => {
    // Frisk Pinia for hver test
    setActivePinia(createPinia())
  })

  it('renderer alle registreringsfeltene riktig', () => {
    const wrapper = mount(Register)

    expect(wrapper.find('input#firstname').exists()).toBe(true)
    expect(wrapper.find('input#lastname').exists()).toBe(true)
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('input#confirmPassword').exists()).toBe(true)
    expect(wrapper.find('input#acceptPolicy[type="checkbox"]').exists()).toBe(
      true
    )
    expect(wrapper.find('button[type="submit"]').text()).toContain('Registrer')
  })

  it('viser feilmeldinger for alle påkrevde felt når de er tomme', async () => {
    const wrapper = mount(Register)

    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.text()).toContain('Fornavn må fylles ut')
    expect(wrapper.text()).toContain('Etternavn må fylles ut')
    expect(wrapper.text()).toContain('Epost må fylles ut')
    expect(wrapper.text()).toContain('Passord må fylles ut')
    expect(wrapper.text()).toContain('Bekreft passord må fylles ut')
    expect(wrapper.text()).toContain('Personvernerklæringen må fylles ut')
  })

  it('validerer passord‐kompleksitet og mismatch riktig', async () => {
    const wrapper = mount(Register)

    // Fyll ut øvrige obligatoriske felt
    await wrapper.find('input#firstname').setValue('Test')
    await wrapper.find('input#lastname').setValue('User')
    await wrapper.find('input#email').setValue('test@example.com')
    await wrapper.find('input#acceptPolicy').setChecked(true)

    // Sett inn passord uten stor bokstav/special-tegn og med ulik bekreftelse
    await wrapper.find('input#password').setValue('password123')
    await wrapper.find('input#confirmPassword').setValue('different123')

    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.text()).toContain(
      'Passordet trenger 8 eller fler tegn, minst en stor bokstav, en liten bokstav, et tall og et spesialtegn'
    )
    expect(wrapper.text()).toContain('Passordene er ikke like')
  })
})
