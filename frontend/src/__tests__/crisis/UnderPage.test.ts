import { describe, it, expect } from 'vitest'
import '@testing-library/jest-dom'
import UnderPage from '../../components/crisis/UnderPage.vue'
import { render, screen } from '@testing-library/vue'

describe('Test that under-dashboard renders', () => {
  it('should render the page', () => {
    render(UnderPage)
    expect(screen.getByText('🚨 Under Krise')).toBeInTheDocument()
  })
})
