import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { BookCardComponent } from '../book-card/book-card.component';
import { Page } from '../models/Page';
import { Book } from '../models/Book';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './homepage.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Homepage {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  protected readonly apiResult = signal<string | null>(null);
  protected readonly apiError = signal<string | null>(null);
  protected readonly apiLoading = signal(false);

  protected readonly themeFilterOpen = signal(false);
  protected readonly pagesFilterOpen = signal(false);
  protected readonly yearFilterOpen = signal(false);

  protected readonly searchQuery = signal('');
  protected readonly suggestions = signal<Book[]>([]);
  protected readonly books = signal<Book[]>([]);
  protected readonly showSuggestions = signal(false);

  protected readonly currentPage = signal(0);
  protected readonly pageSize = signal(10);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSizeOptions = [10, 20, 50];

  protected readonly selectedTheme = signal<string | null>(null);
  protected readonly themeOptions = [
    { label: 'Computerwetenschappen', value: 'COMPUTERSCIENCE' },
    { label: 'Rechten', value: 'LAWS' },
    { label: 'Communicatie', value: 'COMMUNICATION' },
    { label: 'Business', value: 'BUSINESS' },
    { label: 'Financiën', value: 'FINANCE' },
  ];

  private readonly apiBase = environment.apiBase;

  private closeFilters(): void {
    this.pagesFilterOpen.set(false);
    this.yearFilterOpen.set(false);
  }

  protected toggleThemeFilter(): void {
    const next = !this.themeFilterOpen();
    if (next) {
      this.closeFilters();
    }
    this.themeFilterOpen.set(next);
  }

  protected selectTheme(themeValue: string): void {
    if (this.selectedTheme() === themeValue) {
      this.selectedTheme.set(null);
    } else {
      this.selectedTheme.set(themeValue);
      this.themeFilterOpen.set(false);
    }
    this.currentPage.set(0);
    this.searchBooks();
  }

  protected togglePagesFilter(): void {
    const next = !this.pagesFilterOpen();
    this.closeFilters();
    this.pagesFilterOpen.set(next);
  }

  protected toggleYearFilter(): void {
    const next = !this.yearFilterOpen();
    this.closeFilters();
    this.yearFilterOpen.set(next);
  }

  protected onSearchInput(event: Event): void {
    const query = (event.target as HTMLInputElement).value;
    this.searchQuery.set(query);

    if (query.length < 2) {
      this.suggestions.set([]);
      this.showSuggestions.set(false);
      return;
    }
    const params = new HttpParams()
      .set('page', '0')
      .set('size', '5')
      .set('title', query);

    this.http.get<Page<Book>>(`${this.apiBase}/books`, { params }).subscribe({
      next: (page) => {
        const uniqueBooks = Array.from(
          new Map(page.items.map((book) => [book.id, book])).values()
        );
        this.suggestions.set(uniqueBooks);
        this.showSuggestions.set(uniqueBooks.length > 0);
      },
      error: () => {
        this.suggestions.set([]);
        this.showSuggestions.set(false);
      }
    });
  }

  protected selectSuggestion(book: Book): void {
    this.searchQuery.set(book.title);
    this.showSuggestions.set(false);
    this.goToBookDetail(book.id);
  }

  protected onSearchSubmit(): void {
    this.showSuggestions.set(false);
    this.currentPage.set(0);
    this.searchBooks();
  }

  protected closeSuggestions(): void {
    setTimeout(() => this.showSuggestions.set(false), 200);
  }

  protected goToBookDetail(bookId: number): void {
    this.router.navigate(['/books', bookId]);
  }

  protected prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((p) => p - 1);
      this.searchBooks();
    }
  }

  protected nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((p) => p + 1);
      this.searchBooks();
    }
  }

  protected changePageSize(event: Event): void {
    const size = Number((event.target as HTMLSelectElement).value);
    this.pageSize.set(size);
    this.currentPage.set(0);
    this.searchBooks();
  }

  private searchBooks(): void {
    const title = this.searchQuery();
    const theme = this.selectedTheme();

    if (!title && !theme) return;

    this.apiLoading.set(true);
    this.apiError.set(null);

    let params = new HttpParams()
      .set('page', this.currentPage().toString())
      .set('size', this.pageSize().toString())
      .set('sort', 'title')
      .set('direction', 'desc')
      .set('minAmountOfPages', '1')
      .set('maxAmountOfPages', '1111');
    
    if (title) {
      params = params.set('title', title);
    }
    if (theme) {
      params = params.set('themeType', theme);
    }

    this.http.get<Page<Book>>(`${this.apiBase}/books`, { params }).subscribe({
      next: (page) => {
        this.books.set(page.items);
        this.totalElements.set(page.totalElements || 0); 
        this.totalPages.set(page.totalPages || 0);    
        this.apiLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.apiError.set('Failed to load books.');
        this.apiLoading.set(false);
      }
    });
  }
}
