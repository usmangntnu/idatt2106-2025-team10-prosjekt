<template>
  <div
    class="min-h-screen bg-[#E1E5F2] flex justify-center items-center px-4 sm:px-6 py-8"
  >
    <div class="w-full max-w-md bg-white rounded-lg shadow-md p-6 sm:p-8">
      <h1 class="text-2xl font-bold text-center text-gray-800 mb-6">
        Registrer deg
      </h1>

      <form
        ref="formRef"
        novalidate
        @submit.prevent="onSubmit"
        class="space-y-5"
      >
        <!-- First Name Field -->
        <div class="space-y-1">
          <label for="firstname" class="block text-sm font-medium text-gray-700"
            >Fornavn</label
          >
          <input
            id="firstname"
            v-model="form.firstname"
            type="text"
            :disabled="loading"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
          />
          <ErrorMessage :message="errors.firstname" />
        </div>

        <!-- Last Name Field -->
        <div class="space-y-1">
          <label for="lastname" class="block text-sm font-medium text-gray-700"
            >Etternavn</label
          >
          <input
            id="lastname"
            v-model="form.lastname"
            type="text"
            :disabled="loading"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
          />
          <ErrorMessage :message="errors.lastname" />
        </div>

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
              @click="showPassword = !showPassword"
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

        <!-- Confirm Password Field -->
        <div class="space-y-1">
          <label
            for="confirmPassword"
            class="block text-sm font-medium text-gray-700"
            >Bekreft Passord</label
          >
          <div class="relative">
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              :disabled="loading"
              class="w-full px-3 py-2 pr-10 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
            />
            <button
              type="button"
              @click="showConfirmPassword = !showConfirmPassword"
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm text-gray-600"
            >
              <span class="sr-only"
                >{{ showConfirmPassword ? 'Skjul' : 'Vis' }} passord</span
              >
              <v-icon
                :name="showConfirmPassword ? 'fa-eye-slash' : 'fa-eye'"
                class="h-5 w-5"
              />
            </button>
          </div>
          <ErrorMessage :message="errors.confirmPassword" />
        </div>

        <!-- Accept Privacy Policy Checkbox -->
        <div class="space-y-1">
          <div class="flex items-start space-x-2">
            <input
              id="acceptPolicy"
              v-model="form.acceptPolicy"
              type="checkbox"
              :disabled="loading"
              class="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label for="acceptPolicy" class="text-sm text-gray-700">
              Jeg godkjenner
              <a
                href="/personvern"
                target="_blank"
                class="text-blue-600 hover:text-blue-800 underline"
              >
                Personvernerklæringen
              </a>
            </label>
          </div>
          <ErrorMessage :message="errors.acceptPolicy" />
        </div>

        <!-- reCAPTCHA -->
        <div class="space-y-1">
          <label
            for="g-recaptcha-response"
            class="block text-sm font-medium text-gray-700"
            >Complete the captcha to verify that you're a human</label
          >
          <div
            class="g-recaptcha flex justify-center"
            data-sitekey="6LcvECcrAAAAACSxDyMEz4VkHFAKkm7kcIDj37q4"
            data-tabindex="0"
          ></div>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          :disabled="loading"
          :class="['w-full …', shake ? 'animate-shake' : '']"
          @animationend="shake = false"
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
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            Laster...
          </span>
          <span v-else>Registrer</span>
        </button>

        <!-- Sign In Link -->
        <div class="text-center text-sm text-gray-500 pt-2">
          <a
            @click.prevent="onGoToLogin"
            href="#"
            tabindex="0"
            class="cursor-pointer text-blue-600 hover:text-blue-800 underline"
          >
            Har du allerede en bruker? Logg inn
          </a>
        </div>
      </form>

      <!-- Status Message -->
      <div class="mt-4">
        <ErrorMessage :message="statusMessage" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import type { UserCreate } from '@/types/types.ts'
import { AuthApi } from '@/services/authApi.ts'
import { useUserStore } from '@/stores/user.ts'
import ErrorMessage from '@/components/ErrorMessage.vue'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

onMounted(() => {
  //Force load captcha
  if ((window as any).grecaptcha) {
    ;(window as any).grecaptcha.render(document.querySelector('.g-recaptcha'), {
      sitekey: '6LcvECcrAAAAACSxDyMEz4VkHFAKkm7kcIDj37q4',
    })
  } else {
    console.error('reCAPTCHA not loaded')
  }
})

const fieldLabels: Record<keyof typeof errors, string> = {
  firstname: 'Fornavn',
  lastname: 'Etternavn',
  email: 'Epost',
  password: 'Passord',
  confirmPassword: 'Bekreft passord',
  acceptPolicy: 'Personvernerklæringen',
}

const form = reactive({
  firstname: '',
  lastname: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptPolicy: false,
})

const errors = reactive({
  firstname: '',
  lastname: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptPolicy: '',
})

// Validation rules
const required = (value: string, name: string): string =>
  value ? '' : `${name} må fylles ut`
const validEmail = (v: string): string =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? '' : 'Ugyldig epost'

const minPasswordLength = (v: string): string =>
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(v)
    ? ''
    : 'Passordet trenger 8 eller fler tegn, minst en stor bokstav, en liten bokstav, et tall og et spesialtegn'

const passwordsMatch = (pw: string, confirm: string): string =>
  pw === confirm ? '' : 'Passordene er ikke like'
const statusMessage = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const userStore = useUserStore()
const shake = ref(false)

function validateForm(): boolean {
  // Reset errors
  ;(Object.keys(errors) as (keyof typeof errors)[]).forEach((key) => {
    errors[key] = ''
  })

  let isValid = true

  // Fornavn
  const fnErr = required(form.firstname, fieldLabels.firstname)
  if (fnErr) {
    errors.firstname = fnErr
    isValid = false
  }

  // Etternavn
  const lnErr = required(form.lastname, fieldLabels.lastname)
  if (lnErr) {
    errors.lastname = lnErr
    isValid = false
  }

  // Epost
  let emailErr = required(form.email, fieldLabels.email)
  if (!emailErr) emailErr = validEmail(form.email)
  if (emailErr) {
    errors.email = emailErr
    isValid = false
  }

  // Passord
  let pwErr = required(form.password, fieldLabels.password)
  if (!pwErr) pwErr = minPasswordLength(form.password)
  if (pwErr) {
    errors.password = pwErr
    isValid = false
  }

  // Bekreft passord
  let cpwErr = required(form.confirmPassword, fieldLabels.confirmPassword)
  if (!cpwErr) cpwErr = passwordsMatch(form.password, form.confirmPassword)
  if (cpwErr) {
    errors.confirmPassword = cpwErr
    isValid = false
  }

  // Checkbox
  if (!form.acceptPolicy) {
    errors.acceptPolicy = required('', fieldLabels.acceptPolicy)
    isValid = false
  }

  return isValid
}

function focusFirstError(): void {
  const errs = errors as Record<string, string>
  const firstKey = Object.keys(errs).find((key) => !!errs[key])
  if (!firstKey) return

  const el = formRef.value?.querySelector(`#${firstKey}`) as HTMLElement | null
  el?.focus()
}

async function onSubmit() {
  const isValid = validateForm()
  if (!isValid) {
    shake.value = true
    focusFirstError()
    setTimeout(() => (shake.value = false), 300)
    return
  }

  const recaptchaToken = (window as any).grecaptcha.getResponse()
  if (!recaptchaToken) {
    statusMessage.value = 'Vennligst fullfør reCAPTCHA.'
    return
  }

  loading.value = true
  try {
    const registerRequest: UserCreate = {
      firstName: form.firstname,
      lastName: form.lastname,
      email: form.email,
      password: form.password,
      recaptchaToken: recaptchaToken,
    }
    console.log(form.password)
    console.log(recaptchaToken)
    await AuthApi.register(registerRequest)
    userStore.pendingEmail = form.email
    await router.push({ name: 'check-email' })
  } catch (error: any) {
    console.error('Registration failed:', error)
    if (error.response.status === 403) {
      statusMessage.value = 'Captcha verifisering feilet. Vennligst prøv igjen.'
    } else if (error.response.status === 404) {
      statusMessage.value = 'Bruker med eksiterende epost finnes allerede.'
    } else {
      statusMessage.value = 'Feil under registrering. Vennligst prøv igjen.'
    }
  } finally {
    loading.value = false
  }
}

function onGoToLogin() {
  router.push('/login')
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
