import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import BeforeCrisisUnderPage from '@/components/before-crisis/BeforeCrisisUnderPage.vue'
import { createTestingPinia } from '@pinia/testing'

// Mock vue-router
const pushMock = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock,
  }),
}))

describe('BeforeCrisisUnderPage.vue', () => {
  beforeEach(() => {
    pushMock.mockReset()
  })

  it('rendrer statisk innhold', () => {
    const wrapper = mount(BeforeCrisisUnderPage, {
      global: {
        plugins: [createTestingPinia()],
      },
    })

    expect(wrapper.text()).toContain('🛡️ Før Krise')
    expect(wrapper.text()).toContain('Krisefikser hjelper deg')
    expect(wrapper.text()).toContain('Registrer for å ta quiz')
  })

  it('navigerer til /register når knappen klikkes', async () => {
    const wrapper = mount(BeforeCrisisUnderPage, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              user: { isAuthenticated: false },
            },
          }),
        ],
      },
    })

    await wrapper.get('button').trigger('click')
    expect(pushMock).toHaveBeenCalledWith('/register')
  })

  it('viser tooltip når bruker ikke er innlogget og hovrer', async () => {
    const wrapper = mount(BeforeCrisisUnderPage, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            initialState: {
              user: { isAuthenticated: false },
            },
          }),
        ],
      },
    })

    const buttonArea = wrapper.find('div.mb-2')
    expect(wrapper.html()).not.toContain('Logg inn eller registrer deg')
    await buttonArea.trigger('mouseenter')
    expect(wrapper.html()).toContain('Logg inn eller registrer deg')
    await buttonArea.trigger('mouseleave')
    expect(wrapper.html()).not.toContain('Logg inn eller registrer deg')
  })
})
