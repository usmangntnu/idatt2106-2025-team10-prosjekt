<template>
  <footer class="bg-[#022B3A] text-white py-12 px-6">
    <div
      class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-x-8 gap-y-12"
    >
      <!-- Logo og beskrivelse -->
      <div class="flex flex-col items-start">
        <router-link
          to="/"
          class="mb-4 flex items-center"
          aria-label="Til forsiden"
        >
          <h2 class="text-xl font-bold mr-2">Krisefikser.no</h2>
          <img
            :src="logoSrc"
            alt="Krisefikser logo"
            class="h-8 w-auto"
            loading="lazy"
            decoding="async"
          />
        </router-link>
        <p class="text-lg italic text-gray-300">Din trygghet i utrygge tider</p>
      </div>

      <!-- Navigasjon -->
      <div class="flex flex-col">
        <h3 class="text-lg font-semibold mb-4">Navigasjon</h3>
        <ul class="space-y-2">
          <li>
            <router-link to="/" class="hover:underline">Hjem</router-link>
          </li>
          <li>
            <router-link to="/map" class="hover:underline">Kart</router-link>
          </li>
          <li>
            <router-link to="/storage" class="hover:underline"
              >Beredskapslager</router-link
            >
          </li>
          <li>
            <router-link to="/household" class="hover:underline"
              >Husstand</router-link
            >
          </li>
          <li>
            <router-link to="/quiz" class="hover:underline">Quiz</router-link>
          </li>
        </ul>
      </div>

      <!-- Om oss -->
      <div class="flex flex-col">
        <h3 class="text-lg font-semibold mb-4">Om oss</h3>
        <ul class="space-y-2">
          <li>
            <router-link to="/om-oss" class="hover:underline"
              >Om Krisefikser</router-link
            >
          </li>
          <li>
            <router-link to="/personvern" class="hover:underline"
              >Personvern</router-link
            >
          </li>
        </ul>
      </div>

      <!-- Kontakt -->
      <div class="flex flex-col">
        <h3 class="text-lg font-semibold mb-4">Kontakt</h3>
        <p class="mb-2 text-sm">
          Dersom du har spørsmål eller tilbakemeldinger er det bare å kontakte
          oss her!
        </p>
        <a
          href="https://mail.google.com/mail/?view=cm&to=krisefikser4@gmail.com"
          target="_blank"
          class="hover:underline text-sm"
        >
          📧 krisefikser4@gmail.com
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import logoSrc from '@/assets/logoSys.png'
</script>
