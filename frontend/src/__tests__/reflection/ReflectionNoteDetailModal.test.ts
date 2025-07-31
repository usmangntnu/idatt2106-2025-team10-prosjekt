import { mount } from '@vue/test-utils'
import ReflectionNoteDetailModal from '@/components/reflection/ReflectionNoteDetailModal.vue'
import { createTestingPinia } from '@pinia/testing'
import { vi } from 'vitest'

vi.mock('@/services/reflectionApi.ts', () => ({
  deleteReflectionNote: vi.fn(() => Promise.resolve()),
}))

describe('ReflectionNoteDetailModal.vue', () => {
  const mockNote = {
    id: 1,
    title: 'Detail Note',
    content: 'Detailed content here',
    createdAt: '2023-11-01T10:00:00Z',
    visibility: 'HOUSEHOLD',
    creator: {
      email: 'me@example.com',
      username: 'me',
    },
  }

  it('renders and emits close', async () => {
    const wrapper = mount(ReflectionNoteDetailModal, {
      props: { isOpen: true, note: mockNote },
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              user: { currentUser: { email: 'me@example.com' } },
            },
          }),
        ],
      },
    })

    expect(wrapper.text()).toContain('Detail Note')

    const buttons = wrapper.findAll('button')
    const closeButton = buttons.find((btn) => btn.text() === 'Lukk')
    await closeButton?.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('close')
  })

  it('shows and confirms delete', async () => {
    const wrapper = mount(ReflectionNoteDetailModal, {
      props: { isOpen: true, note: mockNote },
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              user: { currentUser: { email: 'me@example.com' } },
            },
          }),
        ],
      },
    })

    const deleteButtons = wrapper.findAll('button')
    const deleteBtn = deleteButtons.find((btn) => btn.text() === 'Slett notat')
    await deleteBtn?.trigger('click')
    expect(wrapper.html()).toContain('Bekreft sletting')

    const deleteButtons2 = wrapper.findAll('button')
    const deleteBtn2 = deleteButtons2.find((btn) => btn.text() === 'Slett')
    await deleteBtn2?.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('deleted')
  })
})
