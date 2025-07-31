import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import MainDashboard from '@/components/home/MainDashboard.vue'
import DashboardCard from '@/components/home/DashboardCard.vue'

const pushMock = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock,
  }),
}))

describe('MainDashboard.vue', () => {
  it('renders heading and intro text', () => {
    const wrapper = mount(MainDashboard)

    expect(wrapper.text()).toContain('Krisehåndtering')
    expect(wrapper.text()).toContain('Forbered deg før krisen')
    expect(wrapper.text()).toContain('Håndter nå')
    expect(wrapper.text()).toContain('Planlegg nå')
    expect(wrapper.text()).toContain('Reflekter')
  })

  it('renders three DashboardCard components', () => {
    const wrapper = mount(MainDashboard)
    const cards = wrapper.findAllComponents(DashboardCard)
    expect(cards.length).toBe(3)
  })

  it('calls router.push with correct path when clicking a DashboardCard', async () => {
    const wrapper = mount(MainDashboard)

    const cards = wrapper.findAllComponents(DashboardCard)
    await cards[0].vm.$emit('click')

    expect(pushMock).toHaveBeenCalledWith('/before-crisis')
  })
})
