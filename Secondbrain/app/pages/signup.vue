<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden">
    <div class="absolute top-0 right-0 w-96 h-96 bg-indigo-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
    <div class="absolute bottom-0 left-0 w-96 h-96 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse" style="animation-delay: 1s"></div>

    <div class="relative z-10 flex items-center justify-center min-h-screen px-6 py-12">
      <div class="w-full max-w-md bg-white/10 backdrop-blur-md rounded-3xl p-8 shadow-2xl border border-white/20">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-12 h-12 bg-gradient-to-br from-indigo-400 to-purple-500 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-500/50">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-extrabold text-white">Create your account</h2>
            <p class="text-sm text-indigo-300">Start building your AI-powered knowledge base</p>
          </div>
        </div>

        <form @submit.prevent="handleSignup" class="space-y-4 mt-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-200">First Name</label>
              <input v-model="firstName" type="text" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-200">Last Name</label>
              <input v-model="lastName" type="text" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-200">Display name</label>
            <input v-model="displayName" type="text" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-200">Profession</label>
            <input v-model="profession" type="text" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-200">Email</label>
            <input v-model="email" type="email" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-200">Password</label>
            <input v-model="password" type="password" required class="mt-1 block w-full rounded-xl bg-white/10 backdrop-blur-sm text-white placeholder-slate-400 border border-white/20 shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-transparent px-4 py-3" />
          </div>

          <div class="flex items-center justify-between mt-2">
            <NuxtLink to="/" class="text-sm text-indigo-300 hover:text-indigo-200 transition-colors">← Back to home</NuxtLink>
            <button type="submit" class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white rounded-xl shadow-lg shadow-indigo-500/50 font-semibold transition-all">Create account</button>
          </div>

          <div v-if="error" class="text-sm text-red-400 bg-red-500/10 border border-red-500/20 rounded-lg px-4 py-2">{{ error }}</div>
        </form>

        <div class="mt-6 text-sm text-slate-300">Already have an account? <NuxtLink to="/login" class="text-indigo-400 font-medium hover:text-indigo-300 transition-colors">Sign in</NuxtLink></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import useSignup from '../../composables/useSignup'
import { useRouter } from 'vue-router'

const { error, signup } = useSignup()
const displayName = ref('')
const firstName = ref('')
const lastName = ref('')
const profession = ref('')
const email = ref('')
const password = ref('')
const router = useRouter()

const handleSignup = async () => {
  const res = await signup(firstName.value , lastName.value ,email.value, password.value, displayName.value, profession.value)
  if (res && res.user) {
    // Redirect newly registered user to their dashboard (by uid)
    router.push(`/${res.user.uid}`)
  }
}
</script>
