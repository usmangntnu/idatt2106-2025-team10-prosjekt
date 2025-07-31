import { describe, it, vi, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Footer from '@/components/footer/Footer.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { render } from '@testing-library/vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [],
})

render(Footer, {
  global: {
    plugins: [router],
  },
})

vi.mock('@/assets/logoSys.png', () => ({
  default: 'mocked-logo.png',
}))

describe('Footer.vue', () => {
  it('renders logo and description text', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          'router-link': {
            props: ['to'],
            template: '<a :href="to"><slot /></a>',
          },
        },
      },
    })

    expect(wrapper.find('img[alt="Krisefikser logo"]').exists()).toBe(true)
  })

  it('renders navigation and about links', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          'router-link': {
            props: ['to'],
            template: '<a :href="to"><slot /></a>',
          },
        },
      },
    })

    const links = wrapper.findAll('a')
    const hrefs = links.map((link) => link.attributes('href'))

    expect(hrefs).toContain('/')
    expect(hrefs).toContain('/map')
    expect(hrefs).toContain('/om-oss')
    expect(hrefs).toContain('/personvern')
  })

  it('contains contact email', () => {
    const wrapper = mount(Footer)
    expect(wrapper.html()).toContain('krisefikser4@gmail.com')
  })
})
