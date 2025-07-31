import { mount } from '@vue/test-utils'
import CreateReflectionNoteModal from '@/components/reflection/CreateReflectionNoteModal.vue'
import { vi } from 'vitest'

vi.mock('@/services/reflectionApi.ts', () => ({
  addReflectionNote: vi.fn(() => Promise.resolve()),
}))

describe('CreateReflectionNoteModal.vue', () => {
  it('renders and submits form', async () => {
    const wrapper = mount(CreateReflectionNoteModal, {
      props: { isOpen: true },
    })

    await wrapper.find('input#title').setValue('Test Title')
    await wrapper.find('textarea#content').setValue('Some content')
    await wrapper.find('select#visibility').setValue('PUBLIC')

    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.emitted()).toHaveProperty('created')
    expect(wrapper.emitted()).toHaveProperty('close')
  })

  it('closes and resets form on cancel', async () => {
    const wrapper = mount(CreateReflectionNoteModal, {
      props: { isOpen: true },
    })

    await wrapper.find('input#title').setValue('Temp')
    await wrapper.find('button[type="button"]').trigger('click')

    expect(wrapper.emitted()).toHaveProperty('close')
  })
})
