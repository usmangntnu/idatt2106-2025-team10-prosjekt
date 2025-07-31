import { mount } from '@vue/test-utils'
import Container from '@/components/Container.vue'
import { describe, it, expect } from 'vitest'

describe('Container.vue', () => {
  it('renders title and description and emits on click', async () => {
    const wrapper = mount(Container, {
      props: {
        title: 'Test Title',
        description: 'Test Description',
      },
    })
    await wrapper.trigger('click')
    expect(wrapper.text()).toContain('Test Title')
    expect(wrapper.text()).toContain('Test Description')
    expect(wrapper.emitted('button-click')).toBeTruthy()
  })
})
