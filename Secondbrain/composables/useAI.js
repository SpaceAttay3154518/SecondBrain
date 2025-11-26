import { useBackend } from './useBackend'

export const useAI = () => {
  const { apiCall } = useBackend()

  /**
   * Ask a question to the AI
   * @param {string} question - The question to ask
   * @returns {Promise<{answer: string, success: boolean, error?: string}>}
   */
  const askQuestion = async (question) => {
    try {
      const response = await apiCall('/api/ai/ask', {
        method: 'POST',
        body: JSON.stringify({ question })
      })
      return response
    } catch (err) {
      console.error('Error asking question:', err)
      return {
        answer: null,
        success: false,
        error: err.message
      }
    }
  }

  /**
   * Upload a document (PDF or TXT) for AI processing
   * @param {File} file - The file to upload
   * @returns {Promise<{success: boolean, message: string, documentId?: string}>}
   */
  const uploadDocument = async (file) => {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const { getAuthToken } = useBackend()
      const token = await getAuthToken()

      const response = await fetch('http://localhost:8080/api/ai/document/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      })

      if (!response.ok) {
        throw new Error(`Upload failed: ${response.statusText}`)
      }

      return await response.json()
    } catch (err) {
      console.error('Error uploading document:', err)
      return {
        success: false,
        message: err.message
      }
    }
  }

  /**
   * Get list of user's uploaded documents
   * @returns {Promise<Array>}
   */
  const getUserDocuments = async () => {
    try {
      const response = await apiCall('/api/ai/documents')
      return JSON.parse(response)
    } catch (err) {
      console.error('Error getting documents:', err)
      return []
    }
  }

  /**
   * Delete a document
   * @param {string} documentId - The ID of the document to delete
   * @returns {Promise<{success: boolean, message: string}>}
   */
  const deleteDocument = async (documentId) => {
    try {
      const response = await apiCall(`/api/ai/document/${documentId}`, {
        method: 'DELETE'
      })
      return JSON.parse(response)
    } catch (err) {
      console.error('Error deleting document:', err)
      return {
        success: false,
        message: err.message
      }
    }
  }

  return {
    askQuestion,
    uploadDocument,
    getUserDocuments,
    deleteDocument
  }
}
