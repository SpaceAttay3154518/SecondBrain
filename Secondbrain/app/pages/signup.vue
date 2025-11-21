<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 relative overflow-hidden">
    <div class="absolute top-0 right-0 w-80 h-80 bg-purple-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30"></div>
    <div class="absolute bottom-0 left-0 w-80 h-80 bg-indigo-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30"></div>

    <div class="relative z-10 flex items-center justify-center min-h-screen px-6 py-12">
      <div class="w-full max-w-md bg-white rounded-3xl p-8 shadow-2xl border border-slate-100">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0"></path>
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-extrabold text-slate-800">Create your account</h2>
            <p class="text-sm text-slate-500">Start building your second brain — it's free to try.</p>
          </div>
        </div>

        <form @submit.prevent="handleSignup" class="space-y-4 mt-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700">First Name</label>
              <input v-model="firstName" type="text" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700">Last Name</label>
              <input v-model="lastName" type="text" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700">Display name</label>
            <input v-model="displayName" type="text" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700">Profession</label>
            <input v-model="profession" type="text" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700">Email</label>
            <input v-model="email" type="email" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700">Password</label>
            <input v-model="password" type="password" required class="mt-1 block w-full rounded-md border-slate-200 shadow-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 px-3 py-2" />
          </div>

          <div class="flex items-center justify-between mt-2">
            <NuxtLink to="/" class="text-sm text-slate-500 hover:underline">Back</NuxtLink>
            <button type="submit" class="inline-flex items-center gap-2 px-5 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl shadow-lg font-semibold">Create account</button>
          </div>

          <div v-if="error" class="text-sm text-red-600">{{ error }}</div>
        </form>

        <div class="mt-6 text-sm text-slate-600">Already have an account? <NuxtLink to="/login" class="text-indigo-600 font-medium hover:underline">Sign in</NuxtLink></div>
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
