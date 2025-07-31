// cypress/e2e/create-household.cy.ts

describe('Create Household', () => {
    beforeEach(() => {
      cy.login();
    });
  
    it('Creates a new household or confirms user is already in one', () => {
      cy.visit('/join-household');
  
      // Fill out household name
      cy.get('input#householdName').type('sander');
  
      // Select address
      cy.get('.multiselect__tags').click().type('Sandgata 10');
  
      // Wait for and select first suggestion
      cy.get('.multiselect__element', { timeout: 10000 }).first().click();
  
      // Submit form
      cy.contains('button', 'Lag husholdning').click();
  
      // Validate one of two possible messages
      cy.get('body', { timeout: 10000 }).should($body => {
        const text = $body.text();
        expect(
          text.includes('Husholdning med navn sander opprettet!') ||
          text.includes('Du er allerede i en husholdning')
        ).to.be.true;
      });
    });
  });
  