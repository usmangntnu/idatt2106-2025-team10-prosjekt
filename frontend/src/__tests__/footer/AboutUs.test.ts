import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AboutUs from '@/components/footer/AboutUs.vue'

describe('AboutUs.vue', () => {
  it('renders the main heading and contact email', () => {
    const wrapper = mount(AboutUs)

    expect(wrapper.text()).toContain('Om oss')
    expect(wrapper.html()).toContain('krisefikser4@gmail.com')
  })

  it('renders all listed team members', () => {
    const wrapper = mount(AboutUs)
    const people = [
      'Aryan Malekian',
      'Jonathan Skomsøy Hubertz',
      'Mikael Stray Frøyshov',
      'Sander Berge',
      'Sander Sandvik Nessa',
      'Scott du Plessis',
      'Usman Ghafoorzai',
    ]

    for (const name of people) {
      expect(wrapper.text()).toContain(name)
    }
  })

  it('renders correct mailto links', () => {
    const wrapper = mount(AboutUs)
    const links = wrapper.findAll('a')
    const teamLinks = links.filter((a) =>
      a.attributes('href')?.includes('outlook')
    )

    expect(teamLinks.length).toBeGreaterThanOrEqual(7)
    expect(teamLinks[0].attributes('href')).toContain('aryanm@stud.ntnu.no')
  })
})
