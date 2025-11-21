export default defineNuxtConfig({
  // The `nuxi module add vuefire` helper tries to add a Nuxt module named
  // "vuefire" or "nuxt-vuefire". In many setups the module isn't published
  // under that name (or it's meant for older Nuxt versions). This project
  // already depends on the `vuefire` package in package.json, so attempting
  // to add a Nuxt module can fail and isn't required.

  // Move Firebase config to runtime config and initialize the Firebase SDK
  // from a plugin (see `plugins/firebase.client.ts`). If you want a Nuxt
  // module that wires VueFire for you, install the official Nuxt integration
  // (if available) — otherwise the plugin approach below is a safe fallback.

  runtimeConfig: {
    public: {
      firebase: {
        apiKey: "AIzaSyCnYiNShZ9wod6AjkK0nLXfiCqx9b2aYgQ",        // Replace with your Firebase apiKey
        authDomain: "second-brain-30911.firebaseapp.com", // e.g., project-id.firebaseapp.com
        projectId: "second-brain-30911",
        appId: "1:1053253631752:web:2ac7b34fd06a2cfc3676bd"
        // Add other config options if needed, like storageBucket or messagingSenderId
      }
    }
  }
    ,
    // Enable Tailwind via the Nuxt Tailwind module and include a small global CSS file.
    modules: [
      '@nuxtjs/tailwindcss'
    ],
    // Let the Tailwind Nuxt module inject the CSS. Using the module option
    // `cssPath` avoids generating an import that Vite may not resolve in some
    // environments (the module resolves the alias internally).
    tailwindcss: {
        cssPath: '~/assets/css/tailwind.css'
    },

    // Move PostCSS plugins into nuxt.config to avoid the `postcss.config.js`
    // warning that Nuxt prints when both are present.
    postcss: {
      plugins: {
        tailwindcss: {},
        autoprefixer: {}
      }
    }
})