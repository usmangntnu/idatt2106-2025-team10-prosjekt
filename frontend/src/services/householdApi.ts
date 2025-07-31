import apiClient from '@/services/apiClient.ts'
import type { Household, HouseholdCreate } from '@/types/types.ts'

export const joinHousehold = async (householdToken: string) => {
  try {
    const response = await apiClient.post(`/household/join/${householdToken}`)
    return response.data
  } catch (error) {
    console.error('Error joining household:', error)
    throw error
  }
}

export const createHousehold = async (data: HouseholdCreate) => {
  try {
    const response = await apiClient.post(`/household/create`, data)
    return response.data //the response is the household name
  } catch (error) {
    console.error('Error creating household:', error)
    throw error
  }
}

export const inviteUserToHousehold = async () => {
  try {
    const response = await apiClient.post(`/household/invite`)
    return response.data
  } catch (error) {
    console.error('Error inviting user to household:', error)
    throw error
  }
}

export const getMyHousehold = async (): Promise<Household> => {
  try {
    const response = await apiClient.get(`/household/my`)
    console.log(response.data)
    return response.data
  } catch (error) {
    console.error('Error fetching my household:', error)
    throw error
  }
}

export const leaveHousehold = async (): Promise<void> => {
  try {
    await apiClient.post(`/household/leave`)
  } catch (error) {
    console.error('Error leaving household:', error)
    throw error
  }
}
