import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '@auth0/auth0-angular';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private readonly auth = inject(AuthService);
  private readonly http = inject(HttpClient);

  protected readonly user = toSignal(this.auth.user$);
  protected readonly isAuthenticated = toSignal(this.auth.isAuthenticated$);
  protected readonly isLoading = toSignal(this.auth.isLoading$);

  protected readonly apiResult = signal<string | null>(null);
  protected readonly apiError = signal<string | null>(null);
  protected readonly apiLoading = signal(false);

  private readonly apiBase = environment.apiBase;

  protected login(): void {
    this.auth.loginWithRedirect();
  }

  protected logout(): void {
    this.auth.logout({ logoutParams: { returnTo: window.location.origin } });
  }

  protected callApi(): void {
    this.apiLoading.set(true);
    this.apiError.set(null);
    this.apiResult.set(null);

    this.http
      .get<unknown>(`${this.apiBase}/libraries`, { responseType: 'json' })
      .subscribe({
        next: (response: unknown) => {
          const asText = JSON.stringify(response, null, 2);
          this.apiResult.set(asText);
          this.apiLoading.set(false);
          console.log('API response:', response);
        },
        error: (error: unknown) => {
          const details = error instanceof HttpErrorResponse && error.error
            ? JSON.stringify(error.error, null, 2)
            : `${error}`;
          this.apiError.set(details);
          this.apiLoading.set(false);
        }
      });
  }
}
