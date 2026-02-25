import { Routes } from '@angular/router';
import { Homepage } from './homepage/homepage';
import { authGuardFn } from '@auth0/auth0-angular';

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
];

