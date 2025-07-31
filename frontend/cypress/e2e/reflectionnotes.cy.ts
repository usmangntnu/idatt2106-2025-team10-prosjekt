// cypress/e2e/reflection-notes.cy.ts

describe('Create Reflection Note', () => {
    beforeEach(() => {
      cy.login();
    });
  
    it('Creates a new private reflection note and verifies it exists in My Notes', () => {
      cy.visit('/reflections');
  
      // Click "Lag nytt notat" button
      cy.contains('button', 'Lag nytt notat').click();
  
      // Fill out the form in the modal
      cy.get('#title').type('Testnotat fra Cypress');
      cy.get('#content').type('Dette er innholdet i notatet opprettet av Cypress-test.');
  
      // Click the save button
      cy.contains('button', 'Lagre').click();
  
      // Click "Dine notater" to go to user's notes
      cy.get('.space-x-4 > .bg-gray-100').click();
  
      // Confirm that the new note appears
      cy.contains('Testnotat fra Cypress').should('exist');
    });
  });