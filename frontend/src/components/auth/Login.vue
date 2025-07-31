<template>
  <div
    class="min-h-screen bg-[#E1E5F2] flex justify-center items-center px-4 sm:px-6 py-8"
  >
    <div class="w-full max-w-md bg-white rounded-lg shadow-md p-6 sm:p-8">
      <h1 class="text-2xl font-bold text-center text-gray-800 mb-6">
        Logg inn
      </h1>

      <form
        ref="formRef"
        novalidate
        @submit.prevent="handleSubmit"
        :class="['space-y-5', shake ? 'animate-shake' : '']"
      >
        <!-- Email Field -->
        <div class="space-y-1">
          <label for="email" class="block text-sm font-medium text-gray-700"
            >Epost</label
          >
          <input
            id="email"
            v-model="form.email"
            type="email"
            :disabled="loading"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
          />
          <ErrorMessage :message="errors.email" />
        </div>

        <!-- Password Field -->
        <div class="space-y-1">
          <label for="password" class="block text-sm font-medium text-gray-700"
            >Passord</label
          >
          <div class="relative">
            <input
              id="password"
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              :disabled="loading"
              class="w-full px-3 py-2 pr-10 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
            />
            <button
              type="button"
              tabindex="0"
              @click="toggleShowPassword"
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm text-gray-600"
            >
              <span class="sr-only"
                >{{ showPassword ? 'Skjul' : 'Vis' }} passord</span
              >
              <v-icon
                :name="showPassword ? 'fa-eye-slash' : 'fa-eye'"
                class="h-5 w-5"
              />
            </button>
          </div>
          <ErrorMessage :message="errors.password" />
        </div>

        <!-- Status Message -->
        <div>
          <ErrorMessage :message="statusMessage" />
        </div>

        <!-- Login Button -->
        <button
          type="submit"
          :disabled="loading"
          class="w-full flex justify-center items-center gap-2 py-3 px-4 text-sm font-medium rounded-xl shadow transition-colors duration-200 text-white bg-[#1F7A8C] hover:bg-[#166172] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1F7A8C] disabled:opacity-60 active:scale-95 active:brightness-90"
        >
          <span v-if="loading" class="inline-flex items-center">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962
                       7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
            Laster...
          </span>
          <span v-else>Logg inn</span>
        </button>

        <!-- Create Account Button -->
        <button
          id="create-account"
          type="button"
          @click="handleCreateAccount"
          :disabled="loading"
          class="w-full flex justify-center items-center py-3 px-4 text-sm font-medium rounded-xl border shadow-sm transition-colors duration-200 text-[#1F7A8C] bg-white border-[#1F7A8C] hover:bg-[#F0F8FA] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1F7A8C] disabled:opacity-50 active:scale-95"
        >
          Opprett en bruker
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import ErrorMessage from '@/components/ErrorMessage.vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<HTMLFormElement | null>(null)
const loading = ref(false)
const shake = ref(false)
const statusMessage = ref('')
const showPassword = ref(false)

const form = reactive({ email: '', password: '' })
const errors = reactive({ email: '', password: '' })

const fieldLabels = { email: 'Epost', password: 'Passord' }

function validateForm(): boolean {
  errors.email = ''
  errors.password = ''
  let valid = true

  if (!form.email) {
    errors.email = `${fieldLabels.email} må fylles ut`
    valid = false
  }

  if (!form.password) {
    errors.password = `${fieldLabels.password} må fylles ut`
    valid = false
  }

  return valid
}

function focusFirstError() {
  const firstKey = Object.keys(errors).find(
    (key) => errors[key as keyof typeof errors]
  )
  if (!firstKey || !formRef.value) return
  const el = formRef.value.querySelector(`#${firstKey}`) as HTMLElement | null
  el?.focus()
}

async function handleSubmit() {
  statusMessage.value = ''
  if (!validateForm()) {
    shake.value = true
    focusFirstError()
    setTimeout(() => (shake.value = false), 300)
    return
  }
  loading.value = true
  try {
    await userStore.login({ email: form.email, password: form.password })
    await router.push('/')
  } catch {
    statusMessage.value = 'Feil epost eller passord. Vennligst prøv igjen.'
    shake.value = true
    await nextTick()
    focusFirstError()
    setTimeout(() => (shake.value = false), 300)
  } finally {
    loading.value = false
  }
}

function toggleShowPassword() {
  showPassword.value = !showPassword.value
}

function handleCreateAccount() {
  router.push('/register')
}
</script>

<style scoped>
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20%,
  60% {
    transform: translateX(-5px);
  }
  40%,
  80% {
    transform: translateX(5px);
  }
}

.animate-shake {
  animation: shake 0.3s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
</style>
