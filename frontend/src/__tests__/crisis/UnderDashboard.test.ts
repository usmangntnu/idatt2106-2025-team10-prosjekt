import { describe, it, expect } from 'vitest'
import '@testing-library/jest-dom'
import UnderDashboard from '@/components/crisis/UnderDashboard.vue'
import { render, screen } from '@testing-library/vue'

describe('Test that under dahsboard renders', () => {
  it('should render the page', () => {
    render(UnderDashboard)
    expect(
      screen.getByText('Handle raskt. Her finner du ressursene dine!')
    ).toBeInTheDocument()
  })
})
