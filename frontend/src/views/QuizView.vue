<template>
  <div
    class="min-h-screen bg-[#E1E5F2] py-12 px-6 relative flex justify-center"
  >
    <div
      class="max-w-6xl w-full grid grid-cols-1 md:grid-cols-12 gap-6 items-start"
    >
      <!-- Quiz content section -->
      <div
        class="md:col-span-9 bg-white shadow-md rounded-lg p-8 space-y-6 min-h-[475px]"
      >
        <div
          class="bg-[#1F7A8C] text-white py-3 px-5 max-w-xl mx-auto rounded-md"
        >
          <h1 class="text-3xl font-bold text-center">Beredskapsquiz</h1>
        </div>
        <p class="text-[#022B3A] text-center text-lg font-semibold mt-2">
          🛡️ Er du beredt på krise?
        </p>
        <p class="text-[#022B3A] text-center text-base mt-2">
          Velg hvor mange spørsmål du vil svare på, og test kunnskapene dine om
          beredskap.
        </p>
        <p class="text-[#022B3A] text-center text-base mt-2">
          📋 Temaer som dekkes: beredskap, førstehjelp, evakuering og mer.
        </p>

        <!-- Quiz start controls -->
        <div v-if="!showQuiz" class="flex flex-col items-center space-y-4">
          <div
            class="flex flex-col sm:flex-row sm:items-center sm:space-x-4 space-y-2 sm:space-y-0"
          >
            <label class="font-medium text-[#022B3A]">Antall spørsmål:</label>
            <select
              v-model.number="selectedCount"
              class="border border-gray-300 rounded-lg p-2 bg-white"
            >
              <option v-for="n in [5, 10, 15]" :key="n" :value="n">
                {{ n }}
              </option>
            </select>
            <button
              @click="handleStartQuiz"
              class="bg-[#1F7A8C] text-white px-6 py-2 rounded-lg hover:brightness-110"
            >
              Start quiz
            </button>
          </div>

          <!-- Show login error if user is not authenticated -->
          <transition name="fade">
            <div
              v-if="loginError"
              class="mt-2 text-sm bg-red-100 text-red-800 border border-red-300 px-4 py-2 rounded-lg shadow"
            >
              Logg inn for å bruke quiz
            </div>
          </transition>
        </div>

        <!-- Quiz question display (one at a time) -->
        <div v-if="showQuiz" class="space-y-8">
          <div
            v-if="quizQuestions.length > 0"
            class="bg-[#F7FAFC] p-6 rounded-lg shadow"
          >
            <h3 class="font-semibold mb-4 text-[#022B3A]">
              Spørsmål {{ currentQuestionIndex + 1 }}:
              {{ quizQuestions[currentQuestionIndex].text }}
            </h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <button
                v-for="option in quizQuestions[currentQuestionIndex].options"
                :key="option.id"
                @click="selectAnswer(currentQuestionIndex, option.id)"
                :disabled="
                  quizQuestions[currentQuestionIndex].correctOptionId !== null
                "
                class="rounded-lg px-4 py-3 text-left transition border"
                :class="[
                  !quizQuestions[currentQuestionIndex].correctOptionId
                    ? 'bg-white border-gray-300 hover:bg-[#EFF6FB] hover:border-[#1F7A8C]'
                    : '',
                  quizQuestions[currentQuestionIndex].correctOptionId &&
                  option.id ===
                    quizQuestions[currentQuestionIndex].correctOptionId
                    ? 'bg-green-300'
                    : '',
                  quizQuestions[currentQuestionIndex].correctOptionId &&
                  option.id ===
                    quizQuestions[currentQuestionIndex].selectedOptionId &&
                  option.id !==
                    quizQuestions[currentQuestionIndex].correctOptionId
                    ? 'bg-red-300'
                    : '',
                  quizQuestions[currentQuestionIndex].correctOptionId
                    ? 'cursor-not-allowed'
                    : '',
                ]"
              >
                {{ option.text }}
              </button>
            </div>

            <!-- Question navigation -->
            <div class="flex justify-between items-center mt-4">
              <button
                @click="goToPreviousQuestion"
                :disabled="currentQuestionIndex === 0"
                class="px-4 py-2 rounded bg-[#BFDBF7] text-[#022B3A] text-sm font-medium hover:brightness-105 disabled:opacity-50"
              >
                Forrige
              </button>
              <button
                @click="goToNextQuestion"
                :disabled="currentQuestionIndex === quizQuestions.length - 1"
                class="px-4 py-2 rounded bg-[#BFDBF7] text-[#022B3A] text-sm font-medium hover:brightness-105 disabled:opacity-50"
              >
                Neste
              </button>
            </div>
          </div>

          <!-- Show result after all questions are answered -->
          <div
            v-if="isQuizCompleted"
            class="flex justify-between items-center mt-6"
          >
            <div class="text-[#022B3A] font-medium">
              Resultat:
              <span class="font-semibold"
                >{{ submittedScore?.score }} / {{ submittedScore?.total }}</span
              >
            </div>
            <button
              @click="closeQuiz"
              class="bg-[#BFDBF7] text-[#022B3A] px-6 py-2 rounded-lg font-semibold hover:brightness-105"
            >
              Lukk quiz
            </button>
          </div>
        </div>
      </div>

      <!-- Quiz history section -->
      <div
        class="md:col-span-3 bg-white shadow-md rounded-lg p-6 h-[475px] w-full flex flex-col"
      >
        <h2 class="text-xl font-bold mb-2 text-[#022B3A] text-center">
          Tidligere resultat
        </h2>
        <ul class="space-y-2 flex-1 overflow-y-auto">
          <li
            v-for="result in paginatedResults"
            :key="result.attemptId"
            @click="openPreviousQuiz(result.attemptId)"
            class="relative bg-[#F7FAFC] p-3 pl-4 rounded border flex justify-between items-center cursor-pointer hover:bg-[#e3eaf5]"
            :title="
              result.status === 'COMPLETED'
                ? 'Quiz fullført. Trykk for å se'
                : 'Quiz ikke fullført. Trykk for å fortsette'
            "
          >
            <div
              class="absolute left-0 top-0 h-full w-2 rounded-l"
              :class="
                result.status === 'COMPLETED' ? 'bg-green-500' : 'bg-yellow-400'
              "
            ></div>
            <span>{{ formatDate(result.date) }}</span>
            <span class="font-semibold"
              >{{ result.correctAnswers }} / {{ result.totalQuestions }}</span
            >
          </li>
        </ul>

        <!-- Pagination -->
        <div class="flex justify-between mt-auto pt-4">
          <button
            @click="goToPreviousPage"
            :disabled="!hasPreviousPage"
            class="px-5 py-2 rounded bg-[#BFDBF7] text-[#022B3A] text-sm font-medium hover:brightness-105 disabled:opacity-50"
          >
            Forrige
          </button>
          <button
            @click="goToNextPage"
            :disabled="!hasNextPage"
            class="px-5 py-2 rounded bg-[#BFDBF7] text-[#022B3A] text-sm font-medium hover:brightness-105 disabled:opacity-50"
          >
            Neste
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import quizApi from '@/services/quizApi'
import type { QuizAnswerRequest } from '@/services/quizApi'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userId = computed(() => userStore.currentUser?.id)

const selectedCount = ref(5)
const showQuiz = ref(false)
const quizId = ref<number | null>(null)
const quizQuestions = ref<any[]>([])
const submittedScore = ref<{ score: number; total: number } | null>(null)
const loginError = ref(false)
const previousResults = ref<any[]>([])
const currentPage = ref(1)
const itemsPerPage = 6
const currentQuestionIndex = ref(0)

const isQuizCompleted = computed(
  () =>
    quizQuestions.value.length > 0 &&
    quizQuestions.value.every((q) => q.correctOptionId !== null)
)

const paginatedResults = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return previousResults.value.slice(start, start + itemsPerPage)
})

const totalPages = computed(() =>
  Math.ceil(previousResults.value.length / itemsPerPage)
)
const hasNextPage = computed(() => currentPage.value < totalPages.value)
const hasPreviousPage = computed(() => currentPage.value > 1)

const goToNextPage = () => {
  if (hasNextPage.value) currentPage.value++
}
const goToPreviousPage = () => {
  if (hasPreviousPage.value) currentPage.value--
}

// Navigation between quiz questions
const goToNextQuestion = () => {
  if (currentQuestionIndex.value < quizQuestions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

const goToPreviousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// Start a new quiz
const handleStartQuiz = () => {
  if (!userId.value) {
    loginError.value = true
    setTimeout(() => (loginError.value = false), 1000)
    return
  }
  startQuiz()
}

const startQuiz = async () => {
  if (!userId.value) return
  try {
    const res = await quizApi.startQuiz({
      userId: userId.value,
      numberOfQuestions: selectedCount.value,
    })
    quizId.value = res.id
    quizQuestions.value = res.questions.map((q) => ({
      ...q,
      selectedOptionId: null,
      correctOptionId: null,
    }))
    submittedScore.value = null
    currentQuestionIndex.value = 0
    showQuiz.value = true
  } catch (err) {
    console.error('Failed to start quiz:', err)
  }
}

// Open a previous quiz attempt
const openPreviousQuiz = async (id: number) => {
  try {
    const res = await quizApi.getQuizAttempt(id)
    quizId.value = id
    quizQuestions.value = res.questions.map((q) => ({
      questionId: q.question.questionId,
      text: q.question.text,
      options: q.question.options,
      selectedOptionId: q.selectedOptionId,
      correctOptionId: q.correctOptionId,
    }))
    currentQuestionIndex.value = 0
    showQuiz.value = true
    window.scrollTo({ top: 0, behavior: 'smooth' })

    const result = await quizApi.getResult(id)
    submittedScore.value = {
      score: result.correctAnswers,
      total: result.totalQuestions,
    }
  } catch (e) {
    console.error('Kunne ikke laste tidligere quiz eller resultat:', e)
  }
}

// Submit answer for current question
const selectAnswer = async (questionIndex: number, optionId: number) => {
  if (!quizId.value || !userId.value || isQuizCompleted.value) return
  const question = quizQuestions.value[questionIndex]
  if (question.selectedOptionId !== null) return

  const answer: QuizAnswerRequest = {
    userId: userId.value,
    questionId: question.questionId,
    selectedAnswerId: optionId,
  }
  try {
    const response = await quizApi.submitAnswer(quizId.value, answer)
    question.selectedOptionId = optionId
    question.correctOptionId = response.correctAnswerId
    updateScore()
  } catch (err) {
    console.error('Answer submission failed:', err)
  }
}

// Recalculate score after each answer
const updateScore = () => {
  const correct = quizQuestions.value.filter(
    (q) => q.selectedOptionId === q.correctOptionId
  ).length
  submittedScore.value = { score: correct, total: quizQuestions.value.length }
}

// Finalize and save quiz score
const submitQuiz = async () => {
  if (!quizId.value || submittedScore.value) return
  try {
    const result = await quizApi.getResult(quizId.value)
    submittedScore.value = {
      score: result.correctAnswers,
      total: result.totalQuestions,
    }
  } catch (err) {
    console.error('Quiz submission failed:', err)
  }
}

// Close quiz and reset state
const closeQuiz = async () => {
  await submitQuiz()
  await loadPreviousResults()
  showQuiz.value = false
  quizQuestions.value = []
  quizId.value = null
}

// Fetch previous quiz attempts on load
const loadPreviousResults = async () => {
  if (!userId.value) return
  try {
    const history = await quizApi.getQuizHistory(userId.value)
    previousResults.value = history
  } catch (err) {
    console.error('Failed to load quiz history:', err)
  }
}

// Format date for display
const formatDate = (isoDate: string): string =>
  new Date(isoDate).toLocaleDateString('nb-NO', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })

onMounted(() => {
  loadPreviousResults()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
