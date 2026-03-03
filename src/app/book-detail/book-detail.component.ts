import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter, map, switchMap, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Book } from '../models/book.model';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookDetailComponent {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private readonly apiBase = environment.apiBase;

  protected readonly apiLoading = signal(true);
  protected readonly apiError = signal<string | null>(null);
  protected readonly book = signal<Book | null>(null);

  constructor() {
    this.route.paramMap
      .pipe(
        map((params) => params.get('id')),
        filter((id): id is string => !!id),
        tap(() => {
          this.apiLoading.set(true);
          this.apiError.set(null);
        }),
        switchMap((id) => this.http.get<Book>(`${this.apiBase}/books/${id}`)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (book) => {
          this.book.set(book);
          this.apiLoading.set(false);
        },
        error: () => {
          this.apiError.set('Kon boek niet laden.');
          this.apiLoading.set(false);
        },
      });
  }

  protected availabilityLabel(state?: string): string {
    if (!state) return 'Onbekend';
    return state === 'AVAILABLE' ? 'Beschikbaar' : 'Niet beschikbaar';
  }

  protected availabilityTone(state?: string): string {
    return state === 'AVAILABLE'
      ? 'bg-emerald-100 text-emerald-700 border-emerald-200'
      : 'bg-amber-100 text-amber-700 border-amber-200';
  }

  protected formatReleaseDate(date?: string): string {
    if (!date) return 'Onbekend';
    const parsed = new Date(date);
    return Number.isNaN(parsed.getTime()) ? date : parsed.toLocaleDateString();
  }
}
