import { NavLink, useNavigate } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import {
  LayoutDashboard, Package, ShoppingCart, X, LogOut, ChevronRight, Pill,
} from 'lucide-react'
import type { RootState } from '@/app/store'
import { logout } from '@/features/auth/authSlice'

interface SidebarProps {
  open: boolean
  onClose: () => void
}

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/products/new', icon: Package, label: 'Cadastro de Produto' },
  { to: '/sales/new', icon: ShoppingCart, label: 'Nova Venda' },
]

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 group
  ${isActive
    ? 'bg-indigo-600 text-white shadow-sm'
    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
  }`

export function Sidebar({ open, onClose }: SidebarProps) {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  function handleLogout() {
    dispatch(logout())
    navigate('/login')
  }

  return (
    <>
      {/* Overlay mobile */}
      {open && (
        <div
          className="fixed inset-0 bg-black/40 z-20 md:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 h-full w-64 bg-white border-r border-slate-200 z-30 flex flex-col
          transition-transform duration-300 ease-in-out
          ${open ? 'translate-x-0' : '-translate-x-full'}
          md:translate-x-0 md:static md:z-auto
        `}
      >
        {/* Logo */}
        <div className="flex items-center justify-between px-5 py-5 border-b border-slate-100">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <Pill className="w-4 h-4 text-white" />
            </div>
            <span className="font-bold text-slate-900 text-base leading-tight">
              PharmaOps
            </span>
          </div>
          <button onClick={onClose} className="md:hidden p-1 rounded hover:bg-slate-100 text-slate-500">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink key={to} to={to} className={linkClass} onClick={onClose}>
              <Icon className="w-4 h-4 shrink-0" />
              <span className="flex-1">{label}</span>
              <ChevronRight className="w-3.5 h-3.5 opacity-0 group-hover:opacity-50 transition-opacity" />
            </NavLink>
          ))}
        </nav>

        {/* User footer */}
        <div className="px-3 pb-4 pt-2 border-t border-slate-100">
          <div className="px-3 py-2 mb-1">
            <p className="text-xs font-medium text-slate-900 truncate">{user?.name ?? 'Usuário'}</p>
            <p className="text-xs text-slate-500 truncate">{user?.email ?? ''}</p>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-slate-600 hover:bg-red-50 hover:text-red-600 transition-colors duration-150"
          >
            <LogOut className="w-4 h-4" />
            Sair
          </button>
        </div>
      </aside>
    </>
  )
}
