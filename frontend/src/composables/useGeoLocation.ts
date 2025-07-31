import { ref } from 'vue'
import * as Sentry from '@sentry/vue'

/**
 * Custom composable to manage user's geolocation in a Vue application.
 *
 * Provides reactive references for latitude, longitude, and error messages.
 * Includes error handling with Sentry integration.
 *
 *
 * @returns {{
 *   latitude: Ref<number | null>
 *   longitude: Ref<number | null>,
 *   errorMessage: Ref<string | null>,
 *   getLocation: () => void
 * }}
 */
export function useGeoLocation() {
  const latitude = ref<number | null>(null)
  const longitude = ref<number | null>(null)
  const errorMessage = ref<string | null>(null)

  /**
   * Attempts to retrieve the user's current geographical location.
   *
   * On success, updates latitude and longitude.
   * On failure, sets an error message and logs the error to Sentry.
   */
  const getLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          latitude.value = position.coords.latitude
          longitude.value = position.coords.longitude
          errorMessage.value = null
        },
        (error) => {
          errorMessage.value = 'Unable to retrieve your location.'
          console.error('Error getting location:', error)
          Sentry.captureException(error, {
            extra: {
              message: 'Error getting location',
              error,
            },
          })
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0,
        }
      )
    } else {
      errorMessage.value = 'Geolocation is not supported by this browser.'
      console.error('Geolocation is not supported by this browser.')
      Sentry.captureException(
        new Error('Geolocation is not supported by this browser.'),
        {
          extra: {
            message: 'Geolocation is not supported by this browser.',
          },
        }
      )
    }
  }
  return { latitude, longitude, errorMessage, getLocation }
}
