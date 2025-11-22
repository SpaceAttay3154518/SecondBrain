<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 p-8">
    <div class="max-w-4xl mx-auto">
      <div class="bg-white rounded-2xl p-8 shadow-lg border border-slate-100">
        <h1 class="text-3xl font-bold text-slate-800 mb-6">Backend Connection Test</h1>
        
        <div class="space-y-6">
          <!-- User Info -->
          <div class="p-4 bg-slate-50 rounded-lg">
            <h2 class="text-lg font-semibold text-slate-700 mb-2">Current User</h2>
            <div v-if="user" class="text-sm text-slate-600">
              <p><strong>Email:</strong> {{ user.email }}</p>
              <p><strong>UID:</strong> {{ user.uid }}</p>
              <p><strong>Display Name:</strong> {{ user.displayName || 'Not set' }}</p>
            </div>
            <div v-else class="text-sm text-red-600">
              Not authenticated. Please <NuxtLink to="/login" class="underline">login</NuxtLink> first.
            </div>
          </div>

          <!-- Token Display -->
          <div class="p-4 bg-slate-50 rounded-lg">
            <h2 class="text-lg font-semibold text-slate-700 mb-2">Firebase ID Token</h2>
            <button @click="fetchToken" class="px-4 py-2 bg-indigo-600 text-white rounded-lg mb-3 hover:bg-indigo-700">
              Get Current Token
            </button>
            <div v-if="token" class="mt-3">
              <p class="text-xs text-slate-500 mb-1">Token (first 100 chars):</p>
              <code class="block p-3 bg-white rounded border border-slate-200 text-xs break-all">{{ token.substring(0, 100) }}...</code>
              <p class="text-xs text-green-600 mt-2">✓ Token generated successfully</p>
            </div>
            <div v-if="tokenError" class="text-sm text-red-600 mt-2">
              {{ tokenError }}
            </div>
          </div>

          <!-- Test /api/hello Endpoint -->
          <div class="p-4 bg-slate-50 rounded-lg">
            <h2 class="text-lg font-semibold text-slate-700 mb-2">Test /api/hello Endpoint</h2>
            <p class="text-sm text-slate-600 mb-3">
              This will call <code class="px-1 bg-white border rounded">GET /api/hello</code> using the apiCall composable
            </p>

            <button @click="testHello" :disabled="helloLoading || !user" class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50">
              {{ helloLoading ? 'Calling...' : 'Test /api/hello' }}
            </button>

            <div v-if="helloResponse" class="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
              <p class="text-sm font-semibold text-green-800 mb-2">✓ Success! Response:</p>
              <pre class="text-sm text-green-700">{{ helloResponse }}</pre>
            </div>

            <div v-if="helloError" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-sm font-semibold text-red-800 mb-2">✗ Error:</p>
              <p class="text-xs text-red-700">{{ helloError }}</p>
              <div class="mt-2 text-xs text-slate-600">
                <p><strong>Troubleshooting:</strong></p>
                <ul class="list-disc ml-4 mt-1">
                  <li>Make sure you're logged in (check user info above)</li>
                  <li>Backend must be running on http://localhost:8080</li>
                  <li>Check backend console for error messages</li>
                  <li>Firebase Admin SDK must be initialized</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Instructions -->
          <div class="p-4 bg-blue-50 rounded-lg border border-blue-200">
            <h2 class="text-lg font-semibold text-blue-800 mb-2">Java Backend Setup Instructions</h2>
            <div class="text-sm text-blue-900 space-y-2">
              <p><strong>1. Add Firebase Admin SDK to your pom.xml:</strong></p>
              <pre class="bg-white p-2 rounded text-xs overflow-auto">&lt;dependency&gt;
  &lt;groupId&gt;com.google.firebase&lt;/groupId&gt;
  &lt;artifactId&gt;firebase-admin&lt;/artifactId&gt;
  &lt;version&gt;9.2.0&lt;/version&gt;
&lt;/dependency&gt;</pre>

              <p class="mt-3"><strong>2. Create a test endpoint that verifies the token:</strong></p>
              <p class="text-xs">The token will be sent in the Authorization header as: <code class="bg-white px-1 rounded">Bearer {token}</code></p>
              
              <p class="mt-3"><strong>3. Enable CORS to allow requests from:</strong></p>
              <code class="bg-white px-2 py-1 rounded">{{ currentOrigin }}</code>
            </div>
          </div>

          <div class="flex gap-3">
            <NuxtLink to="/" class="px-4 py-2 border border-slate-300 rounded-lg hover:bg-slate-50">
              Back to Home
            </NuxtLink>
            <NuxtLink v-if="user" :to="`/${user.uid}`" class="px-4 py-2 bg-slate-700 text-white rounded-lg hover:bg-slate-800">
              Go to Dashboard
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onAuthStateChanged } from 'firebase/auth'
import { projectAuth } from '../../firebase/config'
import { useBackend } from '../../composables/useBackend'

const { getAuthToken, apiCall } = useBackend()

const user = ref(null)
const token = ref('')
const tokenError = ref('')
const helloLoading = ref(false)
const helloResponse = ref('')
const helloError = ref('')
const currentOrigin = ref('')

onMounted(() => {
  currentOrigin.value = window.location.origin
  
  if (process.client) {
    onAuthStateChanged(projectAuth, (u) => {
      user.value = u
      if (u) {
        fetchToken()
      }
    })
  }
})

const fetchToken = async () => {
  tokenError.value = ''
  token.value = ''
  try {
    const idToken = await getAuthToken()
    token.value = idToken
  } catch (err) {
    tokenError.value = err.message
  }
}

const testHello = async () => {
  helloError.value = ''
  helloResponse.value = ''
  helloLoading.value = true

  try {
    const result = await apiCall('/api/hello')
    helloResponse.value = result
  } catch (err) {
    helloError.value = err.message || 'Failed to call /api/hello'
  } finally {
    helloLoading.value = false
  }
}
</script>
