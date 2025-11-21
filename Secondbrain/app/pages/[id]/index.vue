<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 relative overflow-hidden">
    <div class="absolute top-0 right-0 w-96 h-96 bg-purple-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30"></div>
    <div class="absolute bottom-0 left-0 w-96 h-96 bg-indigo-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30"></div>

    <div class="relative z-10 px-4 py-10 w-full">
      <header class="flex items-center justify-between mb-8">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0"></path>
            </svg>
          </div>
          <div>
            <h1 class="text-2xl font-bold text-slate-800">Your Dashboard</h1>
            <p class="text-sm text-slate-500">Welcome back — organize, upload, and ask your documents</p>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <div class="text-sm text-slate-600 text-right">
            <div v-if="user">
              <div class="font-semibold text-slate-800">{{ user.displayName || user.email }}</div>
              <div class="text-xs">{{ user.email }}</div>
            </div>
          </div>
          <button @click="goToProfile" title="Profile" class="w-10 h-10 bg-white rounded-xl border border-slate-200 shadow-sm hover:bg-slate-50 flex items-center justify-center">
            <svg class="w-5 h-5 text-slate-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14v7"></path>
            </svg>
          </button>
          <button @click="handleLogout" class="px-4 py-2 bg-white rounded-xl border border-slate-200 shadow-sm hover:bg-slate-50">Logout</button>
        </div>
      </header>

      <div class="flex gap-8">
        <!-- Left Sidebar -->
        <aside class="w-64 space-y-6">
          <div class="bg-white rounded-2xl p-6 shadow-lg border border-slate-100">
            <h3 class="text-sm font-semibold text-slate-700 mb-2">Recent Activity</h3>
            <ul class="text-sm text-slate-600 list-disc pl-4 space-y-2">
              <li v-for="(ev, idx) in recent" :key="idx">{{ ev }}</li>
              <li v-if="!recent.length">No recent activity</li>
            </ul>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Left: Upload / Ask card -->
          <section class="lg:col-span-2 space-y-6">
            <div class="bg-white rounded-2xl p-6 shadow-lg border border-slate-100">
              <h2 class="text-lg font-semibold text-slate-800 mb-3">Upload Documents</h2>
              <p class="text-sm text-slate-500 mb-4">Add files to your workspace so the AI can analyze them.</p>

              <div class="flex items-center gap-4">
                <input ref="fileInput" type="file" multiple class="hidden" @change="handleFiles" />
                <button @click="triggerFile" class="inline-flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl shadow">Select files</button>
                <div class="text-sm text-slate-600">or drag & drop files here (coming soon)</div>
              </div>

              <div v-if="files.length" class="mt-4">
                <div class="text-sm text-slate-600 mb-2">Files to upload:</div>
                <ul class="space-y-2">
                  <li v-for="(f, i) in files" :key="i" class="flex items-center justify-between bg-slate-50 border border-slate-100 rounded-md p-3">
                    <div class="text-sm text-slate-800">{{ f.name }}</div>
                    <div class="text-xs text-slate-500">{{ formatSize(f.size) }}</div>
                  </li>
                </ul>

                <div class="mt-4">
                  <button @click="uploadFiles" class="px-4 py-2 bg-indigo-600 text-white rounded-lg shadow">Upload</button>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-2xl p-6 shadow-lg border border-slate-100">
              <h2 class="text-lg font-semibold text-slate-800 mb-3">Ask the AI</h2>
              <p class="text-sm text-slate-500 mb-4">Ask questions about your uploaded documents or general knowledge.</p>

              <textarea v-model="question" placeholder="Ask a question..." rows="4" class="w-full rounded-md border border-slate-200 p-3 focus:ring-2 focus:ring-indigo-200"></textarea>
              <div class="flex items-center gap-3 mt-3">
                <button @click="askAI" :disabled="!question.trim()" class="px-4 py-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl shadow">Ask</button>
                <div v-if="loading" class="text-sm text-slate-500">Thinking...</div>
              </div>

              <div v-if="answer" class="mt-4 bg-slate-50 p-4 rounded-md border border-slate-100">
                <div class="text-sm text-slate-700" v-html="answer"></div>
              </div>
            </div>
          </section>

          <!-- Right: Summary -->
          <aside class="space-y-6">
            <div class="bg-white rounded-2xl p-6 shadow-lg border border-slate-100">
              <h3 class="text-sm font-semibold text-slate-700 mb-2">Workspace Summary</h3>
              <div class="text-sm text-slate-600">You have <strong>{{ files.length }}</strong> files staged for upload.</div>
            </div>
          </aside>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { onAuthStateChanged, signOut } from 'firebase/auth'
import { projectAuth } from '../../../firebase/config'

const route = useRoute()
const router = useRouter()

const user = ref(null)
const files = ref([])
const question = ref('')
const answer = ref('')
const loading = ref(false)
const recent = ref([])

// Ensure only authenticated user can view their dashboard
onMounted(() => {
  if (process.client) {
    onAuthStateChanged(projectAuth, (u) => {
      if (!u) {
        router.push('/login')
        return
      }
      user.value = u
      // If route uid doesn't match authenticated user, redirect to their own dashboard
      const routeId = route.params.id
      if (routeId && routeId !== u.uid) {
        router.push(`/${u.uid}`)
      }
    })
  }
})

const triggerFile = () => {
  const input = fileInput.value
  if (input) input.click()
}

const fileInput = ref(null)

const handleFiles = (e) => {
  const selected = Array.from(e.target.files || [])
  // Keep minimal metadata for now
  files.value = selected
}

const uploadFiles = async () => {
  if (!files.value.length) return
  // Placeholder: implement actual storage upload (Firebase Storage) later
  recent.value.unshift(`Uploaded ${files.value.length} file(s)`)
  files.value = []
  answer.value = ''
}

const askAI = async () => {
  if (!question.value.trim()) return
  loading.value = true
  answer.value = ''
  // Placeholder: call your AI endpoint here. We'll mock a response.
  await new Promise((r) => setTimeout(r, 800))
  answer.value = `<strong>Sample answer:</strong> This is a mocked response for "${escapeHtml(question.value)}".`
  recent.value.unshift(`Asked: ${question.value}`)
  question.value = ''
  loading.value = false
}

const handleLogout = async () => {
  try {
    await signOut(projectAuth)
    user.value = null
    router.push('/')
  } catch (err) {
    console.error('Logout error', err)
  }
}

const goToProfile = () => {
  router.push(`/${route.params.id}/profil`)
}

function formatSize(n) {
  if (n < 1024) return n + ' B'
  if (n < 1024 * 1024) return (n / 1024).toFixed(1) + ' KB'
  return (n / (1024 * 1024)).toFixed(1) + ' MB'
}

function escapeHtml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}
</script>
