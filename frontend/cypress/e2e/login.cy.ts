describe('Login Page', () => {
    it('logs in with valid credentials', () => {
      cy.visit('/login');
  
      // Fill in the form
      cy.get('input#email').type('sander.rusten.berge@gmail.com');
      cy.get('input#password').type('Sander1.');
  
      // Wait until the login button is enabled, then click
      cy.get('button[type="submit"]').should('not.be.disabled').click();
  
      // Ensure no error message appears
      cy.contains('Feil epost eller passord').should('not.exist');
  
      // Confirm redirect to home
      cy.url({ timeout: 10000 }).should('not.include', '/login');
  
      // Confirm something visible on home page
      cy.contains('Logg ut').should('exist');
  
      // Log success
      cy.log(' Login test passed and finished');
    });
  });
  