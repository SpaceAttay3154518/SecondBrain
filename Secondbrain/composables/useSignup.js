import { projectAuth, projectFirestore } from "../firebase/config"
import { createUserWithEmailAndPassword, updateProfile } from "firebase/auth"
import { doc, setDoc } from "firebase/firestore"
import { ref } from "vue"

const error = ref(null)

const getFriendlyErrorMessage = (code) => {
    switch (code) {
        case 'auth/email-already-in-use':
            return 'This email is already registered. Try logging in instead.'
        case 'auth/weak-password':
            return 'Password should be at least 6 characters long.'
        case 'auth/invalid-email':
            return 'Please enter a valid email address.'
        case 'auth/network-request-failed':
            return 'Network error. Please check your internet connection and try again.'
        case 'auth/too-many-requests':
            return 'Too many signup attempts. Please try again later.'
        default:
            return 'An error occurred during signup. Please try again.'
    }
}

const signup = async (firstName, lastName, email, password, displayName, profession) => {
    error.value = null
    try {
        const response = await createUserWithEmailAndPassword(projectAuth, email, password)

        if(!response)
            throw new Error('Could not signup')
        console.log(response.user)
        
        // Update the user's display name
        await updateProfile(response.user, { displayName: displayName })

        // Store additional user info in Firestore
        try {
            await setDoc(doc(projectFirestore, 'users', response.user.uid), {
                firstName: firstName,
                lastName: lastName,
                profession: profession,
                email: email,
                displayName: displayName
            })
            console.log('User document created in Firestore')
        } catch (firestoreErr) {
            console.error('Firestore error:', firestoreErr)
            // Don't throw, as user is created, just log
        }

        error.value = null
        return response
    } catch (err) {
        console.log('---> Signup Error: ', err.message)
        error.value = getFriendlyErrorMessage(err.code || err.message)
    }
}

const useSignup = () => {
    return {error, signup}
}

export default useSignup
