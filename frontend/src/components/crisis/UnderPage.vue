<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center text-white"
  >
    <div class="bg-[#1F7A8C] px-6 py-4 rounded-xl shadow mb-12 text-center">
      <h1 class="text-3xl font-bold tracking-wide">🚨 Under Krise</h1>
      <p class="mt-1 text-sm text-[#FFFFFF]">
        Når krisen først inntreffer, er det viktig å ha rask tilgang til riktig
        informasjon og ressurser.
      </p>
    </div>

    <div class="max-w-5xl w-full grid gap-8 md:grid-cols-3 text-[#022B3A]">
      <div
        class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200"
      >
        <div class="text-4xl mb-4">📦</div>
        <h2 class="text-xl font-bold mb-2">Beredskapslager</h2>
        <p class="text-[#1F7A8C] mb-2">
          Et godt beredskapslager gir trygghet. Du får oversikt over hva du har
          av mat, vann, medisiner og annet kritisk utstyr – og systemet hjelper
          deg å holde det oppdatert.
        </p>
        <p class="text-sm italic text-[#1F7A8C]">
          «Alle husstander bør ha nødvendige varer for å klare seg i minst tre
          døgn uten strøm, vann eller butikk.» –
          <a
            href="https://www.sikkerhverdag.no/"
            class="underline hover:text-[#022B3A]"
            target="_blank"
            rel="noopener noreferrer"
            >DSB.no</a
          >
        </p>
      </div>

      <div
        class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200"
      >
        <div class="text-4xl mb-4">🏠</div>
        <h2 class="text-xl font-bold mb-2">Husholdning</h2>
        <p class="text-[#1F7A8C] mb-2">
          I kriser er samarbeid avgjørende. Ved å knytte deg til husholdningen
          din kan dere dele ansvar for forsyninger, oppgaver og varsler – og
          sørge for at ingen blir glemt.
        </p>
        <p class="text-sm italic text-[#1F7A8C]">
          «Snakk sammen på forhånd og lag en plan for hvordan dere skal håndtere
          ulike hendelser.» –
          <a
            href="https://www.sikkerhverdag.no/"
            class="underline hover:text-[#022B3A]"
            target="_blank"
            rel="noopener noreferrer"
            >DSB.no</a
          >
        </p>
      </div>

      <div
        class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200"
      >
        <div class="text-4xl mb-4">🗺️</div>
        <h2 class="text-xl font-bold mb-2">Tilfluktskart</h2>
        <p class="text-[#1F7A8C] mb-2">
          Vårt interaktive kart gir deg rask oversikt over offentlige
          tilfluktsrom i nærheten. Systemet bruker lokasjonen din og gir
          veibeskrivelser i sanntid.
        </p>
        <p class="text-sm italic text-[#1F7A8C]">
          «Finn ut hvor nærmeste tilfluktsrom er i fredstid. I en krisesituasjon
          kan det være for sent.» –
          <a
            href="https://www.sikkerhverdag.no/"
            class="underline hover:text-[#022B3A]"
            target="_blank"
            rel="noopener noreferrer"
            >DSB.no</a
          >
        </p>
        <button
          @click="() => $router.push('/map')"
          class="mt-6 w-full py-2.5 rounded-lg font-semibold transition duration-200 hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1F7A8C] text-white"
          :style="{ backgroundColor: '#1F7A8C' }"
        >
          Til kart
        </button>
      </div>
    </div>

    <div class="mt-16 text-center max-w-2xl">
      <h2 class="text-[#022B3A] text-2xl font-bold mb-2">
        🧭 Vær forberedt før krisen treffer
      </h2>
      <p class="text-[#022B3A] mb-4">
        Med en gratis bruker får du tilgang til husholdningsoversikt,
        beredskapslager og viktige varsler tilpasset din lokasjon. I en krise
        teller hvert sekund – og hver ressurs.
      </p>
      <router-link
        to="/register"
        class="text-[#BFDBF7] bg-[#022B3A] font-semibold py-2 px-5 rounded-lg hover:brightness-105 transition"
      >
        Registrer deg
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// Ingen logikk nødvendig enda
</script>
