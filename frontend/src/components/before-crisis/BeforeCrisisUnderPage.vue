<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center text-white"
  >
    <div class="bg-[#1F7A8C] px-6 py-4 rounded-xl shadow mb-12 text-center">
      <h1 class="text-3xl font-bold tracking-wide">🛡️ Før Krise</h1>
      <p class="mt-1 text-sm text-[#FFFFFF]">
        Det beste du kan gjøre i en krise er å være forberedt før den skjer.
      </p>
    </div>

    <div class="max-w-5xl w-full grid gap-8 md:grid-cols-2 text-[#022B3A]">
      <!-- Info -->
      <div
        class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200"
      >
        <h2 class="text-xl font-bold mb-4">🏡 Egenberedskap</h2>

        <p class="text-[#1F7A8C] mb-4">
          Krisefikser hjelper deg med å få oversikt og forbedre deg på krise.
        </p>

        <p class="text-[#1F7A8C] mb-2">
          Mat, vann, varme og informasjon er ting du bør ha en plan for.
        </p>
        <p class="text-[#1F7A8C] mb-10">
          Lag en bruker og få tilgang til din egen beredskapsplan.
        </p>

        <p class="text-sm italic text-[#1F7A8C] mt-10">
          «Du har et personlig ansvar for egen sikkerhet og beredskap..» –
          <a
            href="https://www.dsb.no"
            class="underline hover:text-[#022B3A]"
            target="_blank"
            rel="noopener noreferrer"
            >DSB.no</a
          >
        </p>
      </div>

      <!-- Quiz -->
      <div
        class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200 relative"
      >
        <h2 class="text-xl font-bold mb-3">📋 Quiz</h2>
        <p class="text-[#1F7A8C] mb-4">
          I en krisesituasjon kan kunnskap være like viktig som forsyninger. Vår
          beredskapsquiz gir deg innsikt i hva du bør vite og hva du kanskje har
          oversett.
        </p>
        <p class="text-[#1F7A8C] mb-2">
          Test deg selv - og gjør deg bedre forberedt før det gjelder.
        </p>

        <div
          class="mb-2"
          @mouseenter="showTooltip = !userStore.isAuthenticated"
          @mouseleave="showTooltip = false"
        >
          <button
            @click="router.push('/register')"
            class="mt-6 w-full py-2.5 rounded-lg font-semibold transition duration-200 hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1F7A8C] text-white"
            :style="{ backgroundColor: '#1F7A8C' }"
          >
            Registrer for å ta quiz
          </button>
          <div
            v-if="showTooltip"
            class="absolute top-full left-0 mt-2 text-sm bg-white text-[#1F7A8C] border border-[#1F7A8C] rounded px-3 py-2 shadow-lg z-10"
          >
            Logg inn eller registrer deg for å ta quiz
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const showTooltip = ref(false)
</script>
