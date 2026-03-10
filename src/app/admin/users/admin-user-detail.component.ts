import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-admin-user-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-user-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminUserDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private readonly apiBase = environment.apiBase;

  protected readonly user = signal<Person | null>(null);
  protected readonly apiLoading = signal(false);
  protected readonly apiError = signal<string | null>(null);

  constructor() {
    this.loadUser();
  }

  protected fullName(): string {
    const currentUser = this.user();
    if (!currentUser) {
      return '-';
    }

    return `${currentUser.firstName ?? ''} ${currentUser.lastName ?? ''}`.trim() || '-';
  }

  protected formatDate(value?: string): string {
    if (!value) {
      return '-';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toLocaleDateString('nl-BE');
  }

  private loadUser(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.apiError.set('Geen gebruiker geselecteerd.');
      return;
    }

    this.apiLoading.set(true);
    this.apiError.set(null);

    this.http.get<Person>(`${this.apiBase}/persons/${idParam}`).subscribe({
      next: (response) => {
        this.user.set(response);
        this.apiLoading.set(false);
      },
      error: () => {
        this.user.set(null);
        this.apiError.set('Kon gebruiker niet laden.');
        this.apiLoading.set(false);
      },
    });
  }
}
