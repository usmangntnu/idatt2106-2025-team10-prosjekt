import apiClient from './apiClient'
import * as Sentry from '@sentry/vue'
import type {
  EventResponse,
  EventTypeResponse,
  PositionResponse,
  PositionTypeResponse,
} from '@/types/types.ts'

/**
 * Fetches all shelters from the API, with the endpoint '/shelters'.
 *
 * @returns {Promise<any>} A promise that resolves to the list of shelters, or rejects with an error.
 */
export const getAllShelters = async (): Promise<PositionResponse> => {
  try {
    console.log('Fetching shelters from API...')
    const response = await apiClient.get('/positions/shelters')
    console.log('Shelters:', response.data)
    return response.data
  } catch (error) {
    console.error('Error fetching shelters:', error)
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Fetches shelters that are within a specified radius from a given latitude and longitude, intended to be used for
 * finding nearby shelters to a user's location.
 *
 * @param latitude the latitude of the user's location
 * @param longitude the longitude of the user's location
 * @param radius the radius in meters within which to search for shelters
 */
export const getNearbyShelters = async (
  latitude: number,
  longitude: number,
  radius: number
): Promise<PositionResponse[]> => {
  try {
    const response = await apiClient.post<PositionResponse[]>(
      '/positions/shelters-nearby',
      null,
      {
        params: { latitude, longitude, radius },
      }
    )
    return response.data
  } catch (error) {
    console.error('Error fetching nearby shelters:', error)
    throw error
  }
}

/**
 * Fetches all event-types from the API, with the endpoint '/event-types'.
 * Returns EventResponse[].
 *
 * @returns {Promise<EventTypeResponse[]>} A promise that resolves to the list of event-types, or rejects with an error.
 */
export const getAllEventTypes = async (): Promise<EventTypeResponse[]> => {
  try {
    const response = await apiClient.get('/event-types')
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Fetches all events from the API, with the endpoint '/events'.
 *
 * @returns {Promise<EventResponse[]>} A promise that resolves to the list of events, or rejects with an error.
 */
export const getAllEvents = async (): Promise<EventResponse[]> => {
  try {
    const response = await apiClient.get('/events')
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Fetches an event by its ID from the API, with the endpoint '/events/:id'.
 *
 * @param {number} id - The ID of the event to fetch.
 * @returns {Promise<EventResponse>} A promise that resolves to the event, or rejects with an error.
 */
export const getEventById = async (id: number): Promise<EventResponse> => {
  try {
    const response = await apiClient.get(`/events/${id}`)
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Method to fetch a position from the API with the endpoint '/positions/:id'.
 *
 * @param {number} id - The ID of the position to fetch.
 * @returns {Promise<PositionResponse>} A promise that resolves to the position, or rejects with an error.
 */
export const getPositionById = async (
  id: number
): Promise<PositionResponse> => {
  try {
    const response = await apiClient.get(`/positions/${id}`)
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Fetches all positions from the API, with the endpoint '/positions'.
 *
 * @returns {Promise<PositionResponse[]>} A promise that resolves to the list of positions, or rejects with an error.
 */
export const getAllPositions = async (): Promise<PositionResponse[]> => {
  try {
    const response = await apiClient.get('/positions')
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}

/**
 * Fetches all position types from the API, with the endpoint '/position-types'.
 *
 * @returns {Promise<any>} A promise that resolves to the list of position types, or rejects with an error.
 */
export const getAllPositionTypes = async (): Promise<
  PositionTypeResponse[]
> => {
  try {
    const response = await apiClient.get('/position-types')
    return response.data
  } catch (error) {
    Sentry.captureException(error)
    throw error
  }
}
