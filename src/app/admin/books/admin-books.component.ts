import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from '../../models/Page';
import { getBookStateLabel, getBookStateColor } from '../../utils/status';
import { Book } from '../../models/Book';

@Component({
  selector: 'app-admin-books',
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-books.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminBooksComponent {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiBase = environment.apiBase;

  protected readonly books = signal<Book[]>([]);
  protected readonly searchQuery = signal('');
  protected readonly sortBy = signal<'status' | 'price' | 'genre'>('price');
  protected readonly sortDirection = signal<'asc' | 'desc'>('asc');
  protected readonly currentPage = signal(0);
  protected readonly pageSize = signal(50);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSizeOptions = [10, 20, 50, 100];
  protected readonly apiLoading = signal(false);
  protected readonly apiError = signal<string | null>(null);
  protected readonly deletingBookId = signal<number | null>(null);

  protected readonly visibleBooks = this.books;

  public bookStateLabel(state: string | undefined): string {
    return getBookStateLabel(state);
  }

  public bookStateColor(state: string | undefined): string {
    const base = 'inline-flex items-center px-3 py-1 rounded-full text-[13px] font-semibold border';
    return `${base} ${getBookStateColor(state)}`;
  }

  constructor() {
    this.loadBooks();
  }

  protected onSearchInput(query: string): void {
    this.searchQuery.set(query);
    this.currentPage.set(0);
    this.loadBooks();
  }

  protected sortByStatus(): void {
    this.toggleSort('status');
  }

  protected sortByPrice(): void {
    this.toggleSort('price');
  }

  protected sortByGenre(): void {
    this.toggleSort('genre');
  }

  protected sortLabel(field: 'status' | 'price' | 'genre'): string {
    if (this.sortBy() !== field) {
      return 'Sorteren';
    }

    return this.sortDirection() === 'asc' ? 'Oplopend' : 'Aflopend';
  }

  private toggleSort(field: 'status' | 'price' | 'genre'): void {
    if (this.sortBy() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(field);
      this.sortDirection.set('asc');
    }

    this.currentPage.set(0);
    this.loadBooks();
  }

  protected prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((page) => page - 1);
      this.loadBooks();
    }
  }

  protected nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((page) => page + 1);
      this.loadBooks();
    }
  }

  protected changePageSize(event: Event): void {
    const value = event.target as HTMLSelectElement;
    this.pageSize.set(Number(value.value));
    this.currentPage.set(0);
    this.loadBooks();
  }

  private backendSortField(): string {
    const activeSort = this.sortBy();
    if (activeSort === 'status') {
      return 'bookState';
    }
    if (activeSort === 'price') {
      return 'purchasePrice';
    }
    return 'theme';
  }

  private loadBooks(): void {
    this.apiLoading.set(true);
    this.apiError.set(null);

    let params = new HttpParams()
      .set('page', String(this.currentPage()))
      .set('size', String(this.pageSize()))
      .set('sort', this.backendSortField())
      .set('direction', this.sortDirection());

    const query = this.searchQuery().trim();
    if (query) {
      params = params.set('title', query);
    }

    this.http.get<Page<Book>>(`${this.apiBase}/books`, { params }).subscribe({
      next: (response) => {
        this.books.set(response.items ?? []);
        this.totalElements.set(response.totalElements ?? 0);
        this.totalPages.set(response.totalPages ?? 0);
        this.apiLoading.set(false);
      },
      error: () => {
        this.books.set([]);
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.apiError.set('Kon boeken niet laden.');
        this.apiLoading.set(false);
      },
    });
  }

  protected openBookDetails(bookId: number, openInEditMode = false): void {
    this.router.navigate(['/books', bookId], {
      queryParams: openInEditMode ? { edit: 1 } : {},
    });
  }

  protected openAddBook(): void {
    this.router.navigate(['/admin/books/add']);
  }

  protected deleteBook(bookId: number): void {
    const isConfirmed = window.confirm('Ben je zeker dat je dit boek wilt verwijderen?');
    if (!isConfirmed) {
      return;
    }

    this.apiError.set(null);
    this.deletingBookId.set(bookId);

    this.http.delete<void>(`${this.apiBase}/books/${bookId}`).subscribe({
      next: () => {
        this.deletingBookId.set(null);

        const remainingItemsOnPage = this.books().length - 1;
        if (remainingItemsOnPage <= 0 && this.currentPage() > 0) {
          this.currentPage.update((page) => page - 1);
        }

        this.loadBooks();
      },
      error: () => {
        this.deletingBookId.set(null);
        this.apiError.set('Verwijderen mislukt. Probeer opnieuw.');
      },
    });
  }
}
