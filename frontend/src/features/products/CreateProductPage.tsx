import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@apollo/client/react'
import { CheckCircle, Package } from 'lucide-react'
import { useState } from 'react'
import { CREATE_PRODUCT_MUTATION } from '@/graphql/mutations'
import { inventoryClient } from '@/graphql/apolloClients'
import { Input } from '@/components/ui/Input'
import { Button } from '@/components/ui/Button'
import { Card, CardHeader, CardTitle } from '@/components/ui/Card'

const schema = z.object({
  name: z.string().min(2, 'Nome deve ter ao menos 2 caracteres'),
  barcode: z.string().min(8, 'Código de barras inválido'),
  salePrice: z.string().refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Preço inválido'),
  controlled: z.boolean(),
})
type FormData = z.infer<typeof schema>

export function CreateProductPage() {
  const [success, setSuccess] = useState(false)

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { controlled: false },
  })

  const [createProduct, { loading, error }] = useMutation(CREATE_PRODUCT_MUTATION, {
    client: inventoryClient,
  })

  async function onSubmit(data: FormData) {
    await createProduct({
      variables: { input: data },
    })
    setSuccess(true)
    reset()
    setTimeout(() => setSuccess(false), 4000)
  }

  return (
    <div className="max-w-2xl mx-auto">
      {/* Page header */}
      <div className="flex items-center gap-3 mb-6">
        <div className="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
          <Package className="w-5 h-5 text-indigo-600" />
        </div>
        <div>
          <h1 className="text-xl font-bold text-slate-900">Cadastro de Produto</h1>
          <p className="text-sm text-slate-500">Adicione um novo produto ao inventário</p>
        </div>
      </div>

      {success && (
        <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl px-4 py-3 mb-5">
          <CheckCircle className="w-4 h-4 shrink-0" />
          Produto cadastrado com sucesso!
        </div>
      )}

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-xl px-4 py-3 mb-5">
          Erro ao cadastrar produto. Verifique os dados e tente novamente.
        </div>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Dados do produto</CardTitle>
        </CardHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <Input
            label="Nome do produto"
            placeholder="Ex: Dipirona 500mg"
            error={errors.name?.message}
            {...register('name')}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Código de barras"
              placeholder="Ex: 7891234567890"
              error={errors.barcode?.message}
              {...register('barcode')}
            />
            <Input
              label="Preço de venda (R$)"
              type="number"
              step="0.01"
              min="0"
              placeholder="0,00"
              error={errors.salePrice?.message}
              {...register('salePrice')}
            />
          </div>

          {/* Controlled toggle */}
          <label className="flex items-start gap-3 p-4 rounded-xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/40 cursor-pointer transition-colors">
            <input
              type="checkbox"
              className="mt-0.5 w-4 h-4 rounded border-slate-300 text-indigo-600 accent-indigo-600"
              {...register('controlled')}
            />
            <div>
              <p className="text-sm font-medium text-slate-900">Medicamento controlado</p>
              <p className="text-xs text-slate-500 mt-0.5">
                Vendas exigirão farmacêutico responsável presente
              </p>
            </div>
          </label>

          <div className="flex gap-3 pt-2">
            <Button type="submit" loading={loading} size="lg" className="flex-1 sm:flex-none">
              Cadastrar produto
            </Button>
            <Button type="button" variant="secondary" onClick={() => reset()} size="lg">
              Limpar
            </Button>
          </div>
        </form>
      </Card>
    </div>
  )
}
