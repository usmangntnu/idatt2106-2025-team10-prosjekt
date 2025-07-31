import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import Footer from '@/components/footer/Footer.vue'
import AboutUs from '@/components/footer/AboutUs.vue'
import PrivacyPolicy from '@/components/footer/Privacy.vue'
import '@testing-library/jest-dom'
import { createRouter, createWebHistory } from 'vue-router'

// Dummy router to support <router-link> in components
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/personvern', component: { template: '<div>Personvern</div>' } },
    { path: '/om-oss', component: { template: '<div>Om Krisefikser</div>' } },
    { path: '/', component: { template: '<div>Hjem</div>' } },
    { path: '/map', component: { template: '<div>Kart</div>' } },
    { path: '/storage', component: { template: '<div>Beredspaslager</div>' } },
    { path: '/household', component: { template: '<div>Household</div>' } },
    { path: '/quiz', component: { template: '<div>Quiz</div>' } },
  ],
})

describe('Footer.vue', () => {
  it('renders footer links correctly', async () => {
    render(Footer, {
      global: {
        plugins: [router],
      },
    })

    expect(await screen.findByText('Personvern')).toBeInTheDocument()
    expect(screen.getByText('Om Krisefikser')).toBeInTheDocument()
    expect(screen.getByText('Hjem')).toBeInTheDocument()
    expect(screen.getByText('Kart')).toBeInTheDocument()
    expect(screen.getByText('Beredskapslager')).toBeInTheDocument()
    expect(screen.getByText('Husstand')).toBeInTheDocument()
    expect(screen.getByText('Quiz')).toBeInTheDocument()
  })

  it('contains correct RouterLink hrefs', async () => {
    render(Footer, {
      global: {
        plugins: [router],
      },
    })

    expect(screen.getByText('Personvern').closest('a')).toHaveAttribute(
      'href',
      '/personvern'
    )
    expect(screen.getByText('Om Krisefikser').closest('a')).toHaveAttribute(
      'href',
      '/om-oss'
    )
    expect(screen.getByText('Hjem').closest('a')).toHaveAttribute('href', '/')
    expect(screen.getByText('Kart').closest('a')).toHaveAttribute(
      'href',
      '/map'
    )
    expect(screen.getByText('Beredskapslager').closest('a')).toHaveAttribute(
      'href',
      '/storage'
    )
    expect(screen.getByText('Husstand').closest('a')).toHaveAttribute(
      'href',
      '/household'
    )
    expect(screen.getByText('Quiz').closest('a')).toHaveAttribute(
      'href',
      '/quiz'
    )
  })

  it('renders AboutUs component', async () => {
    render(AboutUs)
    expect(await screen.findByText(/om oss/i)).toBeInTheDocument()
  })

  it('renders PrivacyPolicy component', async () => {
    render(PrivacyPolicy)
    expect(await screen.findByText(/personvernerklæring/i)).toBeInTheDocument()
  })
})
