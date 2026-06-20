import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from '@/app/store'
import { Layout } from '@/components/Layout'
import { LoginPage } from '@/features/auth/LoginPage'
import { DashboardPage } from '@/features/dashboard/DashboardPage'
import { InventoryPage } from '@/features/inventory/InventoryPage'
import { PointOfSalePage } from '@/features/sales/PointOfSalePage'
import { ReportsPage } from '@/features/reports/ReportsPage'
import { ReturnPage } from '@/features/returns/ReturnPage'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = useSelector((s: RootState) => s.auth.token)
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/"
          element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard"  element={<DashboardPage />} />
          <Route path="sales"      element={<PointOfSalePage />} />
          <Route path="inventory"  element={<InventoryPage />} />
          <Route path="reports"    element={<ReportsPage />} />
          <Route path="returns"    element={<ReturnPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
