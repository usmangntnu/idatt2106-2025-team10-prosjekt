<template>
  <v-form
    @submit.prevent="handleSubmit"
    class="w-full max-w-md mx-auto py-6 space-y-4"
  >
    <v-text-field
      v-model="form.username"
      label="Username"
      type="text"
      required
      :disabled="loading"
    />

    <v-text-field
      v-model="form.password"
      label="Password"
      type="password"
      required
      :disabled="loading"
    />

    <v-btn
      type="submit"
      :loading="loading"
      color="primary"
      block
      class="text-white"
      :disabled="loading"
    >
      Admin Sign In
    </v-btn>

    <div class="text-center text-sm text-gray-500">
      <a @click.prevent="handleResetPassword" class="cursor-pointer underline">
        Forgot password?
      </a>
    </div>
  </v-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { AuthRequest } from '@/types/AuthDto.ts'
import { useUserStore } from '@/stores/user.ts'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

function handleResetPassword() {
  router.push('/admin/reset-password')
}

async function handleSubmit() {
  loading.value = true
  try {
    const authRequest: AuthRequest = {
      username: form.username,
      password: form.password,
    }

    await userStore.login(authRequest)

    if (!userStore.isAdmin) {
      throw new Error('You are not authorized as an admin.')
    }

    await router.push('/admin/dashboard')
  } catch (error) {
    console.error('Admin login failed:', error)
    alert('Admin login failed. Please check your credentials.')
  } finally {
    loading.value = false
  }
}
</script>
