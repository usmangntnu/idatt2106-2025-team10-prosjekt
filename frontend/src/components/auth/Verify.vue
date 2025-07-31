<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AuthApi } from '@/services/authApi.ts'
import { ref } from 'vue'
import { handleApiError } from '@/utils/handleApiError.ts'

const route = useRoute()
const router = useRouter()
const statusMessage = ref('Verifiserer brukeren din...')

onMounted(async () => {
  const token = route.query.token
  if (!token || typeof token !== 'string') {
    statusMessage.value = 'Ugyldig eller manglende epost-verifiseringstoken.'
    return
  }

  try {
    await AuthApi.verifyEmail(token)
    statusMessage.value =
      'Brukeren din er nå verifisert! Du blir sendt til husholdningssiden'
    setTimeout(() => {
      router.push('/join-household')
    }, 1500)
  } catch (error) {
    statusMessage.value = `Verifisering av bruker feilet. Lenken kan være utløpt eller ugyldig.`
    console.error(error)
  }
})
</script>

<template>
  <div class="p-6 text-center">
    <p>{{ statusMessage }}</p>
  </div>
</template>
-
