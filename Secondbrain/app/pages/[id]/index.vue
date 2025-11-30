<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden">
    <!-- Animated background elements -->
    <div class="absolute top-0 right-0 w-96 h-96 bg-indigo-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
    <div class="absolute bottom-0 left-0 w-96 h-96 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse" style="animation-delay: 1s"></div>
    <div class="absolute top-1/2 left-1/2 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-10 animate-pulse" style="animation-delay: 2s"></div>

    <div class="relative z-10 px-4 py-10 w-full max-w-7xl mx-auto">
      <header class="flex items-center justify-between mb-8">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 bg-gradient-to-br from-indigo-400 to-purple-500 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-500/50">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
            </svg>
          </div>
          <div>
            <h1 class="text-2xl font-bold text-white">AI Knowledge Assistant</h1>
            <p class="text-sm text-indigo-300">Your intelligent document companion</p>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <div class="text-sm text-slate-300 text-right">
            <div v-if="user">
              <div class="font-semibold text-white">{{ user.displayName || user.email }}</div>
              <div class="text-xs text-indigo-300">{{ user.email }}</div>
            </div>
          </div>
          <button @click="goToProfile" title="Profile" class="w-10 h-10 bg-white/10 backdrop-blur-sm rounded-xl border border-white/20 shadow-sm hover:bg-white/20 flex items-center justify-center transition-all">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </button>
          <button @click="handleLogout" class="px-4 py-2 bg-white/10 backdrop-blur-sm rounded-xl border border-white/20 shadow-sm hover:bg-white/20 text-white transition-all">Logout</button>
        </div>
      </header>

      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar -->
        <aside class="lg:col-span-1 space-y-4">
          <!-- Recent Activity -->
          <div class="bg-white/10 backdrop-blur-md rounded-2xl p-5 shadow-xl border border-white/20">
            <div class="flex items-center gap-2 mb-3">
              <svg class="w-5 h-5 text-indigo-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              <h3 class="text-sm font-semibold text-white">Recent Activity</h3>
            </div>
            <ul class="text-xs text-slate-300 space-y-2 max-h-48 overflow-y-auto">
              <li v-for="(ev, idx) in recent.slice(0, 10)" :key="idx" class="py-1 border-b border-white/10 last:border-0">
                {{ ev }}
              </li>
              <li v-if="!recent.length" class="text-slate-400 italic">No recent activity</li>
            </ul>
          </div>

          <!-- Uploaded Documents -->
          <div class="bg-white/10 backdrop-blur-md rounded-2xl p-5 shadow-xl border border-white/20">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-indigo-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <h3 class="text-sm font-semibold text-white">Your Documents</h3>
              </div>
              <span class="text-xs bg-indigo-500/50 text-white px-2 py-1 rounded-full">{{ uploadedDocuments.length }}</span>
            </div>
            
            <div v-if="loadingDocs" class="text-xs text-slate-400 animate-pulse">Loading documents...</div>
            
            <div v-else-if="uploadedDocuments.length === 0" class="text-xs text-slate-400 italic">
              No documents uploaded yet
            </div>
            
            <ul v-else class="space-y-2 max-h-64 overflow-y-auto">
              <li v-for="doc in uploadedDocuments" :key="doc.id || doc" 
                  class="group flex items-center justify-between p-2 bg-white/5 hover:bg-white/10 rounded-lg border border-white/10 transition-all">
                <div class="flex items-center gap-2 flex-1 min-w-0">
                  <svg class="w-4 h-4 text-indigo-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"></path>
                  </svg>
                  <span class="text-xs text-slate-200 truncate">{{ doc.filename || doc.name || doc || 'Document' }}</span>
                </div>
                <button 
                  @click="deleteDoc(doc.id || doc)" 
                  class="opacity-0 group-hover:opacity-100 ml-2 p-1 hover:bg-red-500/20 rounded transition-all flex-shrink-0"
                  title="Delete document">
                  <svg class="w-4 h-4 text-red-400 hover:text-red-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                  </svg>
                </button>
              </li>
            </ul>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="lg:col-span-3 space-y-6">
          <!-- Chat Interface -->
          <div class="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl border border-white/20 overflow-hidden">
            <!-- Chat Header -->
            <div class="bg-gradient-to-r from-indigo-600/50 to-purple-600/50 p-4 border-b border-white/10">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
                  <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
                  </svg>
                </div>
                <div>
                  <h2 class="text-lg font-semibold text-white">AI Assistant</h2>
                  <p class="text-xs text-indigo-200">Ask me anything about your documents</p>
                </div>
              </div>
            </div>

            <!-- Conversation Area -->
            <div class="p-6 space-y-4 min-h-[400px] max-h-[500px] overflow-y-auto">
              <!-- Conversation History -->
              <div v-if="conversationHistory.length > 0" class="space-y-4">
                <div v-for="(msg, idx) in conversationHistory" :key="idx">
                  <!-- User Message -->
                  <div class="flex justify-end mb-3">
                    <div class="max-w-[80%] bg-indigo-600 rounded-2xl rounded-tr-sm px-4 py-3 shadow-lg">
                      <p class="text-sm text-white">{{ msg.question }}</p>
                    </div>
                  </div>
                  
                  <!-- AI Response -->
                  <div class="flex justify-start">
                    <div class="max-w-[85%] bg-white/10 backdrop-blur-sm rounded-2xl rounded-tl-sm px-4 py-3 shadow-lg border border-white/20">
                      <div class="flex items-start gap-2">
                        <svg class="w-5 h-5 text-indigo-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                        </svg>
                        <div class="text-sm text-slate-100 leading-relaxed prose prose-sm prose-invert max-w-none" v-html="formatAIResponse(msg.answer)"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Current Answer (if loading or just answered) -->
              <div v-if="currentAnswer && !conversationHistory.find(c => c.answer === currentAnswer)" class="flex justify-start">
                <div class="max-w-[85%] bg-white/10 backdrop-blur-sm rounded-2xl rounded-tl-sm px-4 py-3 shadow-lg border border-white/20">
                  <div class="flex items-start gap-2">
                    <svg class="w-5 h-5 text-indigo-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                    </svg>
                    <div class="text-sm text-slate-100 leading-relaxed prose prose-sm prose-invert max-w-none" v-html="currentAnswer"></div>
                  </div>
                </div>
              </div>

              <!-- Thinking Indicator -->
              <div v-if="loading" class="flex justify-start">
                <div class="bg-white/10 backdrop-blur-sm rounded-2xl rounded-tl-sm px-4 py-3 shadow-lg border border-white/20">
                  <div class="flex items-center gap-2">
                    <div class="flex gap-1">
                      <div class="w-2 h-2 bg-indigo-400 rounded-full animate-bounce"></div>
                      <div class="w-2 h-2 bg-indigo-400 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                      <div class="w-2 h-2 bg-indigo-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                    </div>
                    <span class="text-xs text-indigo-300">AI is thinking...</span>
                  </div>
                </div>
              </div>

              <!-- Empty State -->
              <div v-if="conversationHistory.length === 0 && !currentAnswer && !loading" class="flex flex-col items-center justify-center py-12 text-center">
                <div class="w-16 h-16 bg-indigo-500/20 rounded-full flex items-center justify-center mb-4">
                  <svg class="w-8 h-8 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                  </svg>
                </div>
                <h3 class="text-lg font-semibold text-white mb-2">Start a conversation</h3>
                <p class="text-sm text-slate-300 max-w-md">Ask questions about your documents or request information. The AI will provide intelligent, context-aware responses.</p>
              </div>
            </div>

            <!-- Input Area -->
            <div class="border-t border-white/10 p-4 bg-white/5">
              <div class="flex gap-3">
                <textarea 
                  v-model="question" 
                  @keydown.enter.prevent="handleEnterKey"
                  placeholder="Ask a question about your documents..." 
                  rows="1" 
                  class="flex-1 bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 rounded-xl border border-white/20 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent resize-none"
                ></textarea>
                <button 
                  @click="askAI" 
                  :disabled="!question.trim() || loading" 
                  class="px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 disabled:opacity-50 disabled:cursor-not-allowed text-white rounded-xl shadow-lg shadow-indigo-500/50 transition-all flex items-center gap-2 font-medium">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 9l3 3m0 0l-3 3m3-3H8m13 0a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  Send
                </button>
              </div>
            </div>
          </div>

          <!-- Upload Section -->
          <div class="bg-white/10 backdrop-blur-md rounded-2xl p-6 shadow-xl border border-white/20">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-10 h-10 bg-indigo-500/20 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                </svg>
              </div>
              <div>
                <h2 class="text-lg font-semibold text-white">Upload Documents</h2>
                <p class="text-sm text-indigo-300">Add files for AI analysis (PDF, TXT, MD)</p>
              </div>
            </div>

            <div class="space-y-4">
              <div class="flex items-center gap-4">
                <input ref="fileInput" type="file" multiple accept=".pdf,.txt,.md" class="hidden" @change="handleFiles" />
                <button 
                  @click="triggerFile" 
                  class="inline-flex items-center gap-2 px-5 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white rounded-xl shadow-lg transition-all font-medium">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                  </svg>
                  Select Files
                </button>
                <div class="text-sm text-slate-300">Supports PDF, TXT, and Markdown files</div>
              </div>

              <div v-if="files.length" class="space-y-3">
                <div class="text-sm text-indigo-300 font-medium">Files ready to upload ({{ files.length }}):</div>
                <ul class="space-y-2">
                  <li v-for="(f, i) in files" :key="i" class="flex items-center justify-between bg-white/5 border border-white/10 rounded-xl p-3 hover:bg-white/10 transition-all">
                    <div class="flex items-center gap-3">
                      <svg class="w-5 h-5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                      </svg>
                      <span class="text-sm text-white">{{ f.name }}</span>
                    </div>
                    <span class="text-xs text-slate-400 bg-white/10 px-2 py-1 rounded-full">{{ formatSize(f.size) }}</span>
                  </li>
                </ul>

                <button 
                  @click="uploadFiles" 
                  :disabled="loading" 
                  class="w-full px-4 py-3 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed text-white rounded-xl shadow-lg transition-all font-medium flex items-center justify-center gap-2">
                  <svg v-if="!loading" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                  </svg>
                  <div v-else class="flex gap-1">
                    <div class="w-2 h-2 bg-white rounded-full animate-bounce"></div>
                    <div class="w-2 h-2 bg-white rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                    <div class="w-2 h-2 bg-white rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                  </div>
                  {{ loading ? 'Uploading...' : 'Upload All Files' }}
                </button>
              </div>
            </div>
          </div>
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
import { useAI } from '../../../composables/useAI'

const route = useRoute()
const router = useRouter()
const { askQuestion, uploadDocument, getUserDocuments, deleteDocument } = useAI()

const user = ref(null)
const files = ref([])
const question = ref('')
const currentAnswer = ref('')
const loading = ref(false)
const recent = ref([])
const uploadedDocuments = ref([])
const loadingDocs = ref(false)
const conversationHistory = ref([])

// Fetch user's uploaded documents
const fetchDocuments = async () => {
  loadingDocs.value = true
  try {
    const docs = await getUserDocuments()
    uploadedDocuments.value = docs
    console.log('Fetched documents:', docs)
  } catch (err) {
    console.error('Error fetching documents:', err)
  } finally {
    loadingDocs.value = false
  }
}

// Delete document
const deleteDoc = async (docId) => {
  if (!confirm('Are you sure you want to delete this document?')) return
  
  try {
    const result = await deleteDocument(docId)
    if (result.success) {
      recent.value.unshift(`Deleted document: ${docId}`)
      await fetchDocuments()
    } else {
      alert('Failed to delete document: ' + result.message)
    }
  } catch (err) {
    console.error('Error deleting document:', err)
    alert('Failed to delete document')
  }
}

// Format AI response with better styling
const formatAIResponse = (text) => {
  if (!text) return ''
  
  // Remove "The AI said:" prefix if present
  text = text.replace(/^The AI said:\s*/i, '')
  
  // Convert markdown-style formatting
  text = text
    // Bold: **text** or __text__
    .replace(/\*\*(.*?)\*\*/g, '<strong class="text-white font-semibold">$1</strong>')
    .replace(/__(.*?)__/g, '<strong class="text-white font-semibold">$1</strong>')
    // Italic: *text* or _text_
    .replace(/\*(.*?)\*/g, '<em class="text-indigo-300">$1</em>')
    .replace(/_(.*?)_/g, '<em class="text-indigo-300">$1</em>')
    // Code: `text`
    .replace(/`([^`]+)`/g, '<code class="bg-white/10 px-1.5 py-0.5 rounded text-indigo-300 font-mono text-xs">$1</code>')
    // Line breaks
    .replace(/\n/g, '<br>')
  
  return text
}

// Handle Enter key in textarea
const handleEnterKey = (e) => {
  if (!e.shiftKey) {
    // Enter without Shift = send message
    askAI()
  }
  // Shift+Enter = new line (default textarea behavior)
}

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
      
      // Fetch user's documents after authentication
      fetchDocuments()
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
  files.value = selected
}

const uploadFiles = async () => {
  if (!files.value.length) return
  
  loading.value = true
  try {
    // Upload each file
    for (const file of files.value) {
      const result = await uploadDocument(file)
      if (result.success) {
        recent.value.unshift(`✅ Uploaded: ${file.name}`)
      } else {
        recent.value.unshift(`❌ Failed: ${file.name}`)
        console.error('Upload error:', result.message)
      }
    }
    
    // Clear files and refresh documents list
    files.value = []
    if (fileInput.value) fileInput.value.value = ''
    await fetchDocuments()
  } catch (err) {
    console.error('Upload error:', err)
    recent.value.unshift('❌ Upload failed')
  } finally {
    loading.value = false
  }
}

const askAI = async () => {
  if (!question.value.trim() || loading.value) return
  
  const currentQuestion = question.value.trim()
  question.value = '' // Clear immediately for better UX
  
  loading.value = true
  currentAnswer.value = ''
  
  try {
    const result = await askQuestion(currentQuestion)
    
    if (result.success) {
      const formattedAnswer = formatAIResponse(result.answer)
      currentAnswer.value = formattedAnswer
      
      // Add to conversation history
      conversationHistory.value.push({
        question: currentQuestion,
        answer: result.answer
      })
      
      recent.value.unshift(`💬 Asked: ${currentQuestion.substring(0, 50)}...`)
    } else {
      currentAnswer.value = `<span class="text-red-400">❌ Error: ${escapeHtml(result.error || 'Failed to get answer')}</span>`
    }
  } catch (err) {
    console.error('AI error:', err)
    currentAnswer.value = `<span class="text-red-400">❌ Error: ${escapeHtml(err.message)}</span>`
  } finally {
    loading.value = false
  }
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
  if (!s) return ''
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}
</script>


