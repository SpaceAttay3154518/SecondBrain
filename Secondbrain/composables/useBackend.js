import { projectAuth } from "../firebase/config"

const BACKEND_URL = 'http://localhost:8080' // Change this to your Java backend URL

export const useBackend = () => {
  const getAuthToken = async () => {
    const user = projectAuth.currentUser
    if (!user) {
      throw new Error('No authenticated user')
    }
    try {
      const token = await user.getIdToken()
      return token
    } catch (err) {
      console.error('Error getting auth token:', err)
      throw err
    }
  }

  const apiCall = async (endpoint, options = {}) => {
    try {
      const token = await getAuthToken()
      
      const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers
      }

      const response = await fetch(`${BACKEND_URL}${endpoint}`, {
        ...options,
        headers
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      // Check content type to determine how to parse response
      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('application/json')) {
        return await response.json()
      } else {
        return await response.text()
      }
    } catch (err) {
      console.error('API call error:', err)
      throw err
    }
  }

  return {
    getAuthToken,
    apiCall
  }
}
