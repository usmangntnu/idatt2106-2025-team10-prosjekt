<template>
  <div class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-bold text-[#022B3A]">Krisehåndtering</h1>
      <p class="text-[#022B3A] mt-2 text-lg max-w-xl mx-auto">
        Forbered deg før krisen, håndter den underveis, og reflekter i
        etterkant.
      </p>
    </div>

    <div
      class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8 w-full max-w-6xl"
    >
      <DashboardCard
        title="Før"
        description="📝 Ta en quiz for å sjekke din kunnskap om beredskap og finn nærmeste tilfluktssteder."
        buttonText="Planlegg nå"
        buttonColor="#1F7A8C"
        @click="goTo('before-crisis')"
      />
      <DashboardCard
        title="Under"
        description="⚠️ Sjekk ditt beredskapslager, se din husstand og finn nærmeste tilfluktssteder."
        buttonText="Håndter nå"
        buttonColor="#1F7A8C"
        @click="goTo('under-crisis')"
      />
      <DashboardCard
        title="Etter"
        description="💬 Reflekter og dokumenter læring etter krisen."
        buttonText="Reflekter"
        buttonColor="#1F7A8C"
        @click="goTo('after-crisis')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import DashboardCard from '@/components/home/DashboardCard.vue'

const router = useRouter()
const goTo = (path: string) => router.push(`/${path}`)
</script>
