/// <reference types="cypress" />

Cypress.Commands.add('login', () => {
    cy.visit('/login');
  
    // Fill in login form
    cy.get('input#email').type('sander.rusten.berge@gmail.com');
    cy.get('input#password').type('Sander1.');
  
    // Wait until the login button is enabled, then click
    cy.get('button[type="submit"]').should('not.be.disabled').click();
  
    // Ensure no error message appears
    cy.contains('Feil epost eller passord').should('not.exist');
  
    // Confirm redirect to home
    cy.url({ timeout: 10000 }).should('not.include', '/login');
  
    // Confirm home page loaded (adjust text if needed)
    cy.contains('Logg ut').should('exist');
  });
  
  declare global {
    namespace Cypress {
      interface Chainable {
        /**
         * Custom command to log in with valid credentials
         */
        login(): Chainable<void>;
      }
    }
  }
  
  export {};
  