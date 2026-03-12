import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { environment } from '../../environments/environment';
import { Loan } from '../models/Loan';
import { Page } from '../models/Page';
import { formatLoanDateTime } from '../utils/date';
import { getLoanStatusLabel, getLoanStatusColor } from '../utils/status';
import { AdminLoanCreateComponent } from '../admin/loans/create/admin-loan-create.component';

interface BookCoverResponse {
  coverImage?: string;
}

interface BookCover {
  id: number;
  coverImage?: string;
}

interface PersonSummary {
  id: number;
  auth0Id: string;
}

@Component({
  selector: 'app-loans',
  imports: [CommonModule, RouterLink, AdminLoanCreateComponent],
  templateUrl: './loans.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoansComponent {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly apiBase = environment.apiBase;
  protected readonly isCreatingLoan = signal(false);
  protected readonly loans = signal<Loan[]>([]);
  protected readonly apiLoading = signal(false);
  protected readonly apiError = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly currentPersonId = signal<number | null>(null);
  protected readonly bookCovers = signal<BookCover[]>([]);
  public readonly formatDate = formatLoanDateTime;

  constructor() {
    this.resolveCurrentPersonId();
  }

  protected reload(): void {
    if (!this.currentPersonId()) {
      this.resolveCurrentPersonId();
      return;
    }

    this.loadMyLoans();
  }

  protected prevPage(): void {
    if (this.currentPage() === 0) {
      return;
    }

    this.currentPage.update((page) => page - 1);
    this.loadMyLoans();
  }

  protected nextPage(): void {
    if (this.currentPage() >= this.totalPages() - 1) {
      return;
    }

    this.currentPage.update((page) => page + 1);
    this.loadMyLoans();
  }

  protected statusLabel(status: string): string {
    return getLoanStatusLabel(status);
  }

  protected statusClass(status: string): string {
    const base = 'inline-flex items-center px-2 sm:px-3 py-0.5 sm:py-1 rounded-full text-[11px] sm:text-[13px] font-semibold border';
    return `${base} ${getLoanStatusColor(status)}`;
  }

  protected bookCoverImage(bookId: number): string | undefined {
    return this.bookCovers().find((book) => book.id === bookId)?.coverImage;
  }

  private resolveCurrentPersonId(): void {
    this.apiLoading.set(true);
    this.apiError.set(null);

    this.auth.user$.subscribe({
      next: (user) => {
        const auth0Id = typeof user?.sub === 'string' ? user.sub : null;
        if (!auth0Id) {
          this.apiError.set('Kan huidige gebruiker niet bepalen.');
          this.apiLoading.set(false);
          return;
        }

        this.findPersonIdByAuth0Id(auth0Id, 0);
      },
      error: () => {
        this.apiError.set('Kan huidige gebruiker niet bepalen.');
        this.apiLoading.set(false);
      },
    });
  }

  private findPersonIdByAuth0Id(auth0Id: string, page: number): void {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', '200')
      .set('sort', 'lastName')
      .set('direction', 'asc');

    this.http.get<Page<PersonSummary>>(`${this.apiBase}/persons`, { params }).subscribe({
      next: (response) => {
        const person = (response.items ?? []).find((candidate) => candidate.auth0Id === auth0Id);
        if (person) {
          this.currentPersonId.set(person.id);
          this.currentPage.set(0);
          this.loadMyLoans();
          return;
        }

        if (page + 1 < (response.totalPages ?? 0)) {
          this.findPersonIdByAuth0Id(auth0Id, page + 1);
          return;
        }

        this.apiError.set('Geen gekoppelde persoon gevonden voor deze gebruiker.');
        this.apiLoading.set(false);
      },
      error: () => {
        this.apiError.set('Kon persoon voor huidige gebruiker niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  protected loadMyLoans(): void {
    const personId = this.currentPersonId();
    if (!personId) {
      this.apiError.set('Geen persoon geselecteerd.');
      return;
    }

    this.apiLoading.set(true);
    this.apiError.set(null);

    const params = new HttpParams()
      .set('page', String(this.currentPage()))
      .set('size', String(this.pageSize()))
      .set('sort', 'loanDate')
      .set('direction', 'asc')
      .set('personId', String(personId));

    this.http.get<Page<Loan>>(`${this.apiBase}/loans`, { params }).subscribe({
      next: (response) => {
        const loadedLoans = response.items ?? [];
        this.loans.set(loadedLoans);
        this.loadBookCoversForLoans(loadedLoans);
        this.totalElements.set(response.totalElements ?? 0);
        this.totalPages.set(response.totalPages ?? 0);
        this.apiLoading.set(false);
      },
      error: () => {
        this.loans.set([]);
        this.bookCovers.set([]);
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.apiError.set('Kon je uitleningen niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  private loadBookCoversForLoans(loans: Loan[]): void {
    const uniqueBookIds: number[] = [];

    for (const loan of loans) {
      for (const bookId of loan.bookIds ?? []) {
        if (typeof bookId === 'number' && bookId > 0 && !uniqueBookIds.includes(bookId)) {
          uniqueBookIds.push(bookId);
        }
      }
    }

    if (uniqueBookIds.length === 0) {
      this.bookCovers.set([]);
      return;
    }

    const bookCovers: BookCover[] = [];
    let finishedCount = 0;

    for (const bookId of uniqueBookIds) {
      this.http.get<BookCoverResponse>(`${this.apiBase}/books/${bookId}`).subscribe({
        next: (book) => {
          bookCovers.push({ id: bookId, coverImage: book.coverImage });
        },
        error: () => {
          bookCovers.push({ id: bookId });
        },
        complete: () => {
          finishedCount += 1;

          if (finishedCount === uniqueBookIds.length) {
            this.bookCovers.set(bookCovers);
          }
        },
      });
    }
  }
}
