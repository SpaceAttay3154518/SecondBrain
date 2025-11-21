import { initializeApp, getApps, getApp } from "firebase/app"
import { getAuth } from "firebase/auth"
import { getFirestore } from "firebase/firestore"

const firebaseConfig = {
  apiKey: "AIzaSyCnYiNShZ9wod6AjkK0nLXfiCqx9b2aYgQ",
  authDomain: "second-brain-30911.firebaseapp.com",
  projectId: "second-brain-30911",
  storageBucket: "second-brain-30911.firebasestorage.app",
  messagingSenderId: "1053253631752",
  appId: "1:1053253631752:web:2ac7b34fd06a2cfc3676bd"
}

// Initialize Firebase App (only once)
let firebaseApp
if (getApps().length === 0) {
  firebaseApp = initializeApp(firebaseConfig)
} else {
  firebaseApp = getApp()
}

// Export Firebase services
export const projectAuth = getAuth(firebaseApp)
export const projectFirestore = getFirestore(firebaseApp)
