import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';


interface Page<T> {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  items: T[];
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminUsersComponent {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiBase = environment.apiBase;

  protected readonly users = signal<Person[]>([]);
  protected readonly apiLoading = signal(false);
  protected readonly apiError = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = signal(20);

  constructor() {
    this.loadUsers();
  }

  protected loadUsers(): void {
    this.apiLoading.set(true);
    this.apiError.set(null);

    const params = new HttpParams()
      .set('page', String(this.currentPage()))
      .set('size', String(this.pageSize()))
      .set('sort', 'lastName')
      .set('direction', 'asc');

    this.http.get<Page<Person>>(`${this.apiBase}/persons`, { params }).subscribe({
      next: (response) => {
        this.users.set(response.items ?? []);
        this.totalElements.set(response.totalElements ?? 0);
        this.totalPages.set(response.totalPages ?? 0);
        this.apiLoading.set(false);
      },
      error: () => {
        this.users.set([]);
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.apiError.set('Kon gebruikers niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  protected openUserDetails(personId: number): void {
    this.router.navigate(['/admin/users', personId]);
  }

  protected prevPage(): void {
    if (this.currentPage() === 0) {
      return;
    }

    this.currentPage.update((page) => page - 1);
    this.loadUsers();
  }

  protected nextPage(): void {
    if (this.currentPage() >= this.totalPages() - 1) {
      return;
    }

    this.currentPage.update((page) => page + 1);
    this.loadUsers();
  }

  protected fullName(user: Person): string {
    return `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim() || '-';
  }

  protected formatDate(value: string): string {
    if (!value) {
      return '-';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toLocaleDateString('nl-BE');
  }
}
