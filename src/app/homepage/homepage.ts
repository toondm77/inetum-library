import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Book, Page } from '../models/book.model';
import { BookCardComponent } from '../book-card/book-card.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './homepage.html',
  styleUrl: './homepage.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Homepage {
  private readonly http = inject(HttpClient);

  protected readonly apiResult = signal<string | null>(null);
  protected readonly apiError = signal<string | null>(null);
  protected readonly apiLoading = signal(false);

  protected readonly themeFilterOpen = signal(false);
  protected readonly pagesFilterOpen = signal(false);
  protected readonly yearFilterOpen = signal(false);

  protected readonly searchQuery = signal('');
  protected readonly suggestions = signal<string[]>([]);
  protected readonly books = signal<Book[]>([]);
  protected readonly showSuggestions = signal(false);

  private readonly apiBase = environment.apiBase;

  private closeFilters(): void {
    this.themeFilterOpen.set(false);
    this.pagesFilterOpen.set(false);
    this.yearFilterOpen.set(false);
  }

  protected toggleThemeFilter(): void {
    const next = !this.themeFilterOpen();
    this.closeFilters();
    this.themeFilterOpen.set(next);
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
        const titles = page.items.map(b => b.title);
        const uniqueTitles = [...new Set(titles)];
        this.suggestions.set(uniqueTitles);
        this.showSuggestions.set(uniqueTitles.length > 0);
      },
      error: () => {
        this.suggestions.set([]);
        this.showSuggestions.set(false);
      }
    });
  }

  protected selectSuggestion(title: string): void {
    this.searchQuery.set(title);
    this.showSuggestions.set(false);
    this.searchBooks(title);
  }

  protected onSearchSubmit(): void {
    this.showSuggestions.set(false);
    this.searchBooks(this.searchQuery());
  }

  protected closeSuggestions(): void {
    setTimeout(() => this.showSuggestions.set(false), 200);
  }

  private searchBooks(title: string): void {
    if (!title) return;
    
    this.apiLoading.set(true);
    this.apiError.set(null);

    const params = new HttpParams()
      .set('page', '0')
      .set('size', '20')
      .set('sort', 'title')
      .set('direction', 'desc')
      .set('title', title)
      .set('minAmountOfPages', '1')
      .set('maxAmountOfPages', '1111');

    this.http.get<Page<Book>>(`${this.apiBase}/books`, { params }).subscribe({
      next: (page) => {
        this.books.set(page.items);
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
