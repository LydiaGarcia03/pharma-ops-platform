import { useState } from 'react'
import { useQuery, useMutation } from '@apollo/client/react'
import * as Tabs from '@radix-ui/react-tabs'
import * as Dialog from '@radix-ui/react-dialog'
import {
  Search,
  Filter,
  Plus,
  Upload,
  Download,
  Lock,
  AlertCircle,
  Package,
  Layers,
  ArrowLeftRight,
  X,
} from 'lucide-react'
import { inventoryClient } from '@/graphql/apolloClients'
import { PRODUCTS_QUERY } from '@/graphql/queries'
import { CREATE_PRODUCT_MUTATION } from '@/graphql/mutations'

interface Product {
  id: string
  name: string
  barcode: string
  controlled: boolean
  salePrice: string
  active: boolean
}

type StockStatus = 'Healthy' | 'Low Stock' | 'Depleted' | 'Inactive'

function getStatus(product: Product): StockStatus {
  if (!product.active) return 'Inactive'
  return 'Healthy'
}

function StatusBadge({ status }: { status: StockStatus }) {
  const styles: Record<StockStatus, { bg: string; color: string }> = {
    Healthy:    { bg: '#ecfdf5', color: '#065f46' },
    'Low Stock':{ bg: '#fffbeb', color: '#92400e' },
    Depleted:   { bg: '#fef2f2', color: '#991b1b' },
    Inactive:   { bg: '#f1f5f9', color: '#475569' },
  }
  const s = styles[status]
  return (
    <span
      className="inline-flex items-center gap-1.5 px-2 py-1 rounded-md text-[10px] font-bold uppercase tracking-wider"
      style={{ background: s.bg, color: s.color, border: `1px solid ${s.bg}` }}
    >
      {(status === 'Low Stock' || status === 'Depleted') && <AlertCircle size={10} />}
      {status}
    </span>
  )
}

function NewProductModal({ onCreated }: { onCreated: () => void }) {
  const [open, setOpen] = useState(false)
  const [name, setName] = useState('')
  const [barcode, setBarcode] = useState('')
  const [controlled, setControlled] = useState(false)
  const [salePrice, setSalePrice] = useState('')
  const [error, setError] = useState('')

  const [createProduct, { loading }] = useMutation(CREATE_PRODUCT_MUTATION, {
    client: inventoryClient,
  })

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    try {
      await createProduct({
        variables: { input: { name, barcode, controlled, salePrice } },
      })
      setOpen(false)
      setName(''); setBarcode(''); setControlled(false); setSalePrice('')
      onCreated()
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to create product')
    }
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
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
  }

  return (
    <Dialog.Root open={open} onOpenChange={setOpen}>
      <Dialog.Trigger asChild>
        <button
          className="flex items-center gap-2 px-4 py-2 text-white font-bold text-sm rounded-md shadow-sm transition-colors"
          style={{ background: 'var(--blue-600)' }}
          onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-700)')}
          onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.background = 'var(--blue-600)')}
        >
          <Plus size={16} /> New Product
        </button>
      </Dialog.Trigger>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 bg-black/40 z-40" />
        <Dialog.Content
          className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-xl shadow-2xl w-[480px] z-50 overflow-hidden"
          style={{ border: '1px solid var(--card-border)' }}
        >
          <div
            className="px-6 py-4 flex items-center justify-between"
            style={{ borderBottom: '1px solid var(--card-border)' }}
          >
            <Dialog.Title className="text-base font-bold" style={{ color: 'var(--text-heading)' }}>
              New Product
            </Dialog.Title>
            <Dialog.Close asChild>
              <button className="p-1 rounded text-slate-400 hover:text-slate-600 hover:bg-slate-100">
                <X size={18} />
              </button>
            </Dialog.Close>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            {error && (
              <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
                {error}
              </div>
            )}
            <div>
              <label style={labelStyle}>Product Name</label>
              <input
                style={inputStyle}
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Amoxicillin 500mg Caps"
                required
              />
            </div>
            <div>
              <label style={labelStyle}>Barcode</label>
              <input
                style={inputStyle}
                value={barcode}
                onChange={(e) => setBarcode(e.target.value)}
                placeholder="e.g. 7891234567890"
                required
              />
            </div>
            <div>
              <label style={labelStyle}>Sale Price (R$)</label>
              <input
                style={inputStyle}
                value={salePrice}
                onChange={(e) => setSalePrice(e.target.value)}
                placeholder="e.g. 15.99"
                required
              />
            </div>
            <div className="flex items-center justify-between">
              <div>
                <p style={{ ...labelStyle, marginBottom: 0 }}>Controlled Substance (SNGPC)</p>
                <p className="text-xs mt-0.5" style={{ color: 'var(--text-muted)' }}>
                  Requires pharmacist authorization
                </p>
              </div>
              <button
                type="button"
                onClick={() => setControlled(!controlled)}
                className="relative w-11 h-6 rounded-full transition-colors"
                style={{ background: controlled ? 'var(--purple-600)' : '#cbd5e1' }}
              >
                <span
                  className="absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform"
                  style={{ transform: controlled ? 'translateX(20px)' : 'translateX(0)' }}
                />
              </button>
            </div>
            <div
              className="flex justify-end gap-3 pt-2"
              style={{ borderTop: '1px solid var(--card-border)' }}
            >
              <Dialog.Close asChild>
                <button
                  type="button"
                  className="px-4 py-2 text-sm font-bold rounded-md text-slate-600 hover:bg-slate-100 transition-colors"
                >
                  Cancel
                </button>
              </Dialog.Close>
              <button
                type="submit"
                disabled={loading}
                className="px-6 py-2 text-sm font-bold rounded-md text-white disabled:opacity-60 transition-colors"
                style={{ background: 'var(--blue-600)' }}
              >
                {loading ? 'Saving…' : 'Save Product'}
              </button>
            </div>
          </form>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}

export function InventoryPage() {
  const [search, setSearch] = useState('')
  const { data, loading, error, refetch } = useQuery<{ products: Product[] }>(PRODUCTS_QUERY, {
    client: inventoryClient,
  })

  const products = data?.products ?? []
  const filtered = products.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.barcode.toLowerCase().includes(search.toLowerCase()),
  )

  const tabTriggerClass =
    'flex items-center gap-2 pb-3 pt-2 text-sm font-bold text-slate-500 hover:text-slate-800 data-[state=active]:text-blue-600 data-[state=active]:border-b-2 data-[state=active]:border-blue-600 transition-colors cursor-pointer outline-none'

  const cardStyle: React.CSSProperties = {
    background: 'var(--card-bg)',
    border: '1px solid var(--card-border)',
    boxShadow: 'var(--card-shadow)',
    borderRadius: 'var(--card-radius)',
  }

  return (
    <div className="h-[calc(100vh-4rem)] p-8 max-w-[1600px] mx-auto flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between mb-6 shrink-0">
        <div>
          <h1 className="text-2xl font-bold tracking-tight" style={{ color: 'var(--text-heading)' }}>
            Inventory Control
          </h1>
          <p className="text-sm font-medium mt-1" style={{ color: 'var(--text-body)' }}>
            Manage catalog, track FEFO batches, and coordinate inter-store transfers.
          </p>
        </div>
        <div className="flex gap-2 items-center">
          <button
            className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 text-slate-600 font-bold text-sm rounded-md shadow-sm hover:bg-slate-50 transition-colors"
          >
            <Upload size={16} /> Import
          </button>
          <button
            className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 text-slate-600 font-bold text-sm rounded-md shadow-sm hover:bg-slate-50 transition-colors"
          >
            <Download size={16} /> Export
          </button>
          <div className="w-px h-8 bg-slate-200 mx-1" />
          <NewProductModal onCreated={() => refetch()} />
        </div>
      </div>

      {/* Tabs */}
      <Tabs.Root defaultValue="catalog" className="flex-1 flex flex-col overflow-hidden min-h-0" style={cardStyle}>
        <div className="border-b border-slate-200 px-6 pt-2 bg-slate-50/50 shrink-0">
          <Tabs.List className="flex gap-8">
            <Tabs.Trigger value="catalog" className={tabTriggerClass}>
              <Package size={16} /> Master Catalog
            </Tabs.Trigger>
            <Tabs.Trigger value="batches" className={tabTriggerClass}>
              <Layers size={16} /> Lot / Batch Tracking (FEFO)
            </Tabs.Trigger>
            <Tabs.Trigger value="transfers" className={tabTriggerClass}>
              <ArrowLeftRight size={16} /> Pending Transfers
              <span className="bg-blue-100 text-blue-700 text-[10px] px-1.5 py-0.5 rounded-full ml-1">2</span>
            </Tabs.Trigger>
          </Tabs.List>
        </div>

        {/* Master Catalog */}
        <Tabs.Content value="catalog" className="flex-1 flex flex-col overflow-hidden outline-none">
          <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-white shrink-0">
            <div className="flex gap-3">
              <div className="relative w-80">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                <input
                  type="text"
                  placeholder="Search by ID, name, or barcode..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="w-full pl-9 pr-4 py-1.5 bg-slate-50 border border-slate-200 rounded-md text-sm font-medium focus:outline-none focus:ring-2 focus:bg-white transition-all"
                  style={{ '--tw-ring-color': 'var(--blue-500)' } as React.CSSProperties}
                />
              </div>
              <button className="flex items-center gap-2 px-3 py-1.5 bg-white border border-slate-200 text-slate-600 text-sm font-bold rounded-md hover:bg-slate-50">
                <Filter size={14} /> Filter
              </button>
            </div>
            <div className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Showing {filtered.length} items
            </div>
          </div>

          <div className="flex-1 overflow-auto bg-white">
            {loading && (
              <div className="p-8 space-y-3">
                {[...Array(5)].map((_, i) => (
                  <div key={i} className="h-12 bg-slate-100 rounded animate-pulse" />
                ))}
              </div>
            )}
            {error && (
              <div className="p-8 text-center">
                <AlertCircle className="mx-auto mb-2 text-red-400" size={32} />
                <p className="text-sm font-semibold text-red-600">Failed to load products</p>
                <p className="text-xs text-slate-500 mt-1">
                  Make sure the inventory-service is running on port 8082.
                </p>
              </div>
            )}
            {!loading && !error && (
              <table className="w-full text-left border-collapse whitespace-nowrap">
                <thead className="bg-slate-50 sticky top-0 z-10 shadow-[0_1px_0_#e2e8f0]">
                  <tr>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-36">Product ID</th>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Name & Details</th>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-32">Classification</th>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-28 text-right">Price</th>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-32">Status</th>
                    <th className="px-6 py-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider w-16" />
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {filtered.map((product) => (
                    <tr key={product.id} className="hover:bg-slate-50/80 transition-colors group cursor-pointer">
                      <td className="px-6 py-4 text-xs font-mono font-bold text-slate-500">
                        {product.id.slice(0, 8).toUpperCase()}
                      </td>
                      <td className="px-6 py-4">
                        <p className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>{product.name}</p>
                        <p className="text-xs font-medium text-slate-500">{product.barcode}</p>
                      </td>
                      <td className="px-6 py-4">
                        {product.controlled ? (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-purple-100 text-purple-700 border border-purple-200">
                            <Lock size={10} /> SNGPC
                          </span>
                        ) : (
                          <span className="text-xs text-slate-400 font-medium">—</span>
                        )}
                      </td>
                      <td className="px-6 py-4 text-sm font-bold text-right" style={{ color: 'var(--text-heading)' }}>
                        R$ {parseFloat(product.salePrice).toFixed(2)}
                      </td>
                      <td className="px-6 py-4">
                        <StatusBadge status={getStatus(product)} />
                      </td>
                      <td className="px-6 py-4" />
                    </tr>
                  ))}
                  {filtered.length === 0 && !loading && (
                    <tr>
                      <td colSpan={6} className="px-6 py-12 text-center text-sm text-slate-400">
                        No products found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            )}
          </div>
        </Tabs.Content>

        {/* Batch Tracking */}
        <Tabs.Content value="batches" className="flex-1 flex flex-col overflow-hidden outline-none">
          <div className="p-4 bg-amber-50/60 border-b border-amber-100 flex items-start gap-3 shrink-0">
            <div className="bg-amber-100 p-1.5 rounded text-amber-600 shrink-0">
              <AlertCircle size={16} />
            </div>
            <div>
              <h3 className="text-sm font-bold text-amber-800">FEFO Policy Enforcement Active</h3>
              <p className="text-xs font-medium text-amber-700 mt-0.5">
                Checkout terminals will automatically deduct from the batch closest to expiration.
              </p>
            </div>
          </div>
          <div className="flex-1 flex flex-col items-center justify-center gap-4 p-8">
            <div className="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center">
              <Layers size={28} className="text-slate-300" />
            </div>
            <div className="text-center max-w-sm">
              <p className="text-sm font-bold text-slate-600">Batch data not yet available</p>
              <p className="text-xs text-slate-400 mt-1">
                The <code className="bg-slate-100 px-1 rounded">batches</code> GraphQL query will be added
                when batch management is fully integrated in Phase 2 completion.
              </p>
            </div>
          </div>
        </Tabs.Content>

        {/* Transfers */}
        <Tabs.Content value="transfers" className="flex-1 flex flex-col items-center justify-center gap-4 p-8 outline-none">
          <div className="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center">
            <ArrowLeftRight size={28} className="text-slate-300" />
          </div>
          <div className="text-center max-w-sm">
            <p className="text-sm font-bold text-slate-600">Inter-store transfers</p>
            <p className="text-xs text-slate-400 mt-1">
              Available in <strong>Phase 8 — Extras</strong>. The backend and UI for inventory transfers
              between stores will be implemented after the core transactional features are complete.
            </p>
          </div>
        </Tabs.Content>
      </Tabs.Root>
    </div>
  )
}
