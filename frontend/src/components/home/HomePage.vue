<template>
  <div class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center">
    <h1
      class="text-4xl sm:text-5xl font-extrabold text-[#022B3A] mb-4 text-center"
    >
      Velkommen til Beredskapsportalen
    </h1>
    <p class="text-lg sm:text-xl text-[#1F7A8C] mb-12 text-center max-w-2xl">
      Få oversikt over hvordan du kan forberede deg på en krise.
    </p>

    <div class="flex flex-col sm:flex-row gap-4 mb-12">
      <button
        @click="goToLogin"
        class="bg-[#1F7A8C] text-white font-medium py-3 px-6 rounded-xl shadow-md hover:scale-105 transition-all duration-200"
      >
        Logg inn
      </button>
      <button
        @click="goToRegister"
        class="bg-white text-[#022B3A] border border-[#1F7A8C] font-medium py-3 px-6 rounded-xl shadow-md hover:bg-[#e6f0f7] transition-all duration-200"
      >
        Registrer deg
      </button>
    </div>

    <div
      class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10 w-full max-w-6xl px-2"
    >
      <DashboardCard
        title="Før"
        description="Få oversikt over siden for før krise"
        buttonText="Gå til før krise"
        buttonColor="#1F7A8C"
        @click="goTo('before-crisis')"
      />
      <DashboardCard
        title="Under"
        description="Få oversikt over siden for under krise"
        buttonText="Gå til under krise"
        buttonColor="#1F7A8C"
        @click="goTo('under-crisis')"
      />
      <DashboardCard
        title="Etter"
        description="Få oversikt over siden for etter krise"
        buttonText="Gå til etter krise"
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
const goToLogin = () => router.push('/login')
const goToRegister = () => router.push('/register')
</script>
