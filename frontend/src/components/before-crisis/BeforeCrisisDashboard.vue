<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center text-white"
  >
    <div class="bg-[#1F7A8C] px-6 py-4 rounded-xl shadow mb-10 text-center">
      <h1 class="text-3xl font-bold tracking-wide">🛡️ Før Krise</h1>
      <p class="mt-1 text-sm text-[#FFFFFF]">
        Forbered deg før en krise inntreffer.
      </p>
    </div>

    <div
      class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 gap-8 w-full max-w-6xl"
    >
      <div
        class="bg-white text-[#022B3A] rounded-xl p-6 shadow-md flex flex-col justify-between hover:scale-[1.02] transition"
      >
        <div>
          <h2 class="text-xl font-bold mb-1">Quiz</h2>
          <p class="text-[#1F7A8C]">
            Lær om beredskap og bli bedre forberedt på en krise ved å ta en quiz
            om beredskap!
          </p>
        </div>
        <router-link
          to="/quiz"
          class="mt-4 inline-block bg-[#1F7A8C] text-white text-center py-2 rounded-lg font-semibold hover:brightness-110 transition"
        >
          Ta en quiz
        </router-link>
      </div>

      <div
        class="bg-white text-[#022B3A] rounded-xl p-6 shadow-md flex flex-col justify-between hover:scale-[1.02] transition"
      >
        <div>
          <h2 class="text-xl font-bold mb-1">🗺️ Tilfluktskart</h2>
          <p class="text-[#1F7A8C]">
            Finn nærmeste trygge steder og møtepunkter slik at du er forberedt
            når en krise inntreffer.
          </p>
        </div>
        <router-link
          to="/map"
          class="mt-4 inline-block bg-[#1F7A8C] text-white text-center py-2 rounded-lg font-semibold hover:brightness-110 transition"
        >
          Se kart
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>
