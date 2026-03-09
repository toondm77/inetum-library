import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Book, Page } from '../../models/book.model';

@Component({
  selector: 'app-admin-books',
  imports: [CommonModule],
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

  protected readonly visibleBooks = this.books;

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

  protected openBookDetails(bookId: number): void {
    this.router.navigate(['/books', bookId]);
  }
}
