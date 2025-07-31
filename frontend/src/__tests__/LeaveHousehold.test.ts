import { mount } from '@vue/test-utils'
import LeaveHousehold from '@/components/LeaveHousehold.vue'
import { vi } from 'vitest'
import { describe, it, expect } from 'vitest'

vi.mock('@/composables/useApi', () => ({
  useApi: () => ({
    execute: vi.fn(),
    loading: false,
    error: false,
    data: { value: null },
  }),
}))

describe('LeaveHousehold.vue', () => {
  it('renders leave button and reacts to click', async () => {
    const wrapper = mount(LeaveHousehold)
    expect(wrapper.text()).toContain('forlate denne husstanden')
    const button = wrapper.get('button')
    await button.trigger('click')
  })
})
