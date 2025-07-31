<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-16 px-6 flex flex-col items-center text-white"
  >
    <div class="bg-[#1F7A8C] px-6 py-4 rounded-xl shadow mb-10 text-center">
      <h1 class="text-3xl font-bold tracking-wide">📄 Etter Krise</h1>
      <p class="mt-1 text-sm text-[#FFFFFF]">
        Reflekter og sjekk din beredksap etter krisen.
      </p>
    </div>

    <div
      class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 gap-8 w-full max-w-6xl"
    >
      <div
        class="bg-white text-[#022B3A] rounded-xl p-6 shadow-md flex flex-col justify-between hover:scale-[1.02] transition"
      >
        <div>
          <h2 class="text-xl font-bold mb-1">Refleksjonsnotat</h2>
          <p class="text-[#1F7A8C]">
            Reflekter rundt din håndtering av krisesituasjonen og les andres
            refleksjoner!
          </p>
        </div>
        <router-link
          to="/reflections"
          class="mt-4 inline-block bg-[#1F7A8C] text-white text-center py-2 rounded-lg font-semibold hover:brightness-110 transition"
        >
          Skriv et notat
        </router-link>
      </div>

      <div
        class="bg-white text-[#022B3A] rounded-xl p-6 shadow-md flex flex-col justify-between hover:scale-[1.02] transition"
      >
        <div>
          <h2 class="text-xl font-bold mb-1">📦 Beredskapslager</h2>
          <p class="text-[#1F7A8C]">
            Se ditt beredskapslager etter krisen for å se hvor godt forberedt du
            var på krisen.
          </p>
        </div>
        <router-link
          to="/storage"
          class="mt-4 inline-block bg-[#1F7A8C] text-white text-center py-2 rounded-lg font-semibold hover:brightness-110 transition"
        >
          Åpne lager
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>
