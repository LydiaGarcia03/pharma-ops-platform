import { useForm, useFieldArray } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@apollo/client/react'
import { ShoppingCart, Plus, Trash2, CheckCircle, AlertCircle } from 'lucide-react'
import { useState } from 'react'
import { useSelector } from 'react-redux'
import { CREATE_SALE_MUTATION } from '@/graphql/mutations'
import { salesClient } from '@/graphql/apolloClients'
import type { RootState } from '@/app/store'
import { Input } from '@/components/ui/Input'
import { Button } from '@/components/ui/Button'
import { Card, CardHeader, CardTitle } from '@/components/ui/Card'

const itemSchema = z.object({
  productId: z.string().uuid('ID de produto inválido'),
  controlled: z.boolean(),
  quantity: z.number().int().min(1, 'Mínimo 1'),
  unitPrice: z.string().refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Preço inválido'),
})

const schema = z.object({
  storeId: z.string().uuid('ID de loja inválido'),
  responsiblePharmacistId: z.string().optional(),
  forced: z.boolean(),
  items: z.array(itemSchema).min(1, 'Adicione ao menos um item'),
})
type FormData = z.infer<typeof schema>

export function CreateSalePage() {
  const [successMsg, setSuccessMsg] = useState<string | null>(null)
  const user = useSelector((s: RootState) => s.auth.user)

  const { register, handleSubmit, control, watch, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { forced: false, items: [{ productId: '', controlled: false, quantity: 1, unitPrice: '' }] },
  })

  const { fields, append, remove } = useFieldArray({ control, name: 'items' })

  const [createSale, { loading, error }] = useMutation(CREATE_SALE_MUTATION, {
    client: salesClient,
  })

  const watchedItems = watch('items')
  const total = watchedItems.reduce((sum, item) => {
    const price = parseFloat(item.unitPrice) || 0
    return sum + price * (item.quantity || 0)
  }, 0)

  const hasControlled = watchedItems.some((i) => i.controlled)
  const forced = watch('forced')
  const needsPharmacist = hasControlled || forced

  async function onSubmit(data: FormData) {
    const result = await createSale({
      variables: {
        input: {
          storeId: data.storeId,
          userId: user?.id,
          responsiblePharmacistId: data.responsiblePharmacistId || null,
          forced: data.forced,
          items: data.items,
        },
      },
    })
    const payload = result.data as { createSale: { id: string; status: string; total: string; createdAt: string } }
    const sale = payload.createSale
    setSuccessMsg(`Venda #${sale.id.slice(0, 8)} confirmada — Total: R$ ${parseFloat(sale.total).toFixed(2)}`)
    reset()
    setTimeout(() => setSuccessMsg(null), 6000)
  }

  return (
    <div className="max-w-3xl mx-auto">
      {/* Page header */}
      <div className="flex items-center gap-3 mb-6">
        <div className="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
          <ShoppingCart className="w-5 h-5 text-indigo-600" />
        </div>
        <div>
          <h1 className="text-xl font-bold text-slate-900">Nova Venda</h1>
          <p className="text-sm text-slate-500">Registre uma venda no PDV</p>
        </div>
      </div>

      {successMsg && (
        <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl px-4 py-3 mb-5">
          <CheckCircle className="w-4 h-4 shrink-0" />
          {successMsg}
        </div>
      )}

      {error && (
        <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-700 text-sm rounded-xl px-4 py-3 mb-5">
          <AlertCircle className="w-4 h-4 shrink-0" />
          {error.message.includes('insufficient stock')
            ? 'Estoque insuficiente para um ou mais produtos.'
            : 'Erro ao registrar venda. Tente novamente.'}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        {/* Sale context */}
        <Card>
          <CardHeader>
            <CardTitle>Contexto da venda</CardTitle>
          </CardHeader>
          <div className="space-y-4">
            <Input
              label="ID da Loja"
              placeholder="UUID da loja"
              error={errors.storeId?.message}
              {...register('storeId')}
            />

            <label className="flex items-start gap-3 p-3.5 rounded-xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/40 cursor-pointer transition-colors">
              <input
                type="checkbox"
                className="mt-0.5 w-4 h-4 accent-indigo-600"
                {...register('forced')}
              />
              <div>
                <p className="text-sm font-medium text-slate-900">Venda forçada</p>
                <p className="text-xs text-slate-500">Exige farmacêutico responsável</p>
              </div>
            </label>

            {needsPharmacist && (
              <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
                <p className="text-xs font-medium text-amber-800 mb-2 flex items-center gap-1.5">
                  <AlertCircle className="w-3.5 h-3.5" />
                  Farmacêutico responsável obrigatório
                </p>
                <Input
                  label="ID do Farmacêutico Responsável"
                  placeholder="UUID do farmacêutico"
                  error={errors.responsiblePharmacistId?.message}
                  {...register('responsiblePharmacistId')}
                />
              </div>
            )}
          </div>
        </Card>

        {/* Items */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Itens da venda</CardTitle>
              <Button
                type="button"
                variant="secondary"
                size="sm"
                onClick={() => append({ productId: '', controlled: false, quantity: 1, unitPrice: '' })}
              >
                <Plus className="w-3.5 h-3.5" />
                Adicionar item
              </Button>
            </div>
          </CardHeader>

          <div className="space-y-4">
            {fields.map((field, index) => (
              <div key={field.id} className="p-4 bg-slate-50 rounded-xl border border-slate-200 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                    Item {index + 1}
                  </span>
                  {fields.length > 1 && (
                    <button
                      type="button"
                      onClick={() => remove(index)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <Input
                    label="ID do Produto"
                    placeholder="UUID do produto"
                    error={errors.items?.[index]?.productId?.message}
                    {...register(`items.${index}.productId`)}
                  />
                  <div className="grid grid-cols-2 gap-3">
                    <Input
                      label="Qtd"
                      type="number"
                      min={1}
                      error={errors.items?.[index]?.quantity?.message}
                      {...register(`items.${index}.quantity`, { valueAsNumber: true })}
                    />
                    <Input
                      label="Preço unit. (R$)"
                      type="number"
                      step="0.01"
                      min="0"
                      placeholder="0,00"
                      error={errors.items?.[index]?.unitPrice?.message}
                      {...register(`items.${index}.unitPrice`)}
                    />
                  </div>
                </div>

                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    className="w-4 h-4 accent-indigo-600"
                    {...register(`items.${index}.controlled`)}
                  />
                  <span className="text-xs text-slate-600">Medicamento controlado</span>
                </label>
              </div>
            ))}
          </div>

          {/* Total */}
          <div className="mt-5 pt-4 border-t border-slate-100 flex justify-between items-center">
            <span className="text-sm text-slate-500">Total estimado</span>
            <span className="text-xl font-bold text-slate-900">
              R$ {total.toFixed(2).replace('.', ',')}
            </span>
          </div>
        </Card>

        <div className="flex gap-3">
          <Button type="submit" loading={loading} size="lg" className="flex-1 sm:flex-none">
            Confirmar venda
          </Button>
          <Button type="button" variant="secondary" onClick={() => reset()} size="lg">
            Cancelar
          </Button>
        </div>
      </form>
    </div>
  )
}
