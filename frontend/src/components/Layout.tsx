import { useState } from 'react'
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import * as DropdownMenu from '@radix-ui/react-dropdown-menu'
import {
  LayoutDashboard,
  ShoppingCart,
  Package,
  BarChart3,
  Settings,
  Bell,
  Search,
  Store,
  LogOut,
  ChevronDown,
  User,
  RotateCcw,
} from 'lucide-react'
import type { RootState } from '@/app/store'
import { logout } from '@/features/auth/authSlice'

const navSections = [
  {
    title: 'Core Operations',
    items: [
      { name: 'Dashboard',        path: '/dashboard', icon: LayoutDashboard },
      { name: 'Point of Sale',    path: '/sales',     icon: ShoppingCart },
      { name: 'Inventory (FEFO)', path: '/inventory', icon: Package },
      { name: 'Returns',          path: '/returns',   icon: RotateCcw },
    ],
  },
  {
    title: 'Management',
    items: [
      { name: 'Reports & Analytics', path: '/reports', icon: BarChart3 },
    ],
  },
]

const stores = ['Store 01 - Downtown', 'Store 02 - Northside']

export function Layout() {
  const [activeStore, setActiveStore] = useState(stores[0])
  const location = useLocation()
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const user = useSelector((s: RootState) => s.auth.user)

  function handleLogout() {
    dispatch(logout())
    navigate('/login', { replace: true })
  }

  const initials = user?.name
    ? user.name.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase()
    : 'U'

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: 'var(--page-bg)' }}>
      {/* ── Sidebar ── */}
      <aside
        className="w-64 flex flex-col shrink-0 shadow-2xl z-20"
        style={{
          background: 'var(--sidebar-bg)',
          borderRight: '1px solid var(--sidebar-border)',
        }}
      >
        {/* Logo */}
        <div
          className="h-16 px-6 flex items-center gap-3"
          style={{
            background: 'var(--sidebar-bg-header)',
            borderBottom: '1px solid var(--sidebar-border)',
          }}
        >
          <div
            className="w-8 h-8 rounded flex items-center justify-center font-bold text-lg text-white"
            style={{ background: 'linear-gradient(135deg, var(--blue-500), var(--blue-700))' }}
          >
            P
          </div>
          <span className="text-xl font-bold tracking-tight text-slate-100">PharmaOps</span>
        </div>

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto py-6 flex flex-col gap-6">
          {navSections.map((section) => (
            <div key={section.title} className="px-4">
              <p
                className="text-[10px] font-bold uppercase tracking-widest mb-3 px-3"
                style={{ color: 'var(--sidebar-section-label)' }}
              >
                {section.title}
              </p>
              <div className="flex flex-col gap-1">
                {section.items.map((item) => {
                  const isActive =
                    location.pathname === item.path ||
                    (item.path !== '/' && location.pathname.startsWith(item.path))
                  const Icon = item.icon
                  return (
                    <NavLink
                      key={item.path}
                      to={item.path}
                      style={
                        isActive
                          ? {
                              background: 'var(--sidebar-active-bg)',
                              color: 'var(--sidebar-text-active)',
                              boxShadow: 'inset 2px 0 0 0 var(--sidebar-active-accent)',
                            }
                          : { color: 'var(--sidebar-text)' }
                      }
                      className="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 text-sm font-medium"
                    >
                      <Icon
                        size={18}
                        className="shrink-0"
                        style={{ color: isActive ? 'var(--sidebar-icon-active)' : 'var(--sidebar-icon)' }}
                      />
                      <span>{item.name}</span>
                    </NavLink>
                  )
                })}
              </div>
            </div>
          ))}
        </nav>

        {/* Footer */}
        <div
          className="p-4"
          style={{
            background: 'var(--sidebar-bg-header)',
            borderTop: '1px solid var(--sidebar-border)',
          }}
        >
          <button
            className="flex items-center gap-3 px-3 py-2.5 w-full rounded-lg transition-colors text-sm font-medium"
            style={{ color: 'var(--sidebar-text)' }}
          >
            <Settings size={18} style={{ color: 'var(--sidebar-icon)' }} />
            <span>System Settings</span>
          </button>
        </div>
      </aside>

      {/* ── Main ── */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Topbar */}
        <header
          className="h-16 flex items-center justify-between px-6 shrink-0 z-10"
          style={{
            background: 'var(--topbar-bg)',
            borderBottom: '1px solid var(--topbar-border)',
          }}
        >
          {/* Search */}
          <div className="relative w-96">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
            <input
              type="text"
              placeholder="Press Cmd+K to search across the platform..."
              readOnly
              className="w-full pl-9 pr-20 py-1.5 bg-slate-100/50 border border-transparent rounded-md text-sm focus:outline-none cursor-default text-slate-500"
            />
            <div className="absolute right-2 top-1/2 -translate-y-1/2 flex items-center gap-1">
              <kbd className="px-1.5 py-0.5 text-[10px] font-medium text-slate-400 bg-white border border-slate-200 rounded shadow-sm">⌘</kbd>
              <kbd className="px-1.5 py-0.5 text-[10px] font-medium text-slate-400 bg-white border border-slate-200 rounded shadow-sm">K</kbd>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {/* Store selector */}
            <DropdownMenu.Root>
              <DropdownMenu.Trigger asChild>
                <button className="flex items-center gap-2 bg-slate-50 px-3 py-1.5 rounded-md border border-slate-200 hover:bg-slate-100 transition-all text-xs font-semibold text-slate-700">
                  <Store size={14} style={{ color: 'var(--blue-600)' }} />
                  {activeStore}
                  <ChevronDown size={14} className="text-slate-400 ml-1" />
                </button>
              </DropdownMenu.Trigger>
              <DropdownMenu.Portal>
                <DropdownMenu.Content
                  className="bg-white rounded-lg shadow-xl border border-slate-100 p-1 min-w-[220px] z-50"
                  sideOffset={6}
                >
                  <div className="px-3 py-2 text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                    Select Location
                  </div>
                  {stores.map((s) => (
                    <DropdownMenu.Item
                      key={s}
                      onSelect={() => setActiveStore(s)}
                      className="flex items-center gap-2 px-3 py-2 text-sm rounded-md cursor-pointer outline-none text-slate-700 font-medium data-[highlighted]:bg-blue-50 data-[highlighted]:text-blue-700"
                    >
                      <div
                        className="w-2 h-2 rounded-full shrink-0"
                        style={{ background: activeStore === s ? 'var(--blue-500)' : 'transparent', border: activeStore === s ? 'none' : '1px solid #cbd5e1' }}
                      />
                      {s}
                    </DropdownMenu.Item>
                  ))}
                </DropdownMenu.Content>
              </DropdownMenu.Portal>
            </DropdownMenu.Root>

            <div className="h-6 w-px bg-slate-200" />

            {/* Notifications */}
            <button className="relative p-1.5 rounded-md text-slate-500 hover:bg-slate-100 transition-colors">
              <Bell size={18} />
              <span
                className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full border-2 border-white"
                style={{ background: 'var(--red-500)' }}
              />
            </button>

            {/* User menu */}
            <DropdownMenu.Root>
              <DropdownMenu.Trigger asChild>
                <button className="flex items-center gap-2 hover:bg-slate-50 p-1 pr-2 rounded-md transition-colors border border-transparent hover:border-slate-200">
                  <div
                    className="w-8 h-8 rounded-full flex items-center justify-center text-white font-bold text-sm shrink-0"
                    style={{ background: 'var(--blue-600)' }}
                  >
                    {initials}
                  </div>
                  <div className="text-left hidden md:block">
                    <p className="text-xs font-bold text-slate-800 leading-tight">{user?.name ?? '—'}</p>
                    <p className="text-[10px] text-slate-500 font-medium">{user?.email ?? ''}</p>
                  </div>
                  <ChevronDown size={14} className="text-slate-400 ml-1" />
                </button>
              </DropdownMenu.Trigger>
              <DropdownMenu.Portal>
                <DropdownMenu.Content
                  className="bg-white rounded-lg shadow-xl border border-slate-100 p-1 min-w-[180px] z-50"
                  sideOffset={6}
                  align="end"
                >
                  <DropdownMenu.Item className="flex items-center gap-2 px-3 py-2 text-sm rounded-md cursor-pointer outline-none text-slate-700 data-[highlighted]:bg-slate-50">
                    <User size={16} /> My Profile
                  </DropdownMenu.Item>
                  <DropdownMenu.Separator className="h-px bg-slate-100 my-1" />
                  <DropdownMenu.Item
                    onSelect={handleLogout}
                    className="flex items-center gap-2 px-3 py-2 text-sm rounded-md cursor-pointer outline-none data-[highlighted]:bg-red-50"
                    style={{ color: 'var(--red-500)' }}
                  >
                    <LogOut size={16} /> Sign out
                  </DropdownMenu.Item>
                </DropdownMenu.Content>
              </DropdownMenu.Portal>
            </DropdownMenu.Root>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto" style={{ background: 'var(--page-bg)' }}>
          <Outlet />
        </main>
      </div>
    </div>
  )
}
