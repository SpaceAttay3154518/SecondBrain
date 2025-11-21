<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 relative overflow-hidden">
    <!-- Background decorative elements -->
    <div class="absolute top-0 right-0 w-96 h-96 bg-purple-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse"></div>
    <div class="absolute bottom-0 left-0 w-96 h-96 bg-indigo-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse" style="animation-delay: 1s"></div>
    
    <!-- Top-aligned container that stretches: left content takes more space on large screens -->
    <div class="relative z-10 px-8 py-16">
      <div class="w-full grid grid-cols-1 lg:grid-cols-3 gap-10 items-start max-w-7xl mx-auto">
        <!-- Left: Hero (spans two columns on large screens) -->
        <section class="space-y-8 lg:col-span-2">
          <div class="inline-block px-4 py-2 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium mb-4">
            ✨ AI-Powered Knowledge Management
          </div>
          
          <h1 class="text-5xl sm:text-6xl font-extrabold leading-tight bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent max-w-3xl">
            Build Your Second Brain
          </h1>
          
          <p class="text-xl text-slate-600 leading-relaxed">
            Transform your documents into an intelligent assistant. Upload your notes, get instant answers, and master your knowledge with AI-generated quizzes.
          </p>

          <!-- Feature highlights: stack vertically to use more vertical space -->
          <div class="flex flex-col gap-6 pt-4">
            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 w-12 h-12 bg-indigo-100 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-slate-800">Smart Document Analysis</h3>
                <p class="text-sm text-slate-600">Upload any folder and let AI understand your content</p>
              </div>
            </div>

            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-slate-800">Instant Q&A</h3>
                <p class="text-sm text-slate-600">Ask questions and get answers from your notes</p>
              </div>
            </div>

            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 w-12 h-12 bg-pink-100 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-slate-800">AI Quiz Generator</h3>
                <p class="text-sm text-slate-600">Test your knowledge with personalized quizzes</p>
              </div>
            </div>

            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 w-12 h-12 bg-teal-100 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-slate-800">Smart Search</h3>
                <p class="text-sm text-slate-600">Find information instantly across all documents</p>
              </div>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-4 pt-4">
            <NuxtLink to="/signup" class="group inline-flex items-center justify-center gap-2 px-8 py-4 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 font-semibold">
              Get Started Free
              <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
              </svg>
            </NuxtLink>
            <NuxtLink to="/login" class="inline-flex items-center justify-center gap-2 px-8 py-4 border-2 border-slate-300 rounded-xl text-slate-700 hover:bg-slate-50 hover:border-slate-400 transition-all duration-200 font-semibold">
              Sign In
            </NuxtLink>
          </div>

          <div class="flex items-center gap-6 pt-2 text-sm text-slate-600">
            <div class="flex items-center gap-2">
              <svg class="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
              </svg>
              <span>No credit card required</span>
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
              </svg>
              <span>Free to start</span>
            </div>
          </div>
        </section>

        <!-- Right: Visual Demo Card (smaller column) -->
        <aside class="relative lg:col-span-1">
          <div class="bg-white rounded-3xl p-6 shadow-2xl border border-slate-100 h-full">
            <div class="space-y-6">
              <!-- Header with icon -->
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center">
                  <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                  </svg>
                </div>
                <div>
                  <h3 class="text-xl font-bold text-slate-800">How it works</h3>
                  <p class="text-sm text-slate-500">Three simple steps to get started</p>
                </div>
              </div>

              <!-- Steps -->
              <div class="space-y-4">
                <div class="flex gap-4 p-4 bg-gradient-to-r from-indigo-50 to-purple-50 rounded-xl border border-indigo-100">
                  <div class="flex-shrink-0 w-8 h-8 bg-indigo-600 text-white rounded-full flex items-center justify-center font-bold text-sm">1</div>
                  <div>
                    <h4 class="font-semibold text-slate-800 mb-1">Upload Your Files</h4>
                    <p class="text-sm text-slate-600">Drop your documents, notes, or study materials into SecondBrain</p>
                  </div>
                </div>

                <div class="flex gap-4 p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl border border-purple-100">
                  <div class="flex-shrink-0 w-8 h-8 bg-purple-600 text-white rounded-full flex items-center justify-center font-bold text-sm">2</div>
                  <div>
                    <h4 class="font-semibold text-slate-800 mb-1">AI Learns Your Content</h4>
                    <p class="text-sm text-slate-600">Our intelligent assistant analyzes and understands your documents</p>
                  </div>
                </div>

                <div class="flex gap-4 p-4 bg-gradient-to-r from-pink-50 to-teal-50 rounded-xl border border-pink-100">
                  <div class="flex-shrink-0 w-8 h-8 bg-pink-600 text-white rounded-full flex items-center justify-center font-bold text-sm">3</div>
                  <div>
                    <h4 class="font-semibold text-slate-800 mb-1">Ask & Learn</h4>
                    <p class="text-sm text-slate-600">Get answers, generate quizzes, and master your knowledge</p>
                  </div>
                </div>
              </div>

              <!-- Demo interaction preview -->
              <div class="bg-slate-50 rounded-xl p-4 border border-slate-200">
                <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">Example Query</div>
                <div class="bg-white rounded-lg p-3 mb-3 shadow-sm">
                  <p class="text-sm text-slate-700">"Generate a quiz from my biology notes"</p>
                </div>
                <div class="flex items-center gap-2 text-xs text-slate-500">
                  <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                  <span>AI ready to help with your documents</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Floating badge -->
          <div class="absolute -top-4 -right-4 bg-gradient-to-br from-yellow-400 to-orange-500 text-white px-4 py-2 rounded-full shadow-lg transform rotate-12 font-bold text-sm">
            Powered by AI
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
// Page showcasing SecondBrain's AI-powered document intelligence capabilities
</script>