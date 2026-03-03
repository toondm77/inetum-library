import { Routes } from '@angular/router';
import { Homepage } from './homepage/homepage';
import { authGuardFn } from '@auth0/auth0-angular';
import { adminGuard } from './guards/admin.guard';
import { FavoritesComponent } from './favorites/favorites.component';
import { LoansComponent } from './loans/loans.component';
import { StatsComponent } from './stats/stats.component';
import { SettingsComponent } from './settings/settings.component';
import { AdminLoansComponent } from './admin/loans/admin-loans.component';
import { AdminBooksComponent } from './admin/books/admin-books.component';
import { AdminStatsComponent } from './admin/stats/admin-stats.component';
import { BookDetailComponent } from './book-detail/book-detail.component';

export const routes: Routes = [
  {
    path: '', 
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    component: Homepage,
    canActivate: [authGuardFn],
  },
  {
    path: 'books/:id',
    component: BookDetailComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'favorites',
    component: FavoritesComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'loans',
    component: LoansComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'stats',
    component: StatsComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'admin/loans',
    component: AdminLoansComponent,
    canActivate: [authGuardFn, adminGuard],
  },
  {
    path: 'admin/books',
    component: AdminBooksComponent,
    canActivate: [authGuardFn, adminGuard],
  },
  {
    path: 'admin/stats',
    component: AdminStatsComponent,
    canActivate: [authGuardFn, adminGuard],
  },
];


