import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import EmergencyStorageGrid from '@/components/storage/EmergencyStorageGrid.vue'

describe('EmergencyStorageGrid', () => {
  const categories = [
    { id: 1, name: 'Mat' },
    { id: 2, name: 'Vann' },
  ]
  const storageItems = [
    { id: 1, categoryId: 1, name: 'Brød', currentStock: 2 },
    { id: 2, categoryId: 1, name: 'Ris', currentStock: 1 },
    { id: 3, categoryId: 2, name: 'Vannflasker', currentStock: 0 },
  ]

  it('renders one section per category', () => {
    const wrapper = mount(EmergencyStorageGrid, {
      props: { categories, storageItems },
      global: {
        stubs: ['EmergencyStorageItem'],
      },
    })

    const categoryHeadings = wrapper.findAll('h3')
    expect(categoryHeadings).toHaveLength(2)
    expect(wrapper.text()).toContain('Mat')
    expect(wrapper.text()).toContain('Vann')
  })

  it('emits "updated" when child item emits', async () => {
    const wrapper = mount(EmergencyStorageGrid, {
      props: { categories, storageItems },
    })

    await wrapper.vm.$emit('updated')
    expect(wrapper.emitted('updated')).toBeTruthy()
  })
})
