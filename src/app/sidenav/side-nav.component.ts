import { ChangeDetectionStrategy, Component, signal, inject, computed } from '@angular/core';
import { NgTemplateOutlet, AsyncPipe } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RoleService } from '../services/role.service';
import { environment } from '../../environments/environment';

type IconName =
  | 'home'
  | 'favorites'
  | 'loans'
  | 'all-loans'
  | 'users'
  | 'books'
  | 'stats'
  | 'library-stats'
  | 'settings'
  | 'switch'
  | 'logout';

type NavItem = {
  label: string;
  icon: IconName;
  route: string;
  badge?: number;
  adminOnly?: boolean;
};

interface LoanPageResponse {
  totalElements: number;
}


@Component({
  selector: 'app-side-nav',
  imports: [NgTemplateOutlet, RouterLink, RouterLinkActive],
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SideNavComponent {
  private readonly auth = inject(AuthService);
  private readonly roleService = inject(RoleService);
  private readonly http = inject(HttpClient);
  private readonly apiBase = environment.apiBase;

  private readonly isAdmin = toSignal(this.roleService.isAdmin$, { initialValue: false });
  private readonly allLoansCount = signal(0);

  private readonly allNavItems: NavItem[] = [
    { label: 'Home', icon: 'home', route: '/home' },
    { label: 'Favorieten', icon: 'favorites', route: '/favorites' },
    { label: 'Mijn uitleningen', icon: 'loans', route: '/loans', badge: 4 },
    { label: 'Alle uitleningen', icon: 'all-loans', route: '/admin/loans', adminOnly: true },
    { label: 'Gebruikers', icon: 'users', route: '/admin/users', adminOnly: true },
    { label: 'Boeken beheren', icon: 'books', route: '/admin/books', adminOnly: true },
    { label: 'Mijn statistieken', icon: 'stats', route: '/stats' },
    { label: 'Bibliotheek statistieken', icon: 'library-stats', route: '/admin/stats', adminOnly: true },
    { label: 'Instellingen', icon: 'settings', route: '/settings' },
  ];

  protected readonly navItems = computed(() =>
    this.allNavItems
      .filter((item) => !item.adminOnly || this.isAdmin())
      .map((item) => {
        if (item.route === '/admin/loans') {
          return {
            ...item,
            badge: this.allLoansCount(),
          };
        }

        return item;
      })
  );

  protected readonly user = {
    initials: 'TD',
    name: 'Toon De Meyer',
    role: 'Java Developer',
  };

  protected readonly mobileOpen = signal(false);
  protected readonly profileMenuOpen = signal(false);

  constructor() {
    this.loadAllLoansCount();
  }

  protected toggleMobile(): void {
    this.mobileOpen.update((open) => !open);
  }

  protected closeMobile(): void {
    this.mobileOpen.set(false);
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update((open) => !open);
  }
  
  protected logout(): void {
    this.auth.logout({ logoutParams: { returnTo: window.location.origin } });
  }

  private loadAllLoansCount(): void {
    const params = new HttpParams()
      .set('page', '0')
      .set('size', '1')
      .set('sort', 'loanDate')
      .set('direction', 'desc');

    this.http.get<LoanPageResponse>(`${this.apiBase}/loans`, { params }).subscribe({
      next: (response) => {
        this.allLoansCount.set(response.totalElements ?? 0);
      },
      error: () => {
        this.allLoansCount.set(0);
      },
    });
  }
}