export default defineNuxtConfig({
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