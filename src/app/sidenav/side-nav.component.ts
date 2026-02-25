import { ChangeDetectionStrategy, Component, signal, inject } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';
import { RouterLink, RouterLinkActive } from '@angular/router';

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
};


@Component({
  selector: 'app-side-nav',
  standalone: true,
  imports: [NgTemplateOutlet, RouterLink, RouterLinkActive],
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SideNavComponent {
  private readonly auth = inject(AuthService);

  protected readonly navItems: NavItem[] = [
    { label: 'Home', icon: 'home', route: '/home' },
    { label: 'Favorieten', icon: 'favorites', route: '/favorites' },
    { label: 'Mijn uitleningen', icon: 'loans', route: '/loans', badge: 4 },
    { label: 'Alle uitleningen', icon: 'all-loans', route: '/admin/loans', badge: 47 },
    { label: 'Boeken beheren', icon: 'books', route: '/admin/books' },
    { label: 'Mijn statistieken', icon: 'stats', route: '/stats' },
    { label: 'Bibliotheek statistieken', icon: 'library-stats', route: '/admin/stats' },
    { label: 'Instellingen', icon: 'settings', route: '/settings' },
  ];

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