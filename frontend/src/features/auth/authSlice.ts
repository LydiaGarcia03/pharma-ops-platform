import { createSlice, type PayloadAction } from '@reduxjs/toolkit'

interface AuthUser {
  id: string
  name: string
  email: string
  roles: string[]
  storeId?: string
}

interface AuthState {
  token: string | null
  user: AuthUser | null
  passwordResetRequired: boolean
}

const stored = localStorage.getItem('auth')
const initialState: AuthState = stored
  ? (JSON.parse(stored) as AuthState)
  : { token: null, user: null, passwordResetRequired: false }

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginSuccess(state, action: PayloadAction<{ token: string; user: AuthUser; passwordResetRequired: boolean }>) {
      state.token = action.payload.token
      state.user = action.payload.user
      state.passwordResetRequired = action.payload.passwordResetRequired
      localStorage.setItem('auth', JSON.stringify(state))
    },
    logout(state) {
      state.token = null
      state.user = null
      state.passwordResetRequired = false
      localStorage.removeItem('auth')
    },
  },
})

export const { loginSuccess, logout } = authSlice.actions
export default authSlice.reducer
