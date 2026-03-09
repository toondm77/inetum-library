import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Book } from '../models/book.model';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './book-card.component.html',
})
export class BookCardComponent {
  @Input({ required: true }) book!: Book;
}
