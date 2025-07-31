<template>
  <div
    class="min-h-screen flex flex-col justify-center items-center p-10 text-center"
    style="background-color: #e1e5f2"
  >
    <div
      class="bg-white shadow-xl rounded-2xl px-8 py-10 max-w-md w-full space-y-8 border"
      style="border-color: #bfdbf7"
    >
      <h1 class="text-3xl font-extrabold" style="color: #022b3a">
        Verifiser e-posten din
      </h1>
      <p class="leading-relaxed text-base" style="color: #1f7a8c">
        Vi har sendt en bekreftelseslenke til<br />
        <span class="font-semibold" style="color: #022b3a">{{ userEmail }}</span
        >.<br />
        Klikk på lenken i e-posten for å aktivere kontoen din.
      </p>

      <div class="flex flex-col gap-4">
        <a
          href="https://mail.google.com"
          target="_blank"
          class="w-full py-3 rounded-xl font-medium flex items-center justify-center gap-2 text-white shadow-md hover:shadow-lg transition-all duration-200"
          style="background-color: #bfdbf7; color: #022b3a"
        >
          <img
            src="https://www.gstatic.com/images/branding/product/1x/gmail_48dp.png"
            alt="Gmail"
            class="w-5 h-5"
          />
          Åpne Gmail
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const userStore = useUserStore()
const route = useRoute()

const userEmail = computed(
  () => userStore.pendingEmail || (route.query.email as string) || 'din e-post'
)
</script>
