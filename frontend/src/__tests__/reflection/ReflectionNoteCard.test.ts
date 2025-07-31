import { mount } from '@vue/test-utils'
import ReflectionNoteCard from '@/components/reflection/ReflectionNoteCard.vue'

describe('ReflectionNoteCard.vue', () => {
  const mockNote = {
    title: 'My Note',
    content: 'This is the content of the note.',
    createdAt: '2023-10-01T12:00:00Z',
    visibility: 'PRIVATE',
    creator: {
      username: 'testuser',
    },
  }

  it('renders note content and emits click', async () => {
    const wrapper = mount(ReflectionNoteCard, {
      props: { note: mockNote },
    })

    expect(wrapper.text()).toContain('My Note')
    expect(wrapper.text()).toContain('testuser')

    await wrapper.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('click')
  })
})
