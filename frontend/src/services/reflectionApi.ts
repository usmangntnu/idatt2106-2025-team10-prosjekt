import type { ReflectionNote, ReflectionNoteCreate } from '@/types/types.ts'
import apiClient from '@/services/apiClient.ts'

/**
 * Create a new reflection note for the current user
 *
 * @param newNote - the reflection note
 */
export const addReflectionNote = async (
  newNote: ReflectionNoteCreate
): Promise<void> => {
  try {
    await apiClient.post<void>(`reflection/add`, newNote)
  } catch (error) {
    console.error('Failed to create reflection note:', error)
    throw error
  }
}

/**
 * Delete a reflection note for the current user
 *
 */
export const deleteReflectionNote = async (noteId: number): Promise<void> => {
  try {
    await apiClient.delete<void>(`reflection/${noteId}`)
  } catch (error) {
    console.error('Failed to delete reflection note:', error)
    throw error
  }
}

/**
 * Retrieves a list of public reflection notes
 *
 * @return Promise<ReflectionNote[]> - a list of public reflection notes
 */
export const getPublicReflectionNotes = async (): Promise<ReflectionNote[]> => {
  try {
    const response = await apiClient.get<ReflectionNote[]>(`reflection/public`)
    return response.data
  } catch (error) {
    console.error('Failed to get public reflection notes:', error)
    throw error
  }
}

/**
 * Retrieves the current user's reflection notes - independent of visibility status
 *
 * @return Promise<ReflectionNote[]> - a list of the user's reflection notes
 */
export const getMyReflectionNotes = async (): Promise<ReflectionNote[]> => {
  try {
    const response = await apiClient.get<ReflectionNote[]>(`reflection/my`)
    return response.data
  } catch (error) {
    console.error('Failed to get my reflection notes:', error)
    throw error
  }
}

/**
 * Retrieves all household reflection notes
 *
 * @return Promise<ReflectionNote[]> - a list of the household's reflection notes
 */
export const getHouseholdReflectionNotes = async (): Promise<
  ReflectionNote[]
> => {
  try {
    const response =
      await apiClient.get<ReflectionNote[]>(`reflection/household`)
    return response.data
  } catch (error) {
    console.error('Failed to get household reflection notes:', error)
    throw error
  }
}
