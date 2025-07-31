<template>
  <nav
    class="box-border w-full h-auto min-h-16 md:h-24 bg-gradient-to-r from-[#022B3A] to-[#1F7A8C] flex items-center px-4 md:px-6 py-3 relative"
  >
    <div class="flex-1 flex justify-start"></div>

    <div
      class="flex items-center justify-center absolute left-1/2 transform -translate-x-1/2"
    >
      <router-link
        to="/"
        class="flex items-center gap-2 md:gap-3 justify-center cursor-pointer transition-transform duration-200 hover:scale-105"
      >
        <img
          src="@/assets/logoSys.png"
          alt="KriseFikser Logo"
          class="h-10 md:h-15 w-auto"
        />
        <div class="font-bold text-lg md:text-xl text-white">KriseFikser</div>
      </router-link>
    </div>

    <div class="flex-1 flex justify-end items-center gap-3 md:gap-6">
      <div v-if="isAuthenticated" class="flex items-center gap-3 md:gap-6">
        <!-- Dropdown Bell -->
        <div class="relative" ref="dropdownRef">
          <button
            type="button"
            class="relative text-white text-xl md:text-2xl cursor-pointer transition-transform duration-200 hover:scale-110"
            title="Varsler"
            @click.stop="showNotificationsDropdown = !showNotificationsDropdown"
            aria-haspopup="true"
            :aria-expanded="showNotificationsDropdown"
          >
            <v-icon name="co-bell" :scale="isMobile ? 1.5 : 2" />
            <span
              v-if="hasNotifications"
              class="absolute top-0 right-0 block h-2 w-2 md:h-3 md:w-3 rounded-full ring-2 ring-white bg-red-500"
            ></span>
          </button>
          <!-- Notifications Dropdown -->
          <div
            v-if="showNotificationsDropdown"
            class="absolute right-0 mt-2 w-80 z-50"
          >
            <NotificationPanel
              :notifications="notificationStore.notifications"
            />
          </div>
        </div>

        <button
          @click="handleLogout"
          class="transition-transform duration-200 hover:scale-110 p-2 md:p-4 text-sm md:text-base bg-red-500 text-white font-semibold rounded-lg shadow-md hover:bg-red-600"
        >
          Logg ut
        </button>
      </div>
      <router-link
        v-else
        to="/login"
        class="transition-transform duration-200 hover:scale-110"
        title="Login"
      >
        <button
          class="p-2 md:p-4 text-sm md:text-base bg-green-500 text-white font-semibold rounded-lg shadow-md hover:bg-green-600 transition-transform duration-200"
        >
          Logg inn
        </button>
      </router-link>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.ts'
import { useHouseholdStore } from '@/stores/household.ts'
import { useNotificationStore } from '@/stores/notification.ts'
import { computed, watch, onMounted, ref, onBeforeUnmount } from 'vue'
import NotificationPanel from '@/components/storage/NotificationPanel.vue'

const router = useRouter()
const userStore = useUserStore()
const householdStore = useHouseholdStore()
const notificationStore = useNotificationStore()
const showNotificationsDropdown = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

const isAuthenticated = computed(() => userStore.isAuthenticated)
const householdId = computed(() => householdStore.household?.id)
const hasNotifications = computed(() => notificationStore.hasNotifications)
const isMobile = computed(() => window.innerWidth < 768)

function handleClickOutside(event: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    showNotificationsDropdown.value = false
  }
}

function handleResize() {
  // Force reactivity update for responsive elements
  isMobile.value // Access the computed property to trigger reactivity
}

onMounted(() => {
  userStore.fetchCurrentUser()
  if (isAuthenticated.value) {
    householdStore.fetchHousehold()
  }
  window.addEventListener('click', handleClickOutside)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', handleResize)
})

watch(
  [isAuthenticated, householdId],
  ([auth, hId]) => {
    if (auth && hId) {
      notificationStore.fetchNotifications(hId)
    }
  },
  { immediate: true }
)

const handleLogout = async () => {
  await userStore.logout()
  await router.push('/')
}
</script>
