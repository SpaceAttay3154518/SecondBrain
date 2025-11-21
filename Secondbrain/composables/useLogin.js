import { projectAuth } from "../firebase/config"
import { signInWithEmailAndPassword } from "firebase/auth"
import { ref } from "vue"

const error = ref(null)

const login = async (email, password) => {
    error.value = null
    try {
        const response = await signInWithEmailAndPassword(projectAuth, email, password)
        error.value = null
        console.log(response)
        return response
    } catch (err) {
        console.log('---> Login Error: ', err.message)
        error.value = err.message
    }
}

const useLogin = () => {
    return {error, login}
}

export default useLogin
