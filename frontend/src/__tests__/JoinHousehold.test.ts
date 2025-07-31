import { mount } from '@vue/test-utils'
import JoinHousehold from '@/components/JoinHousehold.vue'
import { vi } from 'vitest'
import { describe, it, expect } from 'vitest'

vi.mock('@/services/householdApi.ts', () => ({
  createHousehold: vi.fn(() => Promise.resolve()),
  joinHousehold: vi.fn(() => Promise.resolve()),
}))

describe('JoinHousehold.vue', () => {
  it('renders and submits both forms', async () => {
    const wrapper = mount(JoinHousehold)
    const inputs = wrapper.findAll('input')
    for (const input of inputs) {
      await input.setValue('test')
    }

    const buttons = wrapper.findAll('button')
    await buttons[0].trigger('click') // join
    await buttons[1].trigger('click') // create

    expect(wrapper.text()).toContain('Bli med i en husholdning')
    expect(wrapper.text()).toContain('Lag en husholdning')
  })
})
