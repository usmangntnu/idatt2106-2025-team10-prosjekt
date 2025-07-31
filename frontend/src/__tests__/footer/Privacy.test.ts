import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Privacy from '@/components/footer/Privacy.vue'

describe('Privacy.vue', () => {
  it('renders main heading and sections', () => {
    const wrapper = mount(Privacy)

    expect(wrapper.text()).toContain('Personvernerklæring')
    expect(wrapper.text()).toContain('Behandlingsansvarlig')
    expect(wrapper.text()).toContain('Dine rettigheter')
  })

  it('lists key sections like cookies and retention', () => {
    const wrapper = mount(Privacy)

    expect(wrapper.text()).toContain('Informasjonskapsler')
    expect(wrapper.text()).toContain('Lagring og sletting')
    expect(wrapper.text()).toContain('Endringer i personvernserklæringen')
  })
})
