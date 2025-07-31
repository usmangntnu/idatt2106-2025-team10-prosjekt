import type {
  Category,
  PreparednessSummary,
  StorageItem,
  Notification,
} from '@/types/types.ts'
import apiClient from '@/services/apiClient.ts'

/**
 * Fetches all the item categories
 *
 * @error logs the error message and returns an empty list
 * @returns Promise<Category[]> - list of categories
 */
export const getAllItemCategories = async (): Promise<Category[]> => {
  try {
    const response = await apiClient.get<Category[]>(`/itemcategories`)
    return response.data
  } catch (error) {
    console.error(`Failed to fetch item categories ${error}`)
    throw error
  }
}

/**
 * Fetches all the storage items responses for a specific household and category
 *
 * @param householdId
 * @param categoryId
 * @return Promise<StorageItem[]> - all storage items for specific household and category
 */
export const getAllStorageItemResponses = async (
  householdId: number,
  categoryId: number
): Promise<StorageItem[]> => {
  try {
    const response = await apiClient.get<StorageItem[]>(
      `/storageitems/${householdId}/${categoryId}`
    )
    return response.data
  } catch (error) {
    console.error(`Failed to fetch storage items ${error}`)
    throw error
  }
}

/**
 * Fetches all storage items for a household
 *
 * @param householdId the household ID
 * @return Promise<StorageItem[]> - dto's for the householdId
 */
export const getAllStorageItemsForHousehold = async (
  householdId: number
): Promise<StorageItem[]> => {
  try {
    const response = await apiClient.get<StorageItem[]>(
      `/storageitems/${householdId}`
    )
    return response.data
  } catch (error) {
    console.error(`Failed to fetch storage items ${error}`)
    throw error
  }
}

/**
 * Updates the stock quantity of a storage item.
 * The backend will automatically calculate and update the expiration date.
 *
 * @param itemId The ID of the storage item to update
 * @param newStock The new stock quantity value
 * @error logs the error message and returns undefined
 * @returns the updated StorageItem if successful, undefined otherwise
 */
export const updateStorageItem = async (
  itemId: number,
  newStock: number
): Promise<StorageItem> => {
  try {
    // Create the update payload according to what the backend expects
    const updatePayload = {
      id: itemId,
      newStock: newStock,
    }

    const response = await apiClient.put<StorageItem>(
      `/storageitems/update-stock`,
      updatePayload
    )

    return response.data
  } catch (error) {
    console.error(`Failed to update storage item ${itemId} stock: ${error}`)
    throw error
  }
}

/**
 * Fetches the preparedness summary for a household
 *
 * @param householdId - the household Id
 * @returns Promise<PreparednessSummary> - the preparedness summary
 */
export const getHouseholdPreparednessSummary = async (
  householdId: number
): Promise<PreparednessSummary> => {
  try {
    const response = await apiClient.get<PreparednessSummary>(
      `preparedness/${householdId}`
    )
    return response.data
  } catch (error) {
    console.error(
      `Failed to fetch preparedness summary for household ${householdId}: ${error}`
    )
    throw error
  }
}

/**
 * Fetches all notifications for a specific household
 *
 * @param householdId - the ID of the household
 * @returns Promise<Notification[]> - list of notifications for the household
 */
export const getNotificationsForHousehold = async (
  householdId: number
): Promise<Notification[]> => {
  try {
    const response = await apiClient.get<Notification[]>(
      `/notifications/household/${householdId}`
    )
    return response.data
  } catch (error) {
    console.error(
      `Failed to fetch notifications for household ${householdId}: ${error}`
    )
    throw error
  }
}

/**
 * Creates a new notification
 *
 * @param notification - the notification data
 * @returns Promise<Notification> - the created notification
 */
export const createNotification = async (
  notification: Omit<Notification, 'id'>
): Promise<Notification> => {
  try {
    const response = await apiClient.post<Notification>(
      '/notifications',
      notification
    )
    return response.data
  } catch (error) {
    console.error('Failed to create notification:', error)
    throw error
  }
}

/**
 * Deletes a notification by ID
 *
 * @param notificationId - the ID of the notification to delete
 * @returns Promise<void>
 */
export const deleteNotification = async (
  notificationId: number
): Promise<void> => {
  try {
    await apiClient.delete(`/notifications/${notificationId}`)
  } catch (error) {
    console.error(`Failed to delete notification ${notificationId}:`, error)
    throw error
  }
}
