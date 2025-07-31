import { mount } from '@vue/test-utils'
import PreparationGroup from '@/components/PreparationGroup.vue'
import { describe, it, expect } from 'vitest'

describe('PreparationGroup.vue', () => {
  it('renders label and slot content', () => {
    const wrapper = mount(PreparationGroup, {
      props: { label: 'Test Label' },
      slots: { default: '<div>Slot Content</div>' },
    })

    expect(wrapper.text()).toContain(
      'Se og oppdater ditt beredskap Mat og drikkeVarme og energiInformasjonFørstehjelp'
    )
  })
})
