import { useState } from 'react'
import { useQuery, useMutation } from '@apollo/client/react'
import { useSelector } from 'react-redux'
import * as Dialog from '@radix-ui/react-dialog'
import {
  Barcode,
  ShoppingCart,
  Trash2,
  Lock,
  ArrowRight,
  Stethoscope,
  AlertCircle,
  CheckCircle,
  X,
} from 'lucide-react'
import type { RootState } from '@/app/store'
import { inventoryClient, identityClient, salesClient } from '@/graphql/apolloClients'
import { PRODUCTS_QUERY, STORES_QUERY } from '@/graphql/queries'
import { CREATE_SALE_MUTATION } from '@/graphql/mutations'

interface Product {
  id: string
  name: string
  barcode: string
  controlled: boolean
  salePrice: string
  active: boolean
}

interface CartItem {
  product: Product
  qty: number
}

const categories = ['All', 'Antibiotics', 'Pain Relief', 'Cardiovascular', 'Psychiatric', 'Vitamins']

export function PointOfSalePage() {
  const [cart, setCart] = useState<CartItem[]>([])
  const [search, setSearch] = useState('')
  const [activeCategory, setActiveCategory] = useState('All')
  const [authOpen, setAuthOpen] = useState(false)
  const [pharmacistId, setPharmacistId] = useState('')
  const [authPin, setAuthPin] = useState('')
  const [saleError, setSaleError] = useState<string | null>(null)
  const [saleSuccess, setSaleSuccess] = useState(false)

  const user = useSelector((s: RootState) => s.auth.user)

  const { data, loading, error } = useQuery<{ products: Product[] }>(PRODUCTS_QUERY, {
    client: inventoryClient,
  })

  const { data: storesData } = useQuery<{ stores: { id: string; name: string; active: boolean }[] }>(
    STORES_QUERY,
    { client: identityClient },
  )
  const storeId = storesData?.stores.find((s) => s.active)?.id ?? null

  const [createSale, { loading: saleLoading }] = useMutation(CREATE_SALE_MUTATION, {
    client: salesClient,
  })

  const products = (data?.products ?? []).filter((p) => p.active)

  const filtered = products.filter((p) => {
    const matchSearch =
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.barcode.toLowerCase().includes(search.toLowerCase())
    return matchSearch
  })

  function addToCart(product: Product) {
    setCart((prev) => {
      const existing = prev.find((i) => i.product.id === product.id)
      if (existing) return prev.map((i) => (i.product.id === product.id ? { ...i, qty: i.qty + 1 } : i))
      return [...prev, { product, qty: 1 }]
    })
  }

  function updateQty(id: string, delta: number) {
    setCart((prev) =>
      prev
        .map((i) => (i.product.id === id ? { ...i, qty: i.qty + delta } : i))
        .filter((i) => i.qty > 0),
    )
  }

  function removeFromCart(id: string) {
    setCart((prev) => prev.filter((i) => i.product.id !== id))
  }

  const subtotal = cart.reduce((acc, i) => acc + parseFloat(i.product.salePrice) * i.qty, 0)
  const tax = subtotal * 0.05
  const total = subtotal + tax

  const hasControlled = cart.some((i) => i.product.controlled)

  async function processSale(responsiblePharmacistId?: string) {
    if (!user?.id || !storeId) {
      setSaleError('No store available. Make sure at least one store is registered.')
      return
    }
    setSaleError(null)
    try {
      await createSale({
        variables: {
          input: {
            storeId,
            userId: user.id,
            responsiblePharmacistId: responsiblePharmacistId ?? null,
            forced: false,
            items: cart.map((i) => ({
              productId: i.product.id,
              controlled: i.product.controlled,
              quantity: i.qty,
              unitPrice: i.product.salePrice,
            })),
          },
        },
      })
      setCart([])
      setSaleSuccess(true)
      setTimeout(() => setSaleSuccess(false), 4000)
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : 'Failed to process sale.'
      setSaleError(msg)
    }
  }

  function handlePay() {
    setSaleError(null)
    if (hasControlled) {
      setAuthOpen(true)
    } else {
      processSale()
    }
  }

  async function handleAuthorize() {
    // Use the current user's ID as the responsible pharmacist for now.
    // In Phase 4, this will validate against a real pharmacist credential.
    await processSale(user?.id)
    setAuthOpen(false)
    setPharmacistId('')
    setAuthPin('')
  }

  const cardStyle: React.CSSProperties = {
    background: 'var(--card-bg)',
    border: '1px solid var(--card-border)',
    borderRadius: 'var(--card-radius)',
  }

  return (
    <div className="h-[calc(100vh-4rem)] flex overflow-hidden" style={{ background: 'var(--page-bg)' }}>
      {/* ── Left: Catalog ── */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <div className="p-6 pb-3 shrink-0">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-xl font-bold tracking-tight" style={{ color: 'var(--text-heading)' }}>
              Point of Sale
            </h1>
            <button
              className="px-3 py-1.5 bg-white border border-slate-200 rounded-md text-xs font-bold text-slate-600 flex items-center gap-1.5 shadow-sm hover:bg-slate-50 cursor-not-allowed opacity-60"
              title="Prescriptions — Phase 9"
            >
              <Stethoscope size={14} /> Add Prescription
            </button>
          </div>
          <div className="relative">
            <Barcode className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input
              type="text"
              placeholder="Scan barcode or search by name, SKU..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm font-medium focus:outline-none focus:ring-2 shadow-sm transition-all"
              style={{ '--tw-ring-color': 'var(--blue-500)' } as React.CSSProperties}
            />
          </div>
        </div>

        {/* Category pills */}
        <div className="px-6 pb-3 flex gap-2 overflow-x-auto shrink-0" style={{ scrollbarWidth: 'none' }}>
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className="px-4 py-1.5 rounded-full text-xs font-bold whitespace-nowrap transition-colors border"
              style={
                activeCategory === cat
                  ? { background: 'var(--text-heading)', color: '#fff', border: '1px solid var(--text-heading)' }
                  : { background: '#fff', color: 'var(--text-body)', border: '1px solid var(--card-border)' }
              }
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Product grid */}
        <div className="flex-1 overflow-auto px-6 pb-6">
          {loading && (
            <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
              {[...Array(8)].map((_, i) => (
                <div key={i} className="h-36 bg-slate-100 rounded-xl animate-pulse" />
              ))}
            </div>
          )}
          {error && (
            <div className="flex flex-col items-center justify-center h-48 gap-3">
              <AlertCircle size={32} className="text-red-400" />
              <p className="text-sm font-semibold text-red-600">Failed to load products</p>
              <p className="text-xs text-slate-400">Make sure the inventory-service is running.</p>
            </div>
          )}
          {!loading && !error && (
            <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4 auto-rows-max">
              {filtered.map((product) => {
                const inCart = cart.find((i) => i.product.id === product.id)
                return (
                  <div
                    key={product.id}
                    onClick={() => addToCart(product)}
                    className="bg-white border rounded-xl p-4 cursor-pointer transition-all flex flex-col h-[140px] relative select-none"
                    style={
                      inCart
                        ? { border: '2px solid var(--blue-500)', boxShadow: '0 0 0 3px rgba(59,130,246,0.1)' }
                        : { border: '1px solid var(--card-border)' }
                    }
                    onMouseEnter={(e) => {
                      if (!inCart) (e.currentTarget as HTMLElement).style.border = '1px solid var(--blue-500)'
                    }}
                    onMouseLeave={(e) => {
                      if (!inCart) (e.currentTarget as HTMLElement).style.border = '1px solid var(--card-border)'
                    }}
                  >
                    <div className="flex justify-between items-start mb-2">
                      <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                        {product.barcode.slice(-6)}
                      </span>
                      {product.controlled && (
                        <span className="text-[10px] font-bold bg-purple-100 text-purple-700 px-1.5 rounded flex items-center gap-0.5">
                          <Lock size={10} /> SNGPC
                        </span>
                      )}
                    </div>
                    <h3 className="font-bold text-sm leading-tight line-clamp-2" style={{ color: 'var(--text-heading)' }}>
                      {product.name}
                    </h3>
                    <div className="flex items-end justify-between mt-auto">
                      <span className="text-lg font-black tracking-tight" style={{ color: 'var(--text-heading)' }}>
                        R$ {parseFloat(product.salePrice).toFixed(2)}
                      </span>
                      {inCart && (
                        <span
                          className="text-[10px] font-bold px-2 py-0.5 rounded-full text-white"
                          style={{ background: 'var(--blue-500)' }}
                        >
                          {inCart.qty} in cart
                        </span>
                      )}
                    </div>
                  </div>
                )
              })}
              {filtered.length === 0 && (
                <div className="col-span-4 py-12 text-center text-sm text-slate-400">
                  No products found.
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* ── Right: Cart ── */}
      <div
        className="w-[380px] flex flex-col shrink-0"
        style={{ background: 'var(--card-bg)', borderLeft: '1px solid var(--card-border)' }}
      >
        <div
          className="h-16 flex items-center justify-between px-5 shrink-0"
          style={{ borderBottom: '1px solid var(--card-border)', background: '#f8fafc' }}
        >
          <div className="flex items-center gap-2 font-bold" style={{ color: 'var(--text-heading)' }}>
            <ShoppingCart size={18} className="text-slate-500" />
            Current Order
          </div>
          <button
            className="text-xs font-bold transition-colors"
            style={{ color: 'var(--red-500)' }}
            onClick={() => setCart([])}
          >
            Clear
          </button>
        </div>

        <div className="flex-1 overflow-auto p-4 flex flex-col gap-2">
          {cart.length === 0 ? (
            <div className="flex-1 flex flex-col items-center justify-center gap-3 text-slate-300">
              <div className="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center border border-slate-100">
                <ShoppingCart size={24} className="text-slate-300" />
              </div>
              <p className="text-sm font-bold">Scan items to start</p>
            </div>
          ) : (
            cart.map((item) => (
              <div
                key={item.product.id}
                className="group flex items-start gap-3 p-3 rounded-lg border transition-colors shadow-sm"
                style={{ background: '#fff', border: '1px solid var(--card-border)' }}
              >
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-1.5 mb-1">
                    <p className="text-sm font-bold truncate" style={{ color: 'var(--text-heading)' }}>
                      {item.product.name}
                    </p>
                    {item.product.controlled && (
                      <Lock size={12} style={{ color: 'var(--purple-600)', flexShrink: 0 }} />
                    )}
                  </div>
                  <p className="text-xs text-slate-500">
                    R$ {parseFloat(item.product.salePrice).toFixed(2)} / un
                  </p>
                </div>
                <div className="flex flex-col items-end gap-2">
                  <p className="text-sm font-black" style={{ color: 'var(--text-heading)' }}>
                    R$ {(parseFloat(item.product.salePrice) * item.qty).toFixed(2)}
                  </p>
                  <div className="flex items-center bg-slate-50 border border-slate-200 rounded">
                    <button
                      className="w-6 h-6 flex items-center justify-center text-slate-500 hover:bg-slate-200 font-bold text-sm rounded-l transition-colors"
                      onClick={() => updateQty(item.product.id, -1)}
                    >
                      −
                    </button>
                    <span className="w-6 text-center text-xs font-bold text-slate-700">{item.qty}</span>
                    <button
                      className="w-6 h-6 flex items-center justify-center text-slate-500 hover:bg-slate-200 font-bold text-sm rounded-r transition-colors"
                      onClick={() => updateQty(item.product.id, 1)}
                    >
                      +
                    </button>
                  </div>
                </div>
                <button
                  onClick={() => removeFromCart(item.product.id)}
                  className="mt-1 text-slate-300 hover:text-red-500 transition-colors opacity-0 group-hover:opacity-100"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            ))
          )}
        </div>

        {/* Totals */}
        <div className="p-5 space-y-4 shrink-0" style={{ borderTop: '1px solid var(--card-border)', background: '#f8fafc' }}>
          {saleSuccess && (
            <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold rounded-md px-3 py-2">
              <CheckCircle size={14} className="shrink-0" /> Sale confirmed successfully!
            </div>
          )}
          {saleError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs font-semibold rounded-md px-3 py-2">
              <AlertCircle size={14} className="shrink-0 mt-0.5" /> {saleError}
            </div>
          )}
          {hasControlled && (
            <div className="border rounded-md p-2.5 flex items-start gap-2 bg-purple-50 border-purple-200">
              <Lock size={14} className="mt-0.5 text-purple-600 shrink-0" />
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-purple-800">
                  SNGPC Authorization Required
                </p>
                <p className="text-xs mt-0.5 text-purple-600">
                  Pharmacist credential required at checkout.
                </p>
              </div>
            </div>
          )}
          <div className="space-y-2">
            <div className="flex justify-between text-sm font-medium text-slate-500">
              <span>Subtotal</span>
              <span>R$ {subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-sm font-medium text-slate-500">
              <span>Tax (5%)</span>
              <span>R$ {tax.toFixed(2)}</span>
            </div>
            <div
              className="flex justify-between items-end pt-3 mt-2"
              style={{ borderTop: '1px solid var(--card-border)' }}
            >
              <span className="text-sm font-bold" style={{ color: 'var(--text-heading)' }}>Total Due</span>
              <span className="text-3xl font-black tracking-tight" style={{ color: 'var(--text-heading)' }}>
                R$ {total.toFixed(2)}
              </span>
            </div>
          </div>
          <button
            disabled={cart.length === 0 || saleLoading}
            onClick={handlePay}
            className="w-full py-4 rounded-lg text-white font-bold text-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ background: 'var(--blue-600)' }}
          >
            {saleLoading ? 'Processing…' : <><span>Pay</span> <ArrowRight size={20} /></>}
          </button>
        </div>
      </div>

      {/* Pharmacist Auth Dialog */}
      <Dialog.Root open={authOpen} onOpenChange={setAuthOpen}>
        <Dialog.Portal>
          <Dialog.Overlay className="fixed inset-0 bg-black/40 z-40" />
          <Dialog.Content
            className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-xl shadow-2xl w-[420px] z-50 overflow-hidden"
            style={{ border: '1px solid var(--card-border)' }}
          >
            <div className="p-6 text-white" style={{ background: 'var(--purple-600)' }}>
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mb-4">
                <Lock size={24} />
              </div>
              <Dialog.Title className="text-xl font-bold">Pharmacist Authorization</Dialog.Title>
              <Dialog.Description className="text-purple-100 text-sm mt-1">
                SNGPC compliance check. Controlled substances detected.
              </Dialog.Description>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1.5 uppercase tracking-wider">
                  Pharmacist ID / Badge
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-2.5 bg-slate-50 border border-slate-300 rounded-md focus:outline-none text-sm font-medium"
                  placeholder="Scan badge or enter ID"
                  value={pharmacistId}
                  onChange={(e) => setPharmacistId(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1.5 uppercase tracking-wider">
                  Authorization PIN
                </label>
                <input
                  type="password"
                  className="w-full px-4 py-2.5 bg-slate-50 border border-slate-300 rounded-md focus:outline-none text-sm font-medium font-mono tracking-[0.3em]"
                  placeholder="••••"
                  autoFocus
                  value={authPin}
                  onChange={(e) => setAuthPin(e.target.value)}
                />
              </div>
            </div>
            <div
              className="p-4 bg-slate-50 flex justify-end gap-3"
              style={{ borderTop: '1px solid var(--card-border)' }}
            >
              <Dialog.Close asChild>
                <button className="px-4 py-2.5 rounded-md text-slate-600 hover:bg-slate-200 font-bold text-sm transition-colors">
                  Cancel Sale
                </button>
              </Dialog.Close>
              <button
                onClick={handleAuthorize}
                className="px-6 py-2.5 rounded-md text-white font-bold text-sm transition-colors shadow-sm"
                style={{ background: 'var(--purple-600)' }}
              >
                Authorize &amp; Continue
              </button>
            </div>
            <Dialog.Close asChild>
              <button className="absolute top-4 right-4 p-1 text-white/70 hover:text-white">
                <X size={18} />
              </button>
            </Dialog.Close>
          </Dialog.Content>
        </Dialog.Portal>
      </Dialog.Root>
    </div>
  )
}
