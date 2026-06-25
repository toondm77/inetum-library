import { Component, inject } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `
    <div class="flex min-h-screen flex-col items-center justify-center bg-slate-50">
      <div class="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg text-center">
        <div class="mb-6 flex justify-center">
          <svg class="h-12 w-12 text-emerald-600" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        
        <h1 class="mb-2 text-2xl font-bold text-slate-900">Welkom bij RLibrary</h1>
        <p class="mb-8 text-slate-500">Log in om toegang te krijgen tot de bibliotheek.</p>

        <button 
          (click)="login()"
          class="w-full rounded-lg bg-emerald-600 px-4 py-3 text-sm font-semibold text-white shadow-sm hover:bg-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2 transition-colors">
          Inloggen
        </button>
      </div>
    </div>
  `
})
export class LoginComponent {
  private auth = inject(AuthService);

  login(): void {
    this.auth.loginWithRedirect();
  }
}
