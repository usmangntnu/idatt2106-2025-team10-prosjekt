import { mount } from '@vue/test-utils'
import InviteToHousehold from '@/components/InviteToHousehold.vue'
import { describe, it, expect } from 'vitest'

describe('InviteToHousehold.vue', () => {
  it('emits click when clicked', async () => {
    const wrapper = mount(InviteToHousehold, {
      props: { title: 'Tittel', description: 'Beskrivelse' },
    })
    await wrapper.find('div').trigger('click')

    expect(wrapper.emitted('button-click')).toBeUndefined()
  })
})
