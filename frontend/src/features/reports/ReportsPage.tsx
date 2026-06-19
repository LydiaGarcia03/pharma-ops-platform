import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'
import { Info, Calendar, Download } from 'lucide-react'

const stockMovementData = [
  { category: 'Antibiotics', stockIn: 380, stockOut: 320 },
  { category: 'Pain Relief',  stockIn: 290, stockOut: 310 },
  { category: 'Cardio',       stockIn: 160, stockOut: 180 },
  { category: 'Psychiatric',  stockIn: 140, stockOut: 155 },
  { category: 'Vitamins',     stockIn: 460, stockOut: 430 },
]

const topProducts = [
  { product: 'Ibuprofen 400mg Caps',   unitsSold: 1245, revenue: 11192.55 },
  { product: 'Amoxicillin 500mg Caps', unitsSold: 892,  revenue: 14263.08 },
  { product: 'Vitamin C 1000mg',       unitsSold: 650,  revenue: 6493.50  },
  { product: 'Loratadine 10mg',        unitsSold: 421,  revenue: 5047.79  },
]

const monthlyFinancial = [
  { month: 'Jan 2026', totalSales: 48320.00, items: 2841, avgTicket: 17.01 },
  { month: 'Feb 2026', totalSales: 51200.00, items: 3010, avgTicket: 17.01 },
  { month: 'Mar 2026', totalSales: 55800.00, items: 3280, avgTicket: 17.01 },
  { month: 'Apr 2026', totalSales: 49100.00, items: 2890, avgTicket: 17.00 },
  { month: 'May 2026', totalSales: 62450.00, items: 3671, avgTicket: 17.01 },
  { month: 'Jun 2026', totalSales: 12450.00, items: 732,  avgTicket: 17.01 },
]

const annualFinancial = [
  { year: '2024', totalSales: 582000.00, items: 34236, avgTicket: 17.00 },
  { year: '2025', totalSales: 631400.00, items: 37140, avgTicket: 17.00 },
  { year: '2026', totalSales: 279320.00, items: 16424, avgTicket: 17.01 },
]

const monthlyMovement = [
  { month: 'May 2026', product: 'Amoxicillin 500mg', outflows: 320, inflows: 400 },
  { month: 'May 2026', product: 'Lisinopril 10mg',   outflows: 95,  inflows: 100 },
  { month: 'May 2026', product: 'Ibuprofen 400mg',   outflows: 510, inflows: 500 },
  { month: 'Jun 2026', product: 'Amoxicillin 500mg', outflows: 112, inflows: 0   },
  { month: 'Jun 2026', product: 'Ibuprofen 400mg',   outflows: 198, inflows: 200 },
]

const cardStyle: React.CSSProperties = {
  background: 'var(--card-bg)',
  border: '1px solid var(--card-border)',
  boxShadow: 'var(--card-shadow)',
  borderRadius: 'var(--card-radius)',
}

function SectionCard({ title, subtitle, children }: { title: string; subtitle?: string; children: React.ReactNode }) {
  return (
    <div style={cardStyle} className="p-6">
      <div className="mb-4">
        <h2 className="text-base font-bold" style={{ color: 'var(--text-heading)' }}>{title}</h2>
        {subtitle && <p className="text-xs mt-0.5" style={{ color: 'var(--text-muted)' }}>{subtitle}</p>}
      </div>
      {children}
    </div>
  )
}

export function ReportsPage() {
  return (
    <div className="p-8 space-y-6 max-w-[1600px] mx-auto">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight" style={{ color: 'var(--text-heading)' }}>
            Analytics &amp; Reports
          </h1>
          <p className="text-sm font-medium mt-1" style={{ color: 'var(--text-body)' }}>
            Aggregated from CQRS read models — Phase 5
          </p>
        </div>
        <div className="flex gap-2">
          <button className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 text-slate-600 font-bold text-sm rounded-md shadow-sm hover:bg-slate-50 transition-colors">
            <Calendar size={16} /> This Month
          </button>
          <button
            className="flex items-center gap-2 px-4 py-2 text-white font-bold text-sm rounded-md shadow-sm transition-colors"
            style={{ background: 'var(--blue-600)' }}
          >
            <Download size={16} /> Export CSV
          </button>
        </div>
      </div>

      {/* Phase 5 notice */}
      <div
        className="flex items-start gap-3 p-4 rounded-xl"
        style={{ background: '#eff6ff', border: '1px solid #bfdbfe' }}
      >
        <Info size={18} className="text-blue-500 shrink-0 mt-0.5" />
        <p className="text-sm font-medium text-blue-700">
          These reports will populate automatically from CQRS read models once{' '}
          <strong>Phase 5</strong> is implemented. Data shown below is illustrative.
        </p>
      </div>

      {/* Row 1: Stock Movement + Top Products */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <SectionCard title="Stock Movement (In / Out)" subtitle="Units per category — current month">
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stockMovementData} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="category" axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: '#94a3b8' }} />
                <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: '#94a3b8' }} />
                <Tooltip
                  contentStyle={{ borderRadius: '8px', border: '1px solid #e2e8f0', fontSize: '12px' }}
                />
                <Legend
                  wrapperStyle={{ fontSize: '11px', fontWeight: 600 }}
                  formatter={(value) => value === 'stockIn' ? 'Stock In' : 'Stock Out'}
                />
                <Bar dataKey="stockIn"  fill="#bfdbfe" radius={[4, 4, 0, 0]} />
                <Bar dataKey="stockOut" fill="var(--blue-500)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </SectionCard>

        <SectionCard title="Top Selling Products" subtitle="By revenue — current month">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Product</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Units Sold</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Revenue</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {topProducts.map((row) => (
                <tr key={row.product}>
                  <td className="py-3 text-sm font-medium" style={{ color: 'var(--text-heading)' }}>{row.product}</td>
                  <td className="py-3 text-sm font-bold text-right text-slate-600">{row.unitsSold.toLocaleString()}</td>
                  <td className="py-3 text-sm font-black text-right" style={{ color: 'var(--text-heading)' }}>
                    R$ {row.revenue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </SectionCard>
      </div>

      {/* Row 2: Monthly Financial + Annual Financial */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <SectionCard
          title="Monthly Financial Report"
          subtitle="Data from monthly_financial_report read model (Phase 5)"
        >
          <table className="w-full text-left border-collapse">
            <thead>
              <tr>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Month</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Total Sales</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Items</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Avg Ticket</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {monthlyFinancial.map((row) => (
                <tr key={row.month}>
                  <td className="py-2.5 text-sm font-medium" style={{ color: 'var(--text-heading)' }}>{row.month}</td>
                  <td className="py-2.5 text-sm font-bold text-right text-slate-700">
                    R$ {row.totalSales.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="py-2.5 text-sm font-bold text-right text-slate-600">{row.items.toLocaleString()}</td>
                  <td className="py-2.5 text-sm font-bold text-right text-slate-600">
                    R$ {row.avgTicket.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </SectionCard>

        <SectionCard
          title="Annual Financial Report"
          subtitle="Data from annual_financial_report read model (Phase 5)"
        >
          <table className="w-full text-left border-collapse">
            <thead>
              <tr>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Year</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Total Sales</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Items</th>
                <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Avg Ticket</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {annualFinancial.map((row) => (
                <tr key={row.year}>
                  <td className="py-3 text-sm font-bold" style={{ color: 'var(--text-heading)' }}>{row.year}</td>
                  <td className="py-3 text-sm font-black text-right" style={{ color: 'var(--text-heading)' }}>
                    R$ {row.totalSales.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="py-3 text-sm font-bold text-right text-slate-600">{row.items.toLocaleString()}</td>
                  <td className="py-3 text-sm font-bold text-right text-slate-600">
                    R$ {row.avgTicket.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </SectionCard>
      </div>

      {/* Row 3: Monthly Inventory Movement */}
      <SectionCard
        title="Monthly Inventory Movement History"
        subtitle="Data from monthly_movement_report read model (Phase 5)"
      >
        <table className="w-full text-left border-collapse">
          <thead>
            <tr>
              <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Month</th>
              <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider">Product</th>
              <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Outflows</th>
              <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Inflows</th>
              <th className="pb-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider text-right">Balance</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {monthlyMovement.map((row, i) => {
              const balance = row.inflows - row.outflows
              return (
                <tr key={i}>
                  <td className="py-3 text-sm font-medium text-slate-600">{row.month}</td>
                  <td className="py-3 text-sm font-bold" style={{ color: 'var(--text-heading)' }}>{row.product}</td>
                  <td className="py-3 text-sm font-bold text-right" style={{ color: 'var(--red-500)' }}>
                    −{row.outflows}
                  </td>
                  <td className="py-3 text-sm font-bold text-right" style={{ color: 'var(--emerald-500)' }}>
                    +{row.inflows}
                  </td>
                  <td
                    className="py-3 text-sm font-black text-right"
                    style={{ color: balance >= 0 ? 'var(--emerald-500)' : 'var(--red-500)' }}
                  >
                    {balance >= 0 ? '+' : ''}{balance}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </SectionCard>
    </div>
  )
}
