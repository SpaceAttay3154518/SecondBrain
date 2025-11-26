<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 p-8">
    <div class="max-w-4xl mx-auto">
      <div class="bg-white rounded-2xl p-8 shadow-lg border border-slate-100">
        <h1 class="text-3xl font-bold text-slate-800 mb-6">AI Assistant Demo</h1>
        
        <!-- User Info -->
        <div class="p-4 bg-slate-50 rounded-lg mb-6">
          <h2 class="text-lg font-semibold text-slate-700 mb-2">Current User</h2>
          <div v-if="user" class="text-sm text-slate-600">
            <p><strong>Email:</strong> {{ user.email }}</p>
            <p><strong>UID:</strong> {{ user.uid }}</p>
          </div>
          <div v-else class="text-sm text-red-600">
            Not authenticated. Please <NuxtLink to="/" class="underline">login</NuxtLink> first.
          </div>
        </div>

        <!-- Ask Question Section -->
        <div class="p-4 bg-slate-50 rounded-lg mb-6">
          <h2 class="text-lg font-semibold text-slate-700 mb-3">Ask a Question</h2>
          
          <textarea 
            v-model="question" 
            placeholder="Type your question here..."
            class="w-full px-3 py-2 border border-slate-300 rounded-lg mb-3 min-h-[100px]"
            :disabled="!user"
          ></textarea>
          
          <button 
            @click="handleAskQuestion" 
            :disabled="askLoading || !user || !question.trim()"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {{ askLoading ? 'Thinking...' : 'Ask Question' }}
          </button>

          <div v-if="answer" class="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
            <p class="text-sm font-semibold text-green-800 mb-2">✓ Answer:</p>
            <p class="text-sm text-green-700 whitespace-pre-wrap">{{ answer }}</p>
          </div>

          <div v-if="askError" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-sm font-semibold text-red-800 mb-2">✗ Error:</p>
            <p class="text-xs text-red-700">{{ askError }}</p>
          </div>
        </div>

        <!-- Upload Document Section -->
        <div class="p-4 bg-slate-50 rounded-lg mb-6">
          <h2 class="text-lg font-semibold text-slate-700 mb-3">Upload Document</h2>
          <p class="text-sm text-slate-600 mb-3">Upload a PDF or TXT file to add to the knowledge base</p>
          
          <input 
            type="file" 
            @change="handleFileSelect"
            accept=".pdf,.txt"
            class="mb-3"
            :disabled="!user"
          />
          
          <button 
            @click="handleUploadDocument" 
            :disabled="uploadLoading || !user || !selectedFile"
            class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
          >
            {{ uploadLoading ? 'Uploading...' : 'Upload Document' }}
          </button>

          <div v-if="uploadSuccess" class="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
            <p class="text-sm font-semibold text-green-800">✓ {{ uploadSuccess }}</p>
          </div>

          <div v-if="uploadError" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-sm font-semibold text-red-800 mb-2">✗ Upload Error:</p>
            <p class="text-xs text-red-700">{{ uploadError }}</p>
          </div>
        </div>

        <!-- Instructions -->
        <div class="p-4 bg-blue-50 rounded-lg border border-blue-200">
          <h2 class="text-lg font-semibold text-blue-800 mb-2">How This Works</h2>
          <div class="text-sm text-blue-900 space-y-2">
            <p><strong>1. Backend Setup (✅ Ready):</strong></p>
            <ul class="list-disc ml-6 text-xs">
              <li>Spring Boot backend running on port 8080</li>
              <li>Endpoints: /api/ai/ask and /api/ai/document/upload</li>
              <li>All requests authenticated with Firebase tokens</li>
            </ul>

            <p class="mt-3"><strong>2. AI Model Setup (⏳ Your Teammate):</strong></p>
            <ul class="list-disc ml-6 text-xs">
              <li>Convert AI Model to Spring Boot REST API on port 8081</li>
              <li>Expose POST /api/query endpoint (receives question, returns answer)</li>
              <li>Expose POST /api/document/upload endpoint (receives file, processes it)</li>
            </ul>

            <p class="mt-3"><strong>3. Request Flow:</strong></p>
            <p class="text-xs">Frontend → Backend (8080) → AI Model (8081) → Response back</p>
          </div>
        </div>

        <div class="mt-6 flex gap-3">
          <NuxtLink to="/" class="px-4 py-2 border border-slate-300 rounded-lg hover:bg-slate-50">
            Back to Home
          </NuxtLink>
          <NuxtLink to="/test-backend" class="px-4 py-2 bg-slate-700 text-white rounded-lg hover:bg-slate-800">
            Test Backend
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onAuthStateChanged } from 'firebase/auth'
import { projectAuth } from '../../firebase/config'
import { useAI } from '../../composables/useAI'

const { askQuestion, uploadDocument } = useAI()

const user = ref(null)
const question = ref('')
const answer = ref('')
const askError = ref('')
const askLoading = ref(false)

const selectedFile = ref(null)
const uploadSuccess = ref('')
const uploadError = ref('')
const uploadLoading = ref(false)

onMounted(() => {
  if (process.client) {
    onAuthStateChanged(projectAuth, (u) => {
      user.value = u
    })
  }
})

const handleAskQuestion = async () => {
  askError.value = ''
  answer.value = ''
  askLoading.value = true

  try {
    const response = await askQuestion(question.value)
    
    if (response.success) {
      answer.value = response.answer
    } else {
      askError.value = response.error || 'Failed to get answer'
    }
  } catch (err) {
    askError.value = err.message
  } finally {
    askLoading.value = false
  }
}

const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0]
  uploadSuccess.value = ''
  uploadError.value = ''
}

const handleUploadDocument = async () => {
  if (!selectedFile.value) return

  uploadError.value = ''
  uploadSuccess.value = ''
  uploadLoading.value = true

  try {
    const response = await uploadDocument(selectedFile.value)
    
    if (response.success) {
      uploadSuccess.value = response.message
      selectedFile.value = null
    } else {
      uploadError.value = response.message
    }
  } catch (err) {
    uploadError.value = err.message
  } finally {
    uploadLoading.value = false
  }
}
</script>
