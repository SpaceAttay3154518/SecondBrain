# SecondBrain - Firebase Authentication Setup

## 🔥 Firebase Configuration

This project uses Firebase Authentication and Firestore with a clean, modular architecture.

### File Structure

```
Secondbrain/
├── firebase/
│   └── config.js          # Firebase initialization & service exports
├── composables/
│   ├── useLogin.js        # Login composable
│   └── useSignup.js       # Signup composable
├── pages/
│   └── index.vue          # Auth playground page
└── nuxt.config.ts         # Nuxt configuration
```

## 📦 Architecture

### 1. Firebase Config (`firebase/config.js`)
- Initializes Firebase App once
- Exports `projectAuth` and `projectFirestore` services
- Uses modern Firebase v9+ modular SDK

### 2. Composables
- **`useLogin()`**: Returns `{error, login}` - handles email/password sign-in
- **`useSignup()`**: Returns `{error, signup}` - handles user registration with display name

### 3. Pages
- **`index.vue`**: Auth playground with separate signup and login forms
- Uses composables for clean separation of concerns
- Tracks auth state with `onAuthStateChanged`

## 🚀 Usage

### Sign Up a New User
```javascript
const { error, signup } = useSignup()
await signup('email@example.com', 'password123', 'Display Name')
```

### Log In
```javascript
const { error, login } = useLogin()
await login('email@example.com', 'password123')
```

### Track Auth State
```javascript
import { projectAuth } from '@/firebase/config'
import { onAuthStateChanged } from 'firebase/auth'

onAuthStateChanged(projectAuth, (user) => {
  if (user) {
    console.log('Signed in:', user.email)
  } else {
    console.log('Signed out')
  }
})
```

### Log Out
```javascript
import { projectAuth } from '@/firebase/config'
import { signOut } from 'firebase/auth'

await signOut(projectAuth)
```

## ⚙️ Development

```bash
# Install dependencies
npm install

# Run dev server
npm run dev

# Visit
http://localhost:3001
```

## ✅ Firebase Services Enabled

Make sure these are enabled in your Firebase Console:
- ✅ Authentication (Email/Password provider)
- ✅ Firestore Database

## 🔐 Security Notes

- Firebase config in `firebase/config.js` contains public API keys (safe for client-side)
- Never commit service account keys or admin SDK credentials
- Configure Firestore security rules in Firebase Console

## 📝 Next Steps

- Add Google/Social sign-in providers
- Implement Firestore CRUD operations
- Add auth route guards for protected pages
- Move Firebase config to environment variables
