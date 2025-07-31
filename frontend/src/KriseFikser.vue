<template>
  <NavBar class="sticky inset-x-0 top-0 h-[var(--header-h)] z-50" />

  <div id="layout" class="flex flex-col bg-[#E1E5F2] min-h-screen">
    <main class="flex-1">
      <router-view />
    </main>

    <Footer />
  </div>

  <svg width="0" height="0" style="position: absolute">
    <defs>
      <filter id="circle-gradient">
        <feGaussianBlur in="SourceGraphic" stdDeviation="5" result="blur" />
        <feColorMatrix
          in="blur"
          mode="matrix"
          values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"
          result="glow"
        />
        <feBlend in="SourceGraphic" in2="glow" mode="normal" />
      </filter>

      <filter id="poly-gradient">
        <feGaussianBlur in="SourceGraphic" stdDeviation="2" result="blur" />
        <feColorMatrix
          in="blur"
          mode="matrix"
          values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 12 -5"
          result="glow"
        />
        <feBlend in="SourceGraphic" in2="glow" mode="normal" />
      </filter>
    </defs>
  </svg>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/footer/Footer.vue'
</script>

<style>
/* ---------------------------------------------------
   Globale layout-konstanter  (brukes i calc() under)
   --------------------------------------------------- */
:root {
  --nav-height: 4rem; /* h-16  */
  --footer-height: 3.5rem; /* h-14 */
}

/* ---------------------------------------------------
   Sørg for at body aldri sklir bak navbaren
   --------------------------------------------------- */
body {
  margin: 0;
  /* NB: padding-top settes av Tailwind-klassen pt-16 på #layout.
     Denne backup-linjen trår til hvis du nå/evt. senere fjerner klassen. */
  padding-top: var(--nav-height);
}

/* ---------------------------------------------------
   Hovedinnholdet får alltid MINST høyden av skjermen
   minus navbar og footer.
   Dette gjør at footeren “henger” i bunnen, men
   blir først synlig når man scroller forbi innholdet.
   --------------------------------------------------- */
#layout > main {
  min-height: calc(100vh - var(--nav-height) - var(--footer-height));
  /* Hindrer uønsket horisontal scrolling fra brede barne-elementer */
  overflow-x: hidden;
}
</style>
