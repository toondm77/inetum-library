import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { environment } from '../../environments/environment';
import { RoleService } from '../services/role.service';
import { BookUpdatePayload } from '../models/book-update-payload.model';
import { Book } from '../models/Book';
import { Loan } from '../models/Loan';
import { getBookStateLabel } from '../utils/status';

interface AuthorOption {
  id: number;
  firstName: string;
  lastName: string;
}

interface LibraryOption {
  id: number;
  name: string;
}

interface PagedResponse<T> {
  items: T[];
}

@Component({
  selector: 'app-book-detail',
  imports: [RouterLink, ReactiveFormsModule, DatePipe],
  templateUrl: './book-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookDetailComponent {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly roleService = inject(RoleService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly apiBase = environment.apiBase;
  private readonly subscriptions: Subscription[] = [];

  protected readonly isAdmin = signal(false);
  protected readonly editRequested = signal(false);
  protected readonly isEditMode = computed(() => this.isAdmin() && this.editRequested());
  protected readonly apiLoading = signal(true);
  protected readonly apiError = signal<string | null>(null);
  protected readonly saveError = signal<string | null>(null);
  protected readonly saveSuccess = signal<string | null>(null);
  protected readonly isSaving = signal(false);
  protected readonly book = signal<Book | null>(null);
  protected readonly loans = signal<Loan[]>([]);
  protected readonly loansLoading = signal(false);
  protected readonly optionsLoading = signal(false);
  protected readonly authors = signal<AuthorOption[]>([]);
  protected readonly libraries = signal<LibraryOption[]>([]);
  private readonly authorsLoaded = signal(false);
  private readonly librariesLoaded = signal(false);
  protected readonly canSave = computed(() => this.isEditMode() && this.editForm.valid && !this.isSaving());

  protected readonly editForm = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required]],
    description: [''],
    isbn: ['', [Validators.required]],
    authorId: [1, [Validators.required, Validators.min(1)]],
    libraryId: [1, [Validators.required, Validators.min(1)]],
    publishedYear: [2000, [Validators.required, Validators.min(0)]],
    amountOfPages: [0, [Validators.required, Validators.min(0)]],
    releaseDate: [''],
    theme: ['', [Validators.required]],
    bookState: ['AVAILABLE', [Validators.required]],
    ageCategory: [''],
    purchasePrice: [0, [Validators.required, Validators.min(0)]],
    duplicates: [0, [Validators.required, Validators.min(0)]],
    coverImage: [''],
  });

  constructor() {
    this.subscriptions.push(
      this.roleService.isAdmin$.subscribe((isAdmin) => {
        this.isAdmin.set(isAdmin);
        if (isAdmin && this.editRequested()) {
          this.loadEditOptionsIfNeeded();
        }
      })
    );

    this.subscriptions.push(
      this.route.queryParamMap.subscribe((params) => {
        const editRequested = params.get('edit') === '1';
        this.editRequested.set(editRequested);

        if (editRequested && this.isAdmin()) {
          this.loadEditOptionsIfNeeded();
        }
      })
    );

    this.subscriptions.push(
      this.route.paramMap.subscribe((params) => {
        const bookId = params.get('id');
        if (bookId) {
          this.loadBook(bookId)
          this.loadLoansIfAdmin(bookId);
        }
      })
    );

    this.destroyRef.onDestroy(() => {
      for (const sub of this.subscriptions) {
        sub.unsubscribe();
      }
    });
  }

  private loadBook(id: string): void {
    this.apiLoading.set(true);
    this.apiError.set(null);

    this.subscriptions.push(
      this.http.get<Book>(`${this.apiBase}/books/${id}`).subscribe({
        next: (book) => {
          this.book.set(book);
          this.resetFormFromBook(book);
          this.apiLoading.set(false);
        },
        error: () => {
          this.apiError.set('Kon boek niet laden.');
          this.apiLoading.set(false);
        },
      })
    );
  }

  private loadLoansIfAdmin(id: string): void {
    this.subscriptions.push(
      this.roleService.isAdmin$.subscribe((isAdmin) => {
        if (isAdmin) {
          this.fetchLoans(id);
        }
      })
    );
  }

  private fetchLoans(id: string): void {
    this.loansLoading.set(true);

    this.subscriptions.push(
      this.http.get<PagedResponse<Loan>>(`${this.apiBase}/loans/book/${id}`).subscribe({
        next: (response) => {
          this.loans.set(response.items || []);
          this.loansLoading.set(false);
        },
        error: () => {
          this.loansLoading.set(false);
        },
      })
    );
  }

  protected availabilityLabel(state?: string): string {
    return getBookStateLabel(state);
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

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = () => {
        let base64 = reader.result as string;
        const commaIndex = base64.indexOf(',');
        if (commaIndex !== -1) {
          base64 = base64.substring(commaIndex + 1);
        }
        this.editForm.patchValue({ coverImage: base64 });
        this.editForm.get('coverImage')?.markAsDirty();
      };

      reader.readAsDataURL(file);
    }
  }

  protected saveBook(): void {
    const currentBook = this.book();
    if (!currentBook || !this.isEditMode()) {
      return;
    }

    this.editForm.markAllAsTouched();
    if (this.editForm.invalid) {
      return;
    }

    this.isSaving.set(true);
    this.saveError.set(null);
    this.saveSuccess.set(null);

    const payload = this.createPayload(currentBook);
    if (!payload) {
      this.saveError.set('Selecteer een geldige auteur en bibliotheek.');
      this.isSaving.set(false);
      return;
    }

    this.subscriptions.push(
      this.http.put<Book>(`${this.apiBase}/books/${currentBook.id}`, payload).subscribe({
        next: (updatedBook) => {
          this.book.set(updatedBook);
          this.resetFormFromBook(updatedBook);
          this.saveSuccess.set('Boek succesvol opgeslagen.');
          this.isSaving.set(false);
        },
        error: () => {
          this.saveError.set('Opslaan mislukt. Probeer opnieuw.');
          this.isSaving.set(false);
        },
      })
    );
  }

  protected cancelEdit(): void {
    const currentBook = this.book();
    if (!currentBook) {
      return;
    }

    this.resetFormFromBook(currentBook);
    this.saveError.set(null);
    this.saveSuccess.set(null);
    this.exitEditMode();
  }

  protected enterEditMode(): void {
    if (!this.isAdmin()) {
      return;
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { edit: 1 },
      queryParamsHandling: 'merge',
    });
  }

  protected exitEditMode(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { edit: null },
      queryParamsHandling: 'merge',
    });
  }

  private resetFormFromBook(book: Book): void {
    this.editForm.reset({
      title: book.title ?? '',
      description: book.description ?? '',
      isbn: book.isbn ?? '',
      authorId: this.ensurePositiveNumber(book.authorId, 1),
      libraryId: this.ensurePositiveNumber(book.libraryId, 1),
      publishedYear: book.publishedYear ?? 0,
      amountOfPages: book.amountOfPages ?? 0,
      releaseDate: book.releaseDate ?? '',
      theme: book.theme ?? '',
      bookState: book.bookState ?? 'AVAILABLE',
      ageCategory: book.ageCategory ?? '',
      purchasePrice: book.purchasePrice ?? 0,
      duplicates: book.duplicates ?? 0,
      coverImage: book.coverImage ?? '',
    });
  }

  private loadEditOptionsIfNeeded(): void {
    if (this.optionsLoading()) {
      return;
    }

    const shouldLoadAuthors = !this.authorsLoaded();
    const shouldLoadLibraries = !this.librariesLoaded();

    if (!shouldLoadAuthors && !shouldLoadLibraries) {
      return;
    }

    let pendingRequests = 0;
    const finishRequest = () => {
      pendingRequests -= 1;
      if (pendingRequests === 0) {
        this.optionsLoading.set(false);
      }
    };

    this.optionsLoading.set(true);

    if (shouldLoadAuthors) {
      pendingRequests += 1;
      this.subscriptions.push(
        this.http
          .get<PagedResponse<AuthorOption>>(`${this.apiBase}/authors`, {
            params: {
              page: 0,
              size: 200,
              sort: 'lastName',
              direction: 'asc',
            },
          })
          .subscribe({
            next: (response) => {
              this.authors.set(response.items ?? []);
              this.authorsLoaded.set(true);
              finishRequest();
            },
            error: () => {
              this.authors.set([]);
              finishRequest();
            },
          })
      );
    }

    if (shouldLoadLibraries) {
      pendingRequests += 1;
      this.subscriptions.push(
        this.http
          .get<PagedResponse<LibraryOption>>(`${this.apiBase}/libraries`, {
            params: {
              page: 0,
              size: 200,
              sort: 'name',
              direction: 'asc',
            },
          })
          .subscribe({
            next: (response) => {
              this.libraries.set(response.items ?? []);
              this.librariesLoaded.set(true);
              finishRequest();
            },
            error: () => {
              this.libraries.set([]);
              finishRequest();
            },
          })
      );
    }
  }

  protected authorLabel(author: AuthorOption): string {
    return `${author.firstName} ${author.lastName}`.trim();
  }

  private createPayload(currentBook: Book): BookUpdatePayload | null {
    const formValue = this.editForm.getRawValue();
    const authorId = this.ensurePositiveNumber(formValue.authorId, currentBook.authorId);
    const libraryId = this.ensurePositiveNumber(formValue.libraryId, currentBook.libraryId);
    const selectedAuthorExists = this.authors().some((author) => author.id === authorId);
    const selectedLibraryExists = this.libraries().some((library) => library.id === libraryId);

    if (authorId <= 0 || libraryId <= 0 || !selectedAuthorExists || !selectedLibraryExists) {
      return null;
    }

    return {
      title: formValue.title,
      description: formValue.description,
      isbn: formValue.isbn,
      authorId,
      libraryId,
      publishedYear: this.ensureNumber(formValue.publishedYear, currentBook.publishedYear),
      amountOfPages: this.ensureNumber(formValue.amountOfPages, currentBook.amountOfPages ?? 0),
      releaseDate: formValue.releaseDate,
      theme: formValue.theme,
      bookState: formValue.bookState,
      ageCategory: formValue.ageCategory,
      purchasePrice: this.ensureNumber(formValue.purchasePrice, currentBook.purchasePrice ?? 0),
      duplicates: this.ensureNumber(formValue.duplicates, currentBook.duplicates ?? 0),
      coverImage: formValue.coverImage || undefined,
    };
  }

  private ensureNumber(value: unknown, fallback: number): number {
    const parsed = typeof value === 'number' ? value : Number(value);
    return Number.isFinite(parsed) ? parsed : fallback;
  }

  private ensurePositiveNumber(value: unknown, fallback: number): number {
    const normalized = this.ensureNumber(value, fallback);
    return normalized > 0 ? normalized : fallback;
  }
}
