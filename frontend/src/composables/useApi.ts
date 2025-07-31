import { ref } from 'vue'

export function useApi<T, E = unknown>(apiFunction: () => Promise<T>) {
  const data = ref<T | null>(null)
  const loading = ref(false)
  const error = ref<E | null>(null)

  const execute = async (opts?: { silent?: boolean }) => {
    if (!opts?.silent) loading.value = true

    try {
      data.value = await apiFunction()
    } catch (e: unknown) {
      error.value = e as E
    } finally {
      loading.value = false
    }
  }

  return { data, loading, error, execute }
}
