import { ChangeDetectionStrategy, Component, signal, inject, computed } from '@angular/core';
import { NgTemplateOutlet, AsyncPipe } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { RoleService } from '../services/role.service';

type IconName =
  | 'home'
  | 'favorites'
  | 'loans'
  | 'all-loans'
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


@Component({
  selector: 'app-side-nav',
  standalone: true,
  imports: [NgTemplateOutlet, AsyncPipe, RouterLink, RouterLinkActive],
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SideNavComponent {
  private readonly auth = inject(AuthService);
  private readonly roleService = inject(RoleService);

  private readonly isAdmin = toSignal(this.roleService.isAdmin$, { initialValue: false });

  private readonly allNavItems: NavItem[] = [
    { label: 'Home', icon: 'home', route: '/home' },
    { label: 'Favorieten', icon: 'favorites', route: '/favorites' },
    { label: 'Mijn uitleningen', icon: 'loans', route: '/loans', badge: 4 },
    { label: 'Alle uitleningen', icon: 'all-loans', route: '/admin/loans', badge: 47, adminOnly: true },
    { label: 'Boeken beheren', icon: 'books', route: '/admin/books', adminOnly: true },
    { label: 'Mijn statistieken', icon: 'stats', route: '/stats' },
    { label: 'Bibliotheek statistieken', icon: 'library-stats', route: '/admin/stats', adminOnly: true },
    { label: 'Instellingen', icon: 'settings', route: '/settings' },
  ];

  protected readonly navItems = computed(() =>
    this.allNavItems.filter(item => !item.adminOnly || this.isAdmin())
  );

  protected readonly user = {
    initials: 'TD',
    name: 'Toon De Meyer',
    role: 'Java Developer',
  };

  protected readonly mobileOpen = signal(false);
  protected readonly profileMenuOpen = signal(false);

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
}