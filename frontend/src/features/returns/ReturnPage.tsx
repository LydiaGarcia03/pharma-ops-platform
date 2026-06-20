import { useState } from 'react'
import { useQuery, useLazyQuery, useMutation } from '@apollo/client/react'
import { useSelector } from 'react-redux'
import { Search, AlertCircle, CheckCircle, Package, RotateCcw, ChevronRight } from 'lucide-react'
import type { RootState } from '@/app/store'
import { inventoryClient, salesClient } from '@/graphql/apolloClients'
import { PRODUCTS_QUERY, GET_SALE_QUERY } from '@/graphql/queries'
import { PROCESS_RETURN_MUTATION } from '@/graphql/mutations'

interface SaleItem {
  id: string
  productId: string
  batchId: string
  quantity: number
  unitPrice: string
}

interface Sale {
  id: string
  status: string
  total: string
  createdAt: string
  items: SaleItem[]
}

interface Product {
  id: string
  name: string
}

const cardStyle: React.CSSProperties = {
  background: 'var(--card-bg)',
  border: '1px solid var(--card-border)',
  boxShadow: 'var(--card-shadow)',
  borderRadius: 'var(--card-radius)',
}

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '0.5rem 0.75rem',
  background: '#f8fafc',
  border: '1px solid var(--card-border)',
  borderRadius: '0.375rem',
  fontSize: '0.875rem',
  outline: 'none',
  color: 'var(--text-heading)',
}

const labelStyle: React.CSSProperties = {
  display: 'block',
  fontSize: '0.75rem',
  fontWeight: 700,
  color: 'var(--text-body)',
  marginBottom: '0.375rem',
  textTransform: 'uppercase' as const,
  letterSpacing: '0.05em',
}

function StatusBadge({ status }: { status: string }) {
  const map: Record<string, { bg: string; color: string }> = {
    CONFIRMED:  { bg: '#ecfdf5', color: '#065f46' },
    PENDING:    { bg: '#fffbeb', color: '#92400e' },
    CANCELLED:  { bg: '#fef2f2', color: '#991b1b' },
  }
  const s = map[status] ?? { bg: '#f1f5f9', color: '#475569' }
  return (
    <span
      className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider"
      style={{ background: s.bg, color: s.color }}
    >
      {status}
    </span>
  )
}

export function ReturnPage() {
  const userId = useSelector((s: RootState) => s.auth.user?.id ?? '')

  const [saleIdInput, setSaleIdInput] = useState('')
  const [sale, setSale] = useState<Sale | null>(null)
  const [loadError, setLoadError] = useState('')
  const [selectedItem, setSelectedItem] = useState<SaleItem | null>(null)
  const [returnQty, setReturnQty] = useState(1)
  const [reason, setReason] = useState('')
  const [submitError, setSubmitError] = useState('')
  const [success, setSuccess] = useState(false)

  const { data: productsData } = useQuery<{ products: Product[] }>(PRODUCTS_QUERY, {
    client: inventoryClient,
  })
  const productMap = Object.fromEntries(
    (productsData?.products ?? []).map((p) => [p.id, p.name]),
  )

  const [executeGetSale, { loading: saleLoading }] = useLazyQuery<{ sale: Sale }>(GET_SALE_QUERY, {
    client: salesClient,
    fetchPolicy: 'network-only',
  })

  const [processReturn, { loading: submitting }] = useMutation(PROCESS_RETURN_MUTATION, {
    client: salesClient,
  })

  async function handleLookup(e: React.FormEvent) {
    e.preventDefault()
    if (!saleIdInput.trim()) return
    setLoadError('')
    setSale(null)
    setSelectedItem(null)
    setSuccess(false)
    try {
      const result = await executeGetSale({ variables: { id: saleIdInput.trim() } })
      if (result.data?.sale) {
        setSale(result.data.sale)
      } else {
        setLoadError('Sale not found. Check the ID and try again.')
      }
    } catch (err: unknown) {
      setLoadError(err instanceof Error ? err.message : 'Failed to load sale.')
    }
  }

  function handleSelectItem(item: SaleItem) {
    setSelectedItem(item)
    setReturnQty(1)
    setReason('')
    setSubmitError('')
  }

  function handleCancelSelection() {
    setSelectedItem(null)
    setSubmitError('')
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!sale || !selectedItem) return
    setSubmitError('')
    try {
      await processReturn({
        variables: {
          input: {
            saleId: sale.id,
            productId: selectedItem.productId,
            batchId: selectedItem.batchId,
            userId,
            responsiblePharmacistId: null,
            quantity: returnQty,
            reason,
          },
        },
      })
      setSuccess(true)
      setSelectedItem(null)
      setSale(null)
      setSaleIdInput('')
    } catch (err: unknown) {
      setSubmitError(err instanceof Error ? err.message : 'Return failed. Please try again.')
    }
  }

  const productName = (id: string) => productMap[id] ?? id.slice(0, 8).toUpperCase()

  return (
    <div className="p-8 max-w-[900px] mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold tracking-tight" style={{ color: 'var(--text-heading)' }}>
          Process Return
        </h1>
        <p className="text-sm font-medium mt-1" style={{ color: 'var(--text-body)' }}>
          Look up a sale by ID to register a product return.
        </p>
      </div>

      {/* Success banner */}
      {success && (
        <div
          className="flex items-center gap-3 px-4 py-3 rounded-lg mb-6 text-sm font-bold"
          style={{ background: '#ecfdf5', border: '1px solid #6ee7b7', color: '#065f46' }}
        >
          <CheckCircle size={18} />
          Return registered successfully. Inventory will be updated when the Kafka event is processed (Phase 3).
          <button
            onClick={() => setSuccess(false)}
            className="ml-auto text-green-600 hover:text-green-800"
          >
            ✕
          </button>
        </div>
      )}

      {/* Sale Lookup */}
      <div className="p-6 mb-4" style={cardStyle}>
        <h2 className="text-sm font-bold mb-4" style={{ color: 'var(--text-heading)' }}>
          Step 1 — Find the original sale
        </h2>
        <form onSubmit={handleLookup} className="flex gap-3">
          <div className="flex-1">
            <label style={labelStyle}>Sale ID</label>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={15} />
              <input
                style={{ ...inputStyle, paddingLeft: '2.25rem' }}
                value={saleIdInput}
                onChange={(e) => setSaleIdInput(e.target.value)}
                placeholder="Paste the sale UUID here…"
                spellCheck={false}
              />
            </div>
          </div>
          <div className="flex items-end">
            <button
              type="submit"
              disabled={saleLoading || !saleIdInput.trim()}
              className="px-5 py-2 text-sm font-bold text-white rounded-md disabled:opacity-50 transition-colors"
              style={{ background: 'var(--blue-600)' }}
              onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-700)')}
              onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-600)')}
            >
              {saleLoading ? 'Loading…' : 'Look Up'}
            </button>
          </div>
        </form>

        {loadError && (
          <div className="flex items-center gap-2 mt-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
            <AlertCircle size={15} /> {loadError}
          </div>
        )}
      </div>

      {/* Sale details + items */}
      {sale && (
        <div className="mb-4" style={cardStyle}>
          {/* Sale summary */}
          <div
            className="px-6 py-4 flex items-center gap-6"
            style={{ borderBottom: '1px solid var(--card-border)', background: '#f8fafc' }}
          >
            <div>
              <p style={labelStyle}>Sale ID</p>
              <p className="font-mono text-xs font-bold text-slate-700">{sale.id.slice(0, 16).toUpperCase()}…</p>
            </div>
            <div>
              <p style={labelStyle}>Status</p>
              <StatusBadge status={sale.status} />
            </div>
            <div>
              <p style={labelStyle}>Total</p>
              <p className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>
                R$ {parseFloat(sale.total).toFixed(2)}
              </p>
            </div>
            <div>
              <p style={labelStyle}>Date</p>
              <p className="text-sm font-medium text-slate-600">
                {new Date(sale.createdAt).toLocaleDateString('en-GB')}
              </p>
            </div>
          </div>

          {/* Items table */}
          <div>
            <div className="px-6 py-3" style={{ borderBottom: '1px solid var(--card-border)' }}>
              <h2 className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>
                Step 2 — Select the item to return
              </h2>
            </div>
            <table className="w-full text-left border-collapse">
              <thead>
                <tr style={{ background: '#f8fafc' }}>
                  <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Product</th>
                  <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-40">Batch ID</th>
                  <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-24 text-right">Qty Sold</th>
                  <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-28 text-right">Unit Price</th>
                  <th className="px-6 py-3 w-24" />
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {sale.items.map((item) => {
                  const isSelected = selectedItem?.id === item.id
                  return (
                    <tr
                      key={item.id}
                      className="transition-colors"
                      style={{ background: isSelected ? '#eff6ff' : undefined }}
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <Package size={15} className="text-slate-400 shrink-0" />
                          <p className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>
                            {productName(item.productId)}
                          </p>
                        </div>
                      </td>
                      <td className="px-6 py-4 font-mono text-xs font-bold text-slate-500">
                        {item.batchId.slice(0, 8).toUpperCase()}…
                      </td>
                      <td className="px-6 py-4 text-sm font-bold text-right text-slate-700">
                        {item.quantity}
                      </td>
                      <td className="px-6 py-4 text-sm font-bold text-right" style={{ color: 'var(--text-heading)' }}>
                        R$ {parseFloat(item.unitPrice).toFixed(2)}
                      </td>
                      <td className="px-6 py-4 text-right">
                        {isSelected ? (
                          <span className="text-[10px] font-bold text-blue-600 uppercase tracking-wider">Selected</span>
                        ) : (
                          <button
                            onClick={() => handleSelectItem(item)}
                            className="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold rounded-md border transition-colors"
                            style={{ borderColor: 'var(--blue-500)', color: 'var(--blue-600)' }}
                            onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = '#eff6ff')}
                            onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'transparent')}
                          >
                            Select <ChevronRight size={12} />
                          </button>
                        )}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Return form */}
      {selectedItem && (
        <div style={cardStyle}>
          <div className="px-6 py-4" style={{ borderBottom: '1px solid var(--card-border)', background: '#f8fafc' }}>
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>
                  Step 3 — Return details
                </h2>
                <p className="text-xs font-medium text-slate-500 mt-0.5">
                  Returning: <strong>{productName(selectedItem.productId)}</strong> — Batch{' '}
                  <span className="font-mono">{selectedItem.batchId.slice(0, 8).toUpperCase()}…</span>
                </p>
              </div>
              <button
                onClick={handleCancelSelection}
                className="text-xs font-bold text-slate-400 hover:text-slate-600 transition-colors"
              >
                Change item
              </button>
            </div>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            {submitError && (
              <div className="flex items-center gap-2 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
                <AlertCircle size={15} /> {submitError}
              </div>
            )}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label style={labelStyle}>
                  Quantity to return (max: {selectedItem.quantity})
                </label>
                <input
                  type="number"
                  min={1}
                  max={selectedItem.quantity}
                  style={inputStyle}
                  value={returnQty}
                  onChange={(e) => setReturnQty(parseInt(e.target.value) || 1)}
                  required
                />
              </div>
              <div className="flex items-end">
                <div
                  className="w-full px-4 py-2 rounded-lg text-sm"
                  style={{ background: '#fffbeb', border: '1px solid #fde68a' }}
                >
                  <p className="text-[10px] font-bold text-amber-700 uppercase tracking-wider mb-0.5">Refund estimate</p>
                  <p className="text-base font-bold text-amber-900">
                    R$ {(returnQty * parseFloat(selectedItem.unitPrice)).toFixed(2)}
                  </p>
                </div>
              </div>
            </div>
            <div>
              <label style={labelStyle}>Reason for return</label>
              <textarea
                style={{ ...inputStyle, resize: 'vertical', minHeight: '80px' }}
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="e.g. Customer received wrong product, damaged packaging, expired lot…"
                required
              />
            </div>
            <div
              className="flex items-center justify-end gap-3 pt-2"
              style={{ borderTop: '1px solid var(--card-border)' }}
            >
              <button
                type="button"
                onClick={handleCancelSelection}
                className="px-4 py-2 text-sm font-bold rounded-md text-slate-600 hover:bg-slate-100 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting}
                className="flex items-center gap-2 px-6 py-2 text-sm font-bold rounded-md text-white disabled:opacity-60 transition-colors"
                style={{ background: 'var(--blue-600)' }}
                onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-700)')}
                onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-600)')}
              >
                <RotateCcw size={15} />
                {submitting ? 'Processing…' : 'Confirm Return'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Empty state — no sale loaded yet */}
      {!sale && !saleLoading && !loadError && (
        <div
          className="flex flex-col items-center justify-center py-16 rounded-xl"
          style={{ border: '2px dashed var(--card-border)' }}
        >
          <div className="w-14 h-14 rounded-full bg-slate-100 flex items-center justify-center mb-3">
            <RotateCcw size={24} className="text-slate-300" />
          </div>
          <p className="text-sm font-bold text-slate-500">No sale loaded</p>
          <p className="text-xs text-slate-400 mt-1">Enter a sale ID above to get started.</p>
        </div>
      )}
    </div>
  )
}
