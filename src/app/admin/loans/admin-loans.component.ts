import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Loan } from '../../models/Loan';
import { Page } from '../../models/Page';
import { getLoanStatusLabel, getLoanStatusColor } from '../../utils/status';

import { AdminLoanCreateComponent } from './create/admin-loan-create.component';

interface BookSummary {
  id: number;
  title: string;
}

@Component({
  selector: 'app-admin-loans',
  imports: [CommonModule, RouterLink, AdminLoanCreateComponent],
  templateUrl: './admin-loans.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminLoansComponent {
  private readonly http = inject(HttpClient);
  private readonly apiBase = environment.apiBase;

  protected readonly isCreatingLoan = signal(false);

  protected readonly loans = signal<Loan[]>([]);
  protected readonly apiLoading = signal(false);
  protected readonly apiError = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly currentPage = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly bookTitlesById = signal<Record<number, string>>({});

  protected readonly loanToDelete = signal<Loan | null>(null);
  protected readonly isDeleting = signal(false);

  constructor() {
    this.loadRecentLoans();
  }

  protected loadRecentLoans(): void {
    this.apiLoading.set(true);
    this.apiError.set(null);

    const params = new HttpParams()
      .set('page', String(this.currentPage()))
      .set('size', String(this.pageSize()))
      .set('sort', 'loanDate')
      .set('direction', 'desc');

    this.http.get<Page<Loan>>(`${this.apiBase}/loans`, { params }).subscribe({
      next: (response) => {
        const loadedLoans = response.items ?? [];
        this.loans.set(loadedLoans);
        this.loadBookTitlesForLoans(loadedLoans);
        this.totalElements.set(response.totalElements ?? 0);
        this.totalPages.set(response.totalPages ?? 0);
        this.apiLoading.set(false);
      },
      error: () => {
        this.loans.set([]);
        this.bookTitlesById.set({});
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.apiError.set('Kon recente uitleningen niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  protected prevPage(): void {
    if (this.currentPage() === 0) {
      return;
    }

    this.currentPage.update((page) => page - 1);
    this.loadRecentLoans();
  }

  protected nextPage(): void {
    if (this.currentPage() >= this.totalPages() - 1) {
      return;
    }

    this.currentPage.update((page) => page + 1);
    this.loadRecentLoans();
  }

  protected formatDate(value: string): string {
    if (!value) {
      return '-';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toLocaleString('nl-BE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  protected confirmDelete(loan: Loan): void {
    this.loanToDelete.set(loan);
  }

  protected executeDelete(): void {
    const loan = this.loanToDelete();
    if (!loan) return;

    this.isDeleting.set(true);
    this.http.delete(`${this.apiBase}/loans/${loan.id}`).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.loanToDelete.set(null);
        this.loadRecentLoans();
      },
      error: (err) => {
        console.error('Kon uitlening niet verwijderen', err);
        this.isDeleting.set(false);
        this.loanToDelete.set(null);
        this.apiError.set('Fout bij het verwijderen van de uitlening.');
      }
    });
  }

  protected statusLabel(status: string): string {
    return getLoanStatusLabel(status);
  }

  protected statusClass(status: string): string {
    const base = 'inline-flex items-center px-3 py-1 rounded-full text-[13px] font-semibold border';
    return `${base} ${getLoanStatusColor(status)}`;
  }

  protected personLabel(loan: Loan): string {
    if (loan.personName && loan.personName.trim().length > 0) {
      return loan.personName;
    }

    return `Persoon #${loan.personId}`;
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
