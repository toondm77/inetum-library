import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Book } from '../../models/Book';

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
  selector: 'app-admin-add-book',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-add-book.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminAddBookComponent {
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiBase = environment.apiBase;

  protected readonly bookStates = [
    'AVAILABLE',
    'TEMPORARILYUNAVAILABLE',
    'PERMANENTLYUNAVAILABLE',
    'LOST',
    'BORROWED',
  ] as const;

  protected readonly themes = [
    'FINANCE',
    'LAWS',
    'COMMUNICATION',
    'BUSINESS',
    'COMPUTERSCIENCE',
    'IT',
  ] as const;

  protected readonly saving = signal(false);
  protected readonly optionsLoading = signal(false);
  protected readonly submitAttempted = signal(false);
  protected readonly submitError = signal<string | null>(null);
  protected readonly submitSuccess = signal<string | null>(null);
  protected readonly authors = signal<AuthorOption[]>([]);
  protected readonly libraries = signal<LibraryOption[]>([]);
  protected readonly addAuthorMode = signal(false);
  protected readonly authorSubmitAttempted = signal(false);
  protected readonly authorSaving = signal(false);
  protected readonly authorError = signal<string | null>(null);
  protected readonly todayDate = new Date().toISOString().slice(0, 10);

  private readonly currentYear = new Date().getFullYear();

  protected readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]],
    description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
    isbn: ['', [Validators.required, Validators.pattern(/^(?:\d{10}|\d{13})$/)]],
    authorId: [null as number | null, [Validators.required, Validators.min(1)]],
    libraryId: [null as number | null, [Validators.required, Validators.min(1)]],
    publishedYear: [null as number | null, [Validators.required, Validators.min(1450), Validators.max(this.currentYear + 1)]],
    amountOfPages: [null as number | null, [Validators.required, Validators.min(1), Validators.max(10000)]],
    releaseDate: ['', [Validators.required]],
    theme: ['FINANCE', [Validators.required, Validators.pattern(/^[A-Z_]{2,40}$/)]],
    bookState: ['AVAILABLE', [Validators.required]],
    ageCategory: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40)]],
    purchasePrice: [0, [Validators.required, Validators.min(0), Validators.max(99999)]],
    coverImage: [''],
  }, { validators: [this.releaseYearMatchesPublishedYearValidator()] });

  protected readonly authorForm = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
    nationality: ['', [Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(1000)]],
    birthDate: ['', [Validators.required]],
  });

  protected readonly releaseYearMismatch = computed(() =>
    (this.submitAttempted() || this.form.touched) && this.form.hasError('releaseYearMismatch')
  );

  constructor() {
    effect(() => {
      const shouldDisableOptions = this.optionsLoading();
      const authorControl = this.form.controls.authorId;
      const libraryControl = this.form.controls.libraryId;

      if (shouldDisableOptions) {
        authorControl.disable({ emitEvent: false });
        libraryControl.disable({ emitEvent: false });
      } else {
        authorControl.enable({ emitEvent: false });
        libraryControl.enable({ emitEvent: false });
      }
    });

    this.loadOptions();
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
        this.form.patchValue({ coverImage: base64 });
        this.form.get('coverImage')?.markAsDirty();
      };

      reader.readAsDataURL(file);
    }
  }

  protected submit(): void {
    this.submitAttempted.set(true);
    this.submitError.set(null);
    this.submitSuccess.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.submitError.set(
        this.form.hasError('releaseYearMismatch')
          ? 'Publicatiejaar moet gelijk zijn aan het jaar in de release datum.'
          : 'Controleer de gemarkeerde velden en probeer opnieuw.'
      );
      return;
    }

    const value = this.form.getRawValue();
    const selectedAuthor = this.authors().find((author) => author.id === Number(value.authorId));
    const selectedLibrary = this.libraries().find((library) => library.id === Number(value.libraryId));

    if (!selectedAuthor || !selectedLibrary) {
      this.submitError.set('Selecteer een geldige auteur en bibliotheek.');
      return;
    }

    const payload: Book = {
      id: 0,
      title: value.title!.trim(),
      description: value.description!.trim(),
      authorName: this.authorLabel(selectedAuthor),
      isbn: value.isbn!.trim(),
      authorId: Number(value.authorId),
      libraryId: Number(value.libraryId),
      libraryName: selectedLibrary.name,
      publishedYear: Number(value.publishedYear),
      amountOfPages: Number(value.amountOfPages),
      releaseDate: value.releaseDate!,
      theme: value.theme!.trim().toUpperCase(),
      bookState: value.bookState!,
      ageCategory: value.ageCategory!.trim(),
      purchasePrice: Number(value.purchasePrice),
      duplicates: 1,
      coverImage: value.coverImage || undefined,
    };

    this.saving.set(true);

    this.http.post<Book>(`${this.apiBase}/books`, payload).subscribe({
      next: () => {
        this.saving.set(false);
        this.submitSuccess.set('Boek is toegevoegd.');
        this.router.navigate(['/admin/books']);
      },
      error: (error) => {
        this.saving.set(false);
        this.submitError.set(this.extractHttpErrorMessage(error, 'Toevoegen mislukt. Controleer je gegevens of probeer later opnieuw.'));
      },
    });
  }

  protected toggleAddAuthorMode(): void {
    this.addAuthorMode.update((current) => !current);
    this.authorSubmitAttempted.set(false);
    this.authorError.set(null);
  }

  protected createAuthor(): void {
    this.authorSubmitAttempted.set(true);
    this.authorError.set(null);

    if (this.authorForm.invalid) {
      this.authorForm.markAllAsTouched();
      this.authorError.set('Controleer de auteurgegevens en probeer opnieuw.');
      return;
    }

    const value = this.authorForm.getRawValue();
    const payload: AuthorCreatePayload = {
      firstName: value.firstName!.trim(),
      lastName: value.lastName!.trim(),
      nationality: value.nationality?.trim() ?? '',
      description: value.description?.trim() ?? '',
      birthDate: value.birthDate!,
    };

    this.authorSaving.set(true);

    this.http.post<AuthorOption>(`${this.apiBase}/authors`, payload).subscribe({
      next: (createdAuthor) => {
        this.authors.update((authors) =>
          [...authors, createdAuthor].sort((a, b) => this.authorLabel(a).localeCompare(this.authorLabel(b)))
        );
        this.form.controls.authorId.setValue(createdAuthor.id);
        this.authorForm.reset({
          firstName: '',
          lastName: '',
          nationality: '',
          description: '',
          birthDate: '',
        });
        this.authorSubmitAttempted.set(false);
        this.addAuthorMode.set(false);
        this.authorSaving.set(false);
      },
      error: (error) => {
        this.authorSaving.set(false);
        this.authorError.set(this.extractHttpErrorMessage(error, 'Auteur aanmaken mislukt. Controleer de gegevens.'));
      },
    });
  }

  protected showControlError(control: { touched: boolean; invalid: boolean }): boolean {
    return (this.submitAttempted() || control.touched) && control.invalid;
  }

  protected showAuthorControlError(control: { touched: boolean; invalid: boolean }): boolean {
    return (this.authorSubmitAttempted() || control.touched) && control.invalid;
  }

  protected authorLabel(author: AuthorOption): string {
    return `${author.firstName} ${author.lastName}`.trim();
  }

  protected backToBooks(): void {
    this.router.navigate(['/admin/books']);
  }

  private releaseYearMatchesPublishedYearValidator(): ValidatorFn {
    return (group) => {
      const publishedYear = group.get('publishedYear')?.value;
      const releaseDate = group.get('releaseDate')?.value;

      if (!publishedYear || !releaseDate) {
        return null;
      }

      const releaseYear = new Date(releaseDate).getFullYear();
      if (Number.isNaN(releaseYear)) {
        return null;
      }

      return Number(publishedYear) === releaseYear ? null : { releaseYearMismatch: true };
    };
  }

  private loadOptions(): void {
    this.optionsLoading.set(true);

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
          this.optionsLoading.set(false);
        },
        error: () => {
          this.authors.set([]);
          this.optionsLoading.set(false);
        },
      });

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
        },
        error: () => {
          this.libraries.set([]);
        },
      });
  }

  private extractHttpErrorMessage(error: unknown, fallback: string): string {
    if (!(error instanceof HttpErrorResponse)) {
      return fallback;
    }

    if (typeof error.error === 'string' && error.error.trim().length > 0) {
      return error.error;
    }

    if (error.error && typeof error.error === 'object') {
      const candidate =
        (error.error as Record<string, unknown>)['message'] ??
        (error.error as Record<string, unknown>)['error'] ??
        (error.error as Record<string, unknown>)['title'];

      if (typeof candidate === 'string' && candidate.trim().length > 0) {
        return candidate;
      }
    }

    if (typeof error.message === 'string' && error.message.trim().length > 0) {
      return error.message;
    }

    return fallback;
  }
}
