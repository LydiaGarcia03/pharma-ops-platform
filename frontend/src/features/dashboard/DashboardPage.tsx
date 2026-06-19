import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import {
  DollarSign,
  FileText,
  AlertTriangle,
  Activity,
  Clock,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react'

const salesData = [
  { time: '08:00', sales: 1200 },
  { time: '10:00', sales: 2100 },
  { time: '12:00', sales: 3400 },
  { time: '14:00', sales: 2800 },
  { time: '16:00', sales: 4100 },
  { time: '18:00', sales: 3800 },
  { time: '20:00', sales: 1900 },
]

const alerts = [
  { id: 1, severity: 'high',   message: 'Amoxicillin 500mg below threshold (8 units)', time: '10M' },
  { id: 2, severity: 'medium', message: 'Batch A742 (Lisinopril) expires in 30 days',  time: '1H'  },
  { id: 3, severity: 'high',   message: 'SNGPC: Unmatched prescription block',          time: '2H'  },
]

interface StatCardProps {
  title: string
  value: string
  icon: React.ReactNode
  iconBg: string
  trend: string
  trendUp: boolean
  note?: string
}

function StatCard({ title, value, icon, iconBg, trend, trendUp, note }: StatCardProps) {
  return (
    <div
      className="rounded-xl p-5 flex flex-col hover:border-blue-300 transition-colors"
      style={{
        background: 'var(--card-bg)',
        border: '1px solid var(--card-border)',
        boxShadow: 'var(--card-shadow)',
        borderRadius: 'var(--card-radius)',
      }}
    >
      <div className="flex justify-between items-start mb-2">
        <h3 className="text-xs font-bold uppercase tracking-wider" style={{ color: 'var(--text-muted)' }}>
          {title}
          {note && <span className="ml-1 normal-case font-normal text-[10px]">({note})</span>}
        </h3>
        <div className="p-1.5 rounded-md text-white shadow-sm" style={{ background: iconBg }}>
          {icon}
        </div>
      </div>
      <p className="text-2xl font-black tracking-tight mt-2" style={{ color: 'var(--text-heading)' }}>
        {value}
      </p>
      <div className="flex items-center gap-1.5 mt-2">
        <span
          className="flex items-center gap-0.5 text-[10px] font-bold px-1.5 py-0.5 rounded"
          style={{
            background: trendUp ? '#ecfdf5' : '#fef2f2',
            color: trendUp ? '#065f46' : '#991b1b',
          }}
        >
          {trendUp ? <ArrowUpRight size={12} /> : <ArrowDownRight size={12} />}
          {trend}
        </span>
        <span className="text-[10px] font-medium" style={{ color: 'var(--text-muted)' }}>
          vs last period
        </span>
      </div>
    </div>
  )
}

export function DashboardPage() {
  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'short',
    day: 'numeric',
  })

  return (
    <div className="p-8 space-y-6 max-w-[1600px] mx-auto">
      {/* Header */}
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight" style={{ color: 'var(--text-heading)' }}>
            Operation Overview
          </h1>
          <p className="text-sm font-medium mt-1" style={{ color: 'var(--text-body)' }}>
            Real-time metrics for Store 01 - Downtown
          </p>
        </div>
        <div
          className="px-3 py-1.5 rounded-md border text-xs font-bold flex items-center gap-2"
          style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--card-border)',
            boxShadow: 'var(--card-shadow)',
            color: 'var(--text-body)',
          }}
        >
          <Clock size={14} style={{ color: 'var(--blue-500)' }} />
          {today}
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Gross Revenue"
          value="$12,450.00"
          icon={<DollarSign size={20} />}
          iconBg="var(--emerald-500)"
          trend="+14.5%"
          trendUp={true}
        />
        <StatCard
          title="Prescriptions (SNGPC)"
          value="142"
          icon={<FileText size={20} />}
          iconBg="var(--blue-500)"
          trend="+5.2%"
          trendUp={true}
          note="Phase 3"
        />
        <StatCard
          title="Low Stock Items"
          value="18"
          icon={<AlertTriangle size={20} />}
          iconBg="var(--amber-500)"
          trend="-2"
          trendUp={true}
        />
        <StatCard
          title="Expiring Lots (30d)"
          value="5"
          icon={<Activity size={20} />}
          iconBg="var(--red-500)"
          trend="+1"
          trendUp={false}
        />
      </div>

      {/* Chart + Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Area chart */}
        <div
          className="lg:col-span-2 flex flex-col p-6 rounded-xl"
          style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--card-border)',
            boxShadow: 'var(--card-shadow)',
            borderRadius: 'var(--card-radius)',
          }}
        >
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-base font-bold" style={{ color: 'var(--text-heading)' }}>
              Intraday Sales Performance
            </h2>
            <div className="flex bg-slate-100 p-0.5 rounded-md">
              <button className="px-3 py-1 text-xs font-semibold bg-white shadow-sm rounded text-slate-800">
                Today
              </button>
              <button className="px-3 py-1 text-xs font-semibold text-slate-500">Week</button>
            </div>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={salesData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis
                  dataKey="time"
                  axisLine={false}
                  tickLine={false}
                  tick={{ fontSize: 11, fill: '#94a3b8', fontWeight: 500 }}
                  dy={10}
                />
                <YAxis
                  axisLine={false}
                  tickLine={false}
                  tick={{ fontSize: 11, fill: '#94a3b8', fontWeight: 500 }}
                  dx={-10}
                />
                <Tooltip
                  contentStyle={{
                    borderRadius: '8px',
                    border: '1px solid #e2e8f0',
                    boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
                    fontSize: '12px',
                    fontWeight: 'bold',
                  }}
                />
                <Area
                  type="monotone"
                  dataKey="sales"
                  stroke="var(--blue-500)"
                  strokeWidth={2}
                  fillOpacity={0.2}
                  fill="var(--blue-500)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Alerts */}
        <div
          className="flex flex-col rounded-xl overflow-hidden"
          style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--card-border)',
            boxShadow: 'var(--card-shadow)',
            borderRadius: 'var(--card-radius)',
          }}
        >
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <h2 className="text-base font-bold" style={{ color: 'var(--text-heading)' }}>
              Action Required
            </h2>
            <span className="bg-red-100 text-red-600 text-xs font-bold px-2 py-0.5 rounded-full">
              3 pending
            </span>
          </div>
          <div className="p-2 flex-1">
            {alerts.map((alert) => (
              <div
                key={alert.id}
                className="flex gap-3 p-3 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer group"
              >
                <div
                  className="w-2 h-2 rounded-full mt-1.5 shrink-0"
                  style={{
                    background: alert.severity === 'high' ? 'var(--red-500)' : 'var(--amber-500)',
                    boxShadow:
                      alert.severity === 'high'
                        ? '0 0 8px rgba(239,68,68,0.4)'
                        : 'none',
                  }}
                />
                <div className="flex-1">
                  <p
                    className="text-sm font-semibold leading-snug group-hover:text-blue-600 transition-colors"
                    style={{ color: 'var(--text-heading)' }}
                  >
                    {alert.message}
                  </p>
                  <span
                    className="text-[10px] font-bold uppercase tracking-wider mt-1 block"
                    style={{ color: 'var(--text-muted)' }}
                  >
                    {alert.time}
                  </span>
                </div>
              </div>
            ))}
          </div>
          <div className="p-4 border-t border-slate-100 bg-slate-50 rounded-b-xl">
            <button
              className="w-full py-2 rounded-md text-sm font-bold transition-colors"
              style={{
                background: 'var(--card-bg)',
                border: '1px solid var(--card-border)',
                color: 'var(--text-body)',
              }}
            >
              View All Notifications
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
