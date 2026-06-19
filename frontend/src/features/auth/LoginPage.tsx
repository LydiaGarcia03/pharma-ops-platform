import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@apollo/client/react'
import { useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { Pill, AlertCircle } from 'lucide-react'
import { LOGIN_MUTATION } from '@/graphql/mutations'
import { identityClient } from '@/graphql/apolloClients'
import { loginSuccess } from './authSlice'
import { Input } from '@/components/ui/Input'
import { Button } from '@/components/ui/Button'

const schema = z.object({
  email: z.string().email('Invalid e-mail'),
  password: z.string().min(1, 'Enter your password'),
})
type FormData = z.infer<typeof schema>

export function LoginPage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const [login, { loading, error }] = useMutation(LOGIN_MUTATION, {
    client: identityClient,
  })

  async function onSubmit(data: FormData) {
    const result = await login({ variables: { input: data } })
    const payload = result.data as { login: { accessToken: string; passwordResetRequired: boolean; user: { id: string; name: string; email: string } } }
    const { accessToken, passwordResetRequired, user } = payload.login
    dispatch(loginSuccess({
      token: accessToken,
      user: { id: user.id, name: user.name, email: user.email, roles: [] },
      passwordResetRequired,
    }))
    navigate('/dashboard')
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-emerald-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 bg-indigo-600 rounded-2xl flex items-center justify-center shadow-lg mb-4">
            <Pill className="w-7 h-7 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">PharmaOps</h1>
          <p className="text-sm text-slate-500 mt-1">Pharmacy chain management app</p>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">
          <h2 className="text-lg font-semibold text-slate-900 mb-6">Log in to your account</h2>

          {error && (
            <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3 mb-5">
              <AlertCircle className="w-4 h-4 shrink-0" />
              {error.networkError
                ? 'Serviço indisponível. Verifique se o identity-service está rodando.'
                : 'Credenciais inválidas. Verifique e tente novamente.'}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="E-mail"
              type="email"
              placeholder="your@email.com"
              autoComplete="email"
              error={errors.email?.message}
              {...register('email')}
            />
            <Input
              label="Password"
              type="password"
              placeholder="••••••••"
              autoComplete="current-password"
              error={errors.password?.message}
              {...register('password')}
            />

            <Button type="submit" className="w-full mt-2" size="lg" loading={loading}>
              Log in
            </Button>
          </form>
        </div>

        <p className="text-center text-xs text-slate-400 mt-6">
          pharma-ops-platform © 2026
        </p>
      </div>
    </div>
  )
}
