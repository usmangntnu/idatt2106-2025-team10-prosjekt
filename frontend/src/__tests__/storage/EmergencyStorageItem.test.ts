import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import EmergencyStorageItem from '@/components/storage/EmergencyStorageItem.vue'
import * as api from '@/services/EmergencyStorageApi'

vi.mock('@/services/EmergencyStorageApi', () => ({
  updateStorageItem: vi.fn(),
}))

import { editingId } from '@/composables/useGlobalEditing'
beforeEach(() => {
  editingId.value = null
})

describe('EmergencyStorageItem', () => {
  const item = {
    id: 1,
    name: 'Vann',
    unit: 'L',
    categoryId: 1,
    currentStock: 2,
    recommendedStockForHousehold: 6,
    stockCompletionPercentage: 0.33,
    expirationDate: new Date(Date.now() + 3 * 86400000).toISOString(), // 3 days from now
  }

  it('renders item name and stock', () => {
    const wrapper = mount(EmergencyStorageItem, { props: { item } })
    const displayText = wrapper.text().replace(/\s+/g, ' ')
    expect(displayText).toContain('Vann')
    expect(displayText).toContain('2 / 6')
  })

  it('displays expiry info', () => {
    const wrapper = mount(EmergencyStorageItem, { props: { item } })
    expect(wrapper.text()).toContain('Utløper om')
  })

  it('enters edit mode on click', async () => {
    const wrapper = mount(EmergencyStorageItem, { props: { item } })
    await wrapper.trigger('click')
    expect(editingId.value).toBe(item.id)
  })

  it('calls API and emits on save', async () => {
    const mockUpdate = vi.mocked(api.updateStorageItem)
    mockUpdate.mockResolvedValueOnce({
      stockCompletionPercentage: 0.5,
      expirationDate: item.expirationDate,
    })

    const wrapper = mount(EmergencyStorageItem, { props: { item } })
    await wrapper.trigger('click')

    const input = wrapper.find('input')
    await input.setValue(4)
    await wrapper
      .find('button[aria-label="Lagre ny beholdning"]')
      .trigger('click')

    await flushPromises()

    expect(mockUpdate).toHaveBeenCalled()
    expect(wrapper.emitted('updated')).toBeTruthy()
  })
})
