import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Person } from '../../models/Person';
import { Loan } from '../../models/Loan';

interface BookSummary {
  id: number;
  title: string;
}

@Component({
  selector: 'app-admin-user-detail',
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
  protected readonly loans = signal<Loan[]>([]);
  protected readonly loansLoading = signal(false);
  protected readonly loansError = signal<string | null>(null);
  protected readonly bookTitlesById = signal<Record<number, string>>({});

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
        this.loadLoansForUser(response.loanIds ?? []);
        this.apiLoading.set(false);
      },
      error: () => {
        this.user.set(null);
        this.loans.set([]);
        this.apiError.set('Kon gebruiker niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  private loadLoansForUser(loanIds: number[]): void {
    const uniqueLoanIds: number[] = [];
    for (const id of loanIds ?? []) {
      if (typeof id === 'number' && id > 0 && !uniqueLoanIds.includes(id)) {
        uniqueLoanIds.push(id);
      }
    }

    if (uniqueLoanIds.length === 0) {
      this.loans.set([]);
      this.bookTitlesById.set({});
      this.loansLoading.set(false);
      this.loansError.set(null);
      return;
    }

    this.loansLoading.set(true);
    this.loansError.set(null);
    const loadedLoans: Loan[] = [];
    let finishedCount = 0;
    let failedCount = 0;

    for (const loanId of uniqueLoanIds) {
      this.http.get<Loan>(`${this.apiBase}/loans/${loanId}`).subscribe({
        next: (loan) => {
          loadedLoans.push(loan);
        },
        error: () => {
          failedCount += 1;
        },
        complete: () => {
          finishedCount += 1;

          if (finishedCount === uniqueLoanIds.length) {
            loadedLoans.sort((a, b) => {
              if (a.loanDate === b.loanDate) {
                return 0;
              }
              return a.loanDate > b.loanDate ? -1 : 1;
            });

            this.loans.set(loadedLoans);
            this.loadBookTitlesForLoans(loadedLoans);

            if (failedCount > 0) {
              this.loansError.set('Niet alle uitleningen konden geladen worden.');
            }

            this.loansLoading.set(false);
          }
        },
      });
    }
  }

  protected bookTitle(bookId: number): string {
    return this.bookTitlesById()[bookId] ?? `Boek #${bookId}`;
  }

  private loadBookTitlesForLoans(loans: Loan[]): void {
    const uniqueBookIds: number[] = [];
    for (const loan of loans) {
      for (const bookId of loan.bookIds ?? []) {
        if (typeof bookId === 'number' && bookId > 0 && !uniqueBookIds.includes(bookId)) {
          uniqueBookIds.push(bookId);
        }
      }
    }

    if (uniqueBookIds.length === 0) {
      this.bookTitlesById.set({});
      return;
    }

    const titleMap: Record<number, string> = {};
    let finishedCount = 0;

    for (const bookId of uniqueBookIds) {
      this.http.get<BookSummary>(`${this.apiBase}/books/${bookId}`).subscribe({
        next: (book) => {
          titleMap[bookId] = book.title || `Boek #${bookId}`;
        },
        error: () => {
          titleMap[bookId] = `Boek #${bookId}`;
        },
        complete: () => {
          finishedCount += 1;

          if (finishedCount === uniqueBookIds.length) {
            this.bookTitlesById.set(titleMap);
          }
        },
      });
    }
  }
}
