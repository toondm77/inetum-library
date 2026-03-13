import { ChangeDetectionStrategy, Component, inject, output, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { BarcodeFormat } from '@zxing/library';
import { BrowserMultiFormatReader } from '@zxing/browser';
import { environment } from '../../../../environments/environment';
import { Book } from '../../../models/Book';
import { Page } from '../../../models/Page';
import { PersonService } from '../../../services/person.service';
import { toIsoDateTime } from '../../../utils/date';

@Component({
  selector: 'app-admin-loan-create',
  imports: [CommonModule, ZXingScannerModule],
  templateUrl: './admin-loan-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminLoanCreateComponent {
  private readonly http = inject(HttpClient);
  private readonly personService = inject(PersonService);
  private readonly apiBase = environment.apiBase;

  public readonly allowedFormats = [
    BarcodeFormat.QR_CODE,
    BarcodeFormat.EAN_13,
    BarcodeFormat.EAN_8,
    BarcodeFormat.CODE_128,
  ];

  public readonly closeDialog = output<void>();
  public readonly loanCreated = output<void>();

  protected readonly cart = signal<Book[]>([]);
  protected readonly isScanning = signal(true);
  protected readonly isLoading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly cartCount = computed(() => this.cart().length);

  protected async onFileSelected(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const file = input.files[0];
    const objectUrl = URL.createObjectURL(file);

    try {
      const formatReader = new BrowserMultiFormatReader();
      const result = await formatReader.decodeFromImageUrl(objectUrl);
      this.isLoading.set(false);
      this.onCodeResult(result.getText());
    } catch (err) {
      this.error.set('Kon geen barcode in de afbeelding vinden.');
      this.isLoading.set(false);
    } finally {
      URL.revokeObjectURL(objectUrl);
      input.value = '';
    }
  }

  protected onCodeResult(resultString: string): void {

    if (this.isLoading()) {
      return; 
    }

    if (this.cart().some(b => b.isbn === resultString)) {
      this.error.set(`Boek met barcode ${resultString} zit al in het mandje.`);
      this.isScanning.set(false);
      return;
    }

    this.isScanning.set(false);
    this.isLoading.set(true);
    this.error.set(null);

    this.http.get<Book>(`${this.apiBase}/books/isbn/${resultString}`).subscribe({
      next: (book) => {
        if (book && book.id) {
          if (this.cart().some(b => b.id === book.id)) {
            this.error.set(`Boek "${book.title}" zit al in het mandje.`);
          } else {
            this.cart.update(current => [...current, book]);
          }
        } else {
          this.error.set(`Geen boek gevonden voor barcode: ${resultString}`);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set(`Geen boek gevonden of fout bij zoeken naar barcode: ${resultString}`);
        this.isLoading.set(false);
      }
    });
  }

  protected scanAnother(): void {
    this.error.set(null);
    this.isScanning.set(true);
  }

  protected removeBook(index: number): void {
    this.cart.update(current => {
      const items = [...current];
      items.splice(index, 1);
      return items;
    });
  }

  protected placeOrder(): void {
    if (this.cart().length === 0) return;

    const currentPerson = this.personService.currentPerson();
    if (!currentPerson) {
      this.error.set('Kon uw gebruikersgegevens niet vinden. Zorg dat u ingelogd bent.');
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const bookIds = this.cart().map(b => b.id);
    const payload = {
      loanDate: toIsoDateTime(new Date().toISOString()),
      returnDate: null,
      status: "LOANED", 
      personId: currentPerson.id,
      loanRuleId: null,
      bookIds: bookIds
    };

    this.http.post(`${this.apiBase}/loans`, payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.loanCreated.emit();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Kan de uitlening niet voltooien');
        this.isLoading.set(false);
      }
    });
  }

  protected cancel(): void {
    this.closeDialog.emit();
  }
}
