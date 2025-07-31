<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center text-white"
  >
    <!-- Hero Header -->
    <div class="bg-[#1F7A8C] px-6 py-4 rounded-xl shadow mb-10 text-center">
      <h1 class="text-3xl font-bold tracking-wide">🚨 Etter Krise</h1>
      <p class="mt-1 text-sm text-[#FFFFFF]">
        Etter at krisen er over, er det viktig å reflektere over erfaringene og
        oppdatere dine beredskapsplaner.
      </p>
    </div>

    <!-- Cards Grid -->
    <div class="max-w-6xl w-full grid grid-cols-1 sm:grid-cols-2 gap-8">
      <InfoCard
        icon="📄"
        title="Refleksjonsnotat"
        description="Skriv ned hva som fungerte og hva som må forbedres for fremtidige kriser."
        link="/register"
        linkText="Registrer en bruker for å skrive et notat"
      />

      <InfoCard
        icon="🔧"
        title="Oppdater Beredskap"
        description="Oppdater ditt lager og sjekklister basert på erfaringer fra siste krise."
        quote="Blah blah"
        link="/register"
        linkText="Registrer en bruker for å åpne lager"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import InfoCard from '@/components/after-crisis/InfoCard.vue'
</script>
