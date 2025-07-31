import apiClient from '@/services/apiClient.ts'

export interface QuizRequest {
  userId: number
  numberOfQuestions: number
}

export interface QuizOption {
  id: number
  text: string
}

export interface QuizQuestion {
  questionId: number
  text: string
  options: QuizOption[]
}

export interface QuizHistoryDto {
  correctAnswers: number
  totalQuestions: number
  date: string
  status: string
}

export interface QuizResponse {
  id: number
  questions: QuizQuestion[]
}

export interface QuizAnswerRequest {
  userId: number
  questionId: number
  selectedAnswerId: number
}

export interface QuizAnswerResponse {
  questionId: number
  selectedAnswerId: number
  correctAnswer: boolean
  correctAnswerId: number
}

export interface QuizResultResponse {
  totalQuestions: number
  correctAnswers: number
}

export interface QuizAttemptQuestionDto {
  question: QuizQuestion
  selectedOptionId: number | null
  isCorrect: boolean | null
  correctOptionId: number | null
}

export interface QuizAttemptResponse {
  attemptId: number
  attemptTime: string
  status: string
  questions: QuizAttemptQuestionDto[]
}

const quizApi = {
  async startQuiz(request: QuizRequest): Promise<QuizResponse> {
    const res = await apiClient.post<QuizResponse>('/quizzes', request)
    return res.data
  },

  async submitAnswer(
    quizId: number,
    answer: QuizAnswerRequest
  ): Promise<QuizAnswerResponse> {
    const res = await apiClient.post<QuizAnswerResponse>(
      `/quizzes/${quizId}/answers`,
      answer
    )
    return res.data
  },

  async cancelQuiz(quizId: number): Promise<void> {
    await apiClient.delete(`/quizzes/${quizId}`)
  },

  async getResult(quizId: number): Promise<QuizResultResponse> {
    const res = await apiClient.get<QuizResultResponse>(
      `/quizzes/${quizId}/result`
    )
    return res.data
  },

  async getQuizHistory(userId: number): Promise<QuizHistoryDto[]> {
    const res = await apiClient.get<QuizHistoryDto[]>(
      `/quizzes/history/${userId}`
    )
    return res.data
  },

  async getQuizAttempt(quizId: number): Promise<QuizAttemptResponse> {
    const res = await apiClient.get<QuizAttemptResponse>(`/quizzes/${quizId}`)
    return res.data
  },
}

export default quizApi
