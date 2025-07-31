import apiClient from './apiClient'

import type {
  PositionRequest,
  PositionResponse,
  EventRequest,
  EventResponse,
} from '@/types/types.ts'

/**
 * Sends a new position to the backend and returns the saved PositionResponse.
 *
 * @param data PositionRequest object with required position data
 * @returns Promise resolving to the created PositionResponse
 */
export const createPosition = async (
  data: PositionRequest
): Promise<PositionResponse> => {
  try {
    console.log('The typeId is:', data.typeId)
    const response = await apiClient.post<PositionResponse>('/positions', data)
    return response.data
  } catch (error) {
    console.error('Error creating position:', error)
    throw error
  }
}

/**
 * Updates a position with an optional amount (but atleast one) of parameters.
 *
 * @param positionId The ID of the position to be updated
 * @param data PositionRequest object with the updated position data
 * @returns Promise resolving to the updated PositionResponse
 */
export const updatePosition = async (
  positionId: number,
  data: PositionRequest
): Promise<PositionResponse> => {
  try {
    console.log('Reaching endpoint to update position')
    const response = await apiClient.put<PositionResponse>(
      `/positions/${positionId}`,
      data
    )
    return response.data
  } catch (error) {
    console.error('Error updating position:', error)
    throw error
  }
}

/**
 * Sends a new event to the backend and returns the saved EventResponse.
 *
 * @param data EventRequest object with required event data
 * @returns Promise resolving to the created EventResponse
 */
export const createEvent = async (
  data: EventRequest
): Promise<EventResponse> => {
  try {
    const response = await apiClient.post<EventResponse>('/events', data)
    return response.data
  } catch (error) {
    console.error('Error creating event:', error)
    throw error
  }
}

/**
 * Method to delete a position by its ID.
 * It takes the positionId as a parameter and sends a DELETE request to the backend.
 *
 * @param positionId The ID of the position to be deleted
 * @returns Promise resolving to void
 */
export const deletePositionById = async (positionId: number): Promise<void> => {
  try {
    await apiClient.delete(`/positions/${positionId}`)
  } catch (error) {
    console.error('Error deleting position:', error)
    throw error
  }
}

/**
 * Method to update an event. It takes an eventId and an EventRequest object as parameters, sends it to the backend
 * and returns the updated EventResponse.
 *
 * @param eventId The ID of the event to be updated
 * @param data EventRequest object with the updated event data
 * @returns Promise resolving to the updated EventResponse
 */
export const updateEvent = async (
  eventId: number,
  data: EventRequest
): Promise<EventResponse> => {
  try {
    const response = await apiClient.put<EventResponse>(
      `/events/${eventId}`,
      data
    )
    return response.data
  } catch (error) {
    console.error('Error updating event:', error)
    throw error
  }
}

/**
 * Method to delete an event by its ID. It takes the eventId as a parameter and sends a DELETE request to the backend.
 * @param eventId The ID of the event to be deleted
 * @returns Promise resolving to void
 */
export const deleteEventById = async (eventId: number): Promise<void> => {
  try {
    await apiClient.delete(`/events/${eventId}`)
  } catch (error) {
    console.error('Error deleting event:', error)
    throw error
  }
}
