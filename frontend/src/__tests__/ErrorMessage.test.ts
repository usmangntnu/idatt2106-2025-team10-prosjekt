import { mount } from '@vue/test-utils'
import ErrorMessage from '@/components/ErrorMessage.vue'
import { describe, it, expect } from 'vitest'

describe('ErrorMessage.vue', () => {
  it('shows message when present', () => {
    const wrapper = mount(ErrorMessage, {
      props: { message: 'Test error' },
    })
    expect(wrapper.text()).toContain('Test error')
  })

  it('hides content when message is empty', () => {
    const wrapper = mount(ErrorMessage, {
      props: { message: '' },
    })
    expect(wrapper.html()).toBe('<!--v-if-->')
  })
})
