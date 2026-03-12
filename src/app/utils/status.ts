export function getLoanStatusLabel(status: string | undefined | null): string {
  switch (status?.toUpperCase()) {
    case 'LOANED': return 'Uitgeleend';
    case 'RETURNED': return 'Teruggebracht';
    case 'PARTLYRETURNED': return 'Gedeeltelijk';
    case 'LOST': return 'Verloren';
    default: return status || 'Onbekend';
  }
}

export function getLoanStatusColor(status: string | undefined | null): string {
  switch (status?.toUpperCase()) {
    case 'LOANED': return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'RETURNED': return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case 'PARTLYRETURNED': return 'bg-orange-100 text-orange-700 border-orange-200';
    case 'LOST': return 'bg-red-100 text-red-700 border-red-200';
    default: return 'bg-slate-100 text-slate-700 border-slate-200';
  }
}

export function getBookStateLabel(state: string | undefined | null): string {
  switch (state?.toUpperCase()) {
    case 'AVAILABLE': return 'Beschikbaar';
    case 'BORROWED': return 'Uitgeleend';
    case 'LOST': return 'Verloren';
    case 'TEMPORARILYUNAVAILABLE': return 'Tijdelijk onbeschikbaar';
    default: return state || 'Onbekend';
  }
}

export function getBookStateColor(state: string | undefined | null): string {
  switch (state?.toUpperCase()) {
    case 'AVAILABLE': return 'bg-[#e6f7ec] text-[#2ebd6b] border-[#a0dec1]';
    case 'BORROWED': return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'TEMPORARILYUNAVAILABLE': return 'bg-[#fff8e1] text-[#fbc02d] border-[#ffe082]';
    case 'LOST': return 'bg-red-100 text-red-700 border-red-200';
    default: return 'bg-slate-100 text-slate-700 border-slate-200';
  }
}

export function getUserStatusLabel(status: string | undefined | null): string {
  switch (status?.toUpperCase()) {
    case 'ACTIVE': return 'Actief';
    case 'INACTIVE': return 'Inactief';
    case 'BANNED': return 'Verbannen';
    default: return status || 'Onbekend';
  }
}

export function getUserStatusColor(status: string | undefined | null): string {
  switch (status?.toUpperCase()) {
    case 'ACTIVE': return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case 'INACTIVE': return 'bg-slate-100 text-slate-600 border-slate-200';
    case 'BANNED': return 'bg-red-100 text-red-700 border-red-200';
    default: return 'bg-slate-100 text-slate-700 border-slate-200';
  }
}
