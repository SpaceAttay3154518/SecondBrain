<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 relative">
    <div class="relative z-10 px-6 py-10 max-w-4xl mx-auto">
      <div class="bg-white rounded-2xl p-6 shadow-lg border border-slate-100">
        <header class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 rounded-xl overflow-hidden bg-slate-100 flex items-center justify-center relative">
              <img v-if="user && user.photoURL" :src="user.photoURL" alt="avatar" class="w-full h-full object-cover" />
              <svg v-else class="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14v7"></path></svg>
              <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarSelected" />
              <button @click="triggerAvatarInput" title="Upload avatar" class="absolute -bottom-2 -right-2 bg-white border rounded-full p-1 shadow text-xs">✎</button>
            </div>
            <div>
              <h2 class="text-xl font-bold text-slate-800">Profile</h2>
              <p class="text-sm text-slate-500">Manage your account information</p>
            </div>
          </div>
          <div>
            <button @click="handleLogout" class="px-3 py-2 bg-white rounded-md border border-slate-200 shadow-sm hover:bg-slate-50">Logout</button>
          </div>
        </header>

        <section v-if="loadingUser" class="py-12 text-center text-slate-500">Loading...</section>

        <section v-else>
          <form @submit.prevent="saveProfile" class="grid grid-cols-1 gap-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-slate-700">First Name</label>
                <input v-model="form.firstName" type="text" class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700">Last Name</label>
                <input v-model="form.lastName" type="text" class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700">Display name</label>
              <input v-model="form.displayName" type="text" class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700">Profession</label>
              <input v-model="form.profession" type="text" class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700">Avatar URL</label>
              <input v-model="form.photoURL" type="url" placeholder="https://..." class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700">Email</label>
              <div class="mt-1 flex gap-3 items-center">
                <input v-model="form.email" type="email" class="flex-1 rounded-md border border-slate-200 px-3 py-2 bg-slate-50" disabled />
                <button type="button" @click="openEmailChange" class="px-3 py-2 bg-indigo-600 text-white rounded-md">Change</button>
              </div>
              <p class="text-xs text-slate-500 mt-2">Changing email requires entering your current password for security.</p>
            </div>

            <div class="flex items-center gap-3 pt-2">
              <button type="submit" class="px-4 py-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl shadow">Save</button>
              <button type="button" @click="resetForm" class="px-3 py-2 border rounded-md">Reset</button>
              <div v-if="statusMessage" :class="statusClass" class="text-sm px-3 py-1 rounded-md">{{ statusMessage }}</div>
            </div>
          </form>

          <!-- Email change modal -->
          <div v-if="showEmailModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
              <h3 class="text-lg font-semibold mb-3">Change Email</h3>
              <p class="text-sm text-slate-600 mb-4">Enter a new email and your current password to confirm.</p>
              <form @submit.prevent="changeEmail" class="space-y-3">
                <div>
                  <label class="block text-sm text-slate-700">New email</label>
                  <input v-model="emailChange.newEmail" type="email" required class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
                </div>
                <div>
                  <label class="block text-sm text-slate-700">Current password</label>
                  <input v-model="emailChange.password" type="password" required class="mt-1 block w-full rounded-md border border-slate-200 px-3 py-2" />
                </div>
                <div class="flex items-center justify-end gap-2 pt-2">
                  <button type="button" @click="closeEmailChange" class="px-3 py-2 border rounded-md">Cancel</button>
                  <button type="submit" class="px-4 py-2 bg-indigo-600 text-white rounded-md">Confirm</button>
                </div>
                <div v-if="emailChange.error" class="text-sm text-red-600">{{ emailChange.error }}</div>
              </form>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { projectAuth, projectFirestore } from '../../../firebase/config'
import { onAuthStateChanged, updateProfile, updateEmail, reauthenticateWithCredential, EmailAuthProvider, signOut } from 'firebase/auth'
import { doc, getDoc, setDoc } from 'firebase/firestore'

const route = useRoute()
const router = useRouter()

const user = ref(null)
const loadingUser = ref(true)
const statusMessage = ref('')
const statusClass = ref('text-slate-700')

const form = reactive({ displayName: '', photoURL: '', email: '', profession: '', firstName: '', lastName: '' })

const showEmailModal = ref(false)
const emailChange = reactive({ newEmail: '', password: '', error: '' })

function setStatus(msg, ok = true) {
  statusMessage.value = msg
  statusClass.value = ok ? 'text-green-600' : 'text-red-600'
  setTimeout(() => (statusMessage.value = ''), 4000)
}

onMounted(async () => {
  if (process.client) {
    onAuthStateChanged(projectAuth, async (u) => {
      if (!u) {
        router.push('/login')
        return
      }
      // restrict access to the owner of this profile
      const id = route.params.id
      if (id && id !== u.uid) {
        router.push(`/${u.uid}`)
        return
      }
      user.value = u
      form.displayName = u.displayName || ''
      form.photoURL = u.photoURL || ''
      form.email = u.email || ''
      // Load additional data from Firestore
      const userDoc = await getDoc(doc(projectFirestore, 'users', u.uid))
      if (userDoc.exists()) {
        const data = userDoc.data()
        form.profession = data.profession || ''
        form.firstName = data.firstName || ''
        form.lastName = data.lastName || ''
      }
      loadingUser.value = false
    })
  }
})

const resetForm = () => {
  if (user.value) {
    form.displayName = user.value.displayName || ''
    form.photoURL = user.value.photoURL || ''
    form.email = user.value.email || ''
    form.profession = form.profession || '' // Keep current profession
    form.firstName = form.firstName || ''
    form.lastName = form.lastName || ''
    setStatus('Form reset', true)
  }
}

const saveProfile = async () => {
  if (!user.value) return
  try {
    await updateProfile(user.value, { displayName: form.displayName, photoURL: form.photoURL })
    // Save additional data to Firestore
    await setDoc(doc(projectFirestore, 'users', user.value.uid), {
      firstName: form.firstName,
      lastName: form.lastName,
      profession: form.profession,
      email: form.email,
      displayName: form.displayName
    }, { merge: true })
    setStatus('Profile updated successfully', true)
  } catch (err) {
    console.error(err)
    setStatus('Failed to update profile: ' + (err.message || err), false)
  }
}

const openEmailChange = () => {
  emailChange.newEmail = ''
  emailChange.password = ''
  emailChange.error = ''
  showEmailModal.value = true
}

const closeEmailChange = () => {
  showEmailModal.value = false
}

const changeEmail = async () => {
  emailChange.error = ''
  if (!user.value) return
  try {
    // Reauthenticate user with provided password
    const cred = EmailAuthProvider.credential(user.value.email, emailChange.password)
    await reauthenticateWithCredential(user.value, cred)
    await updateEmail(user.value, emailChange.newEmail)
    form.email = emailChange.newEmail
    setStatus('Email changed successfully', true)
    showEmailModal.value = false
  } catch (err) {
    console.error('Email change error', err)
    emailChange.error = err.message || String(err)
  }
}

const handleLogout = async () => {
  try {
    await signOut(projectAuth)
    router.push('/')
  } catch (err) {
    console.error('Logout error', err)
  }
}

// Avatar upload logic (uses Firebase Storage)
import { getStorage, ref as storageRef, uploadBytes, getDownloadURL } from 'firebase/storage'

const avatarFile = ref(null)
const avatarInput = ref(null)

const triggerAvatarInput = () => {
  if (avatarInput.value) avatarInput.value.click()
}

const handleAvatarSelected = async (e) => {
  const f = e.target.files && e.target.files[0]
  if (!f || !user.value) return
  avatarFile.value = f
  // Optionally show preview before upload by setting form.photoURL to local object URL
  form.photoURL = URL.createObjectURL(f)
  // Auto-upload
  await uploadAvatar()
}

const uploadAvatar = async () => {
  if (!avatarFile.value || !user.value) return
  try {
    const storage = getStorage()
    const fileRef = storageRef(storage, `avatars/${user.value.uid}/${Date.now()}_${avatarFile.value.name}`)
    await uploadBytes(fileRef, avatarFile.value)
    const url = await getDownloadURL(fileRef)
    // Update auth profile with new photoURL
    await updateProfile(user.value, { photoURL: url })
    form.photoURL = url
    setStatus('Avatar uploaded', true)
  } catch (err) {
    console.error('Avatar upload failed', err)
    setStatus('Avatar upload failed: ' + (err.message || err), false)
  }
}
</script>

<style scoped></style>
