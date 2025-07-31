import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LandingPage from '@/components/home/HomePage.vue' // Update with your actual component path
import DashboardCard from '@/components/home/DashboardCard.vue'
import { useRouter } from 'vue-router'

// Mock the vue-router module
vi.mock('vue-router', () => ({
  useRouter: vi.fn(),
}))

// Mock the DashboardCard component
vi.mock('@/components/home/DashboardCard.vue', () => ({
  default: {
    name: 'DashboardCard',
    props: ['title', 'description', 'buttonText', 'buttonColor'],
    template:
      '<div class="dashboard-card-mock" :data-title="title" :data-description="description" :data-button-text="buttonText" :data-button-color="buttonColor"></div>',
  },
}))

describe('LandingPage', () => {
  const mockRouterPush = vi.fn()

  beforeEach(() => {
    // Reset the mock function before each test
    mockRouterPush.mockReset()

    // Mock the useRouter implementation
    vi.mocked(useRouter).mockReturnValue({
      push: mockRouterPush,
    })
  })

  it('renders correctly with title and description', () => {
    const wrapper = mount(LandingPage)
    expect(wrapper.find('h1').text()).toBe('Velkommen til Beredskapsportalen')
    expect(wrapper.find('p').text()).toBe(
      'Få oversikt over hvordan du kan forberede deg på en krise.'
    )
  })

  it('renders login and register buttons', () => {
    const wrapper = mount(LandingPage)
    const buttons = wrapper.findAll('button')
    expect(buttons.length).toBe(2)
    expect(buttons[0].text()).toBe('Logg inn')
    expect(buttons[1].text()).toBe('Registrer deg')
  })

  it('renders three DashboardCard components', () => {
    const wrapper = mount(LandingPage)
    expect(wrapper.findAllComponents(DashboardCard).length).toBe(3)
  })

  it('redirects to login page when login button is clicked', async () => {
    const wrapper = mount(LandingPage)
    await wrapper.findAll('button')[0].trigger('click')
    expect(mockRouterPush).toHaveBeenCalledWith('/login')
  })

  it('redirects to register page when register button is clicked', async () => {
    const wrapper = mount(LandingPage)
    await wrapper.findAll('button')[1].trigger('click')
    expect(mockRouterPush).toHaveBeenCalledWith('/register')
  })

  it('passes correct props to the DashboardCard components', () => {
    const wrapper = mount(LandingPage)
    const cards = wrapper.findAll('.dashboard-card-mock')

    // First card (Before crisis)
    expect(cards[0].attributes('data-title')).toBe('Før')
    expect(cards[0].attributes('data-description')).toBe(
      'Få oversikt over siden for før krise'
    )
    expect(cards[0].attributes('data-button-text')).toBe('Gå til før krise')
    expect(cards[0].attributes('data-button-color')).toBe('#1F7A8C')

    // Second card (During crisis)
    expect(cards[1].attributes('data-title')).toBe('Under')
    expect(cards[1].attributes('data-description')).toBe(
      'Få oversikt over siden for under krise'
    )
    expect(cards[1].attributes('data-button-text')).toBe('Gå til under krise')
    expect(cards[1].attributes('data-button-color')).toBe('#1F7A8C')

    // Third card (After crisis)
    expect(cards[2].attributes('data-title')).toBe('Etter')
    expect(cards[2].attributes('data-description')).toBe(
      'Få oversikt over siden for etter krise'
    )
    expect(cards[2].attributes('data-button-text')).toBe('Gå til etter krise')
    expect(cards[2].attributes('data-button-color')).toBe('#1F7A8C')
  })

  it('navigates to correct path when DashboardCard is clicked', async () => {
    const wrapper = mount(LandingPage)
    const cards = wrapper.findAllComponents(DashboardCard)

    // Click first card (Before crisis)
    await cards[0].trigger('click')
    expect(mockRouterPush).toHaveBeenCalledWith('/before-crisis')

    // Click second card (During crisis)
    await cards[1].trigger('click')
    expect(mockRouterPush).toHaveBeenCalledWith('/under-crisis')

    // Click third card (After crisis)
    await cards[2].trigger('click')
    expect(mockRouterPush).toHaveBeenCalledWith('/after-crisis')
  })

  it('has a responsive grid layout based on screen size', () => {
    const wrapper = mount(LandingPage)
    const gridDiv = wrapper.find('.grid')
    expect(gridDiv.classes()).toContain('grid-cols-1')
    expect(gridDiv.classes()).toContain('sm:grid-cols-2')
  })
})
