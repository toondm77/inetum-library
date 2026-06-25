import { ChangeDetectionStrategy, Component, inject, signal, computed, OnInit, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormsModule } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import { Loan } from '../../../models/Loan';
import { Person } from '../../../models/Person';
import { Page } from '../../../models/Page';
import { Book } from '../../../models/Book';
import { formatForDateTimeLocalInput, toIsoDateTime } from '../../../utils/date';

@Component({
  selector: 'app-admin-loan-edit',
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FormsModule],
  templateUrl: './admin-loan-edit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminLoanEditComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly el = inject(ElementRef);
  private readonly apiBase = environment.apiBase;

  protected readonly loanId = signal<number | null>(null);
  protected readonly isSaving = signal(false);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly allPersons = signal<Person[]>([]);
  protected readonly personSearchQuery = signal('');
  protected readonly isPersonDropdownOpen = signal(false);

  protected readonly allBooks = signal<Book[]>([]);
  protected readonly bookSearchQuery = signal('');
  protected readonly isBookDropdownOpen = signal(false);
  protected readonly currentBookIds = signal<number[]>([]);
  protected readonly currentPersonId = signal<number | null>(null);

  protected readonly filteredPersons = computed(() => {
    const query = this.personSearchQuery().toLowerCase();
    return this.allPersons().filter(p => 
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(query)
    );
  });

  protected readonly selectedPersonName = computed(() => {
    const personId = this.currentPersonId();
    const person = this.allPersons().find(p => p.id === personId);
    return person ? `${person.firstName} ${person.lastName}` : '';
  });

  protected readonly filteredBooks = computed(() => {
    const query = this.bookSearchQuery().toLowerCase();
    const excludeIds = this.currentBookIds();
    return this.allBooks().filter(b =>
      !excludeIds.includes(b.id) &&
      (b.title.toLowerCase().includes(query) || b.isbn?.toLowerCase().includes(query))
    );
  });

  // Helper method to get book titles for the display
  protected getBookTitle(id: number): string {
    const book = this.allBooks().find(b => b.id === id);
    return book ? book.title : 'Onbekend boek (' + id + ')';
  }

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if (!this.el.nativeElement.contains(event.target)) {
      this.isPersonDropdownOpen.set(false);
      this.isBookDropdownOpen.set(false);
    }
  }

  protected readonly loanForm = this.fb.nonNullable.group({
    loanDate: ['', Validators.required],
    returnDate: [''],
    status: ['LOANED', Validators.required],
    personId: [0, Validators.required],
    loanRuleId: [1],
    bookIds: [[] as number[]]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = parseInt(idParam, 10);
      this.loanId.set(id);
      
      this.http.get<Page<Person>>(`${this.apiBase}/persons?size=1000`).subscribe(res => this.allPersons.set(res.items ?? []));
      this.http.get<Page<Book>>(`${this.apiBase}/books?size=1000`).subscribe(res => this.allBooks.set(res.items ?? []));
      
      this.loadLoan(id);
    } else {
      this.error.set('Geen uitlening ID gevonden.');
      this.isLoading.set(false);
    }
  }

  private loadLoan(id: number): void {
    this.http.get<Loan>(`${this.apiBase}/loans/${id}`).subscribe({
      next: (loan) => {
        this.loanForm.patchValue({
          loanDate: formatForDateTimeLocalInput(loan.loanDate),
          returnDate: formatForDateTimeLocalInput(loan.returnDate),
          status: loan.status ?? 'LOANED',
          personId: loan.personId,
          loanRuleId: loan.loanRuleId ?? null,
          bookIds: loan.bookIds ?? []
        });

        this.currentBookIds.set(loan.bookIds ?? []);
        this.currentPersonId.set(loan.personId);

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Fout bij ophalen uitlening', err);
        this.error.set('Kan uitlening niet laden.');
        this.isLoading.set(false);
      }
    });
  }

  protected removeBookId(index: number): void {
    const currentBookIds = this.loanForm.get('bookIds')?.value ?? [];
    const updated = [...currentBookIds];
    updated.splice(index, 1);
    this.loanForm.patchValue({ bookIds: updated });
    this.currentBookIds.set(updated);
  }

  protected selectPerson(person: Person): void {
    this.loanForm.patchValue({ personId: person.id });
    this.currentPersonId.set(person.id);
    this.personSearchQuery.set('');
    this.isPersonDropdownOpen.set(false);
  }

  protected addBook(book: Book): void {
    const currentBookIds = this.loanForm.get('bookIds')?.value ?? [];
    if (!currentBookIds.includes(book.id)) {
      const updated = [...currentBookIds, book.id];
      this.loanForm.patchValue({ bookIds: updated });
      this.currentBookIds.set(updated);
    }
    this.bookSearchQuery.set('');
    this.isBookDropdownOpen.set(false);
  }

  protected save(): void {
    if (this.loanForm.invalid || !this.loanId()) {
      return;
    }

    this.isSaving.set(true);
    this.error.set(null);

    const values = this.loanForm.getRawValue();
    const payload = {
      ...values,
      loanDate: toIsoDateTime(values.loanDate),
      returnDate: toIsoDateTime(values.returnDate),
      loanRuleId: values.loanRuleId || null
    };

    this.http.put(`${this.apiBase}/loans/${this.loanId()}`, payload).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.router.navigate(['/admin/loans']);
      },
      error: (err) => {
        console.error('Fout bij opslaan:', err);
        this.error.set('Kon gegevens niet opslaan. Controleer de velden.');
        this.isSaving.set(false);
      }
    });
  }
}