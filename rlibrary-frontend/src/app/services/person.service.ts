import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '@auth0/auth0-angular';
import { Person } from '../models/Person';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly apiBase = environment.apiBase;

  public readonly currentPerson = signal<Person | null>(null);

  constructor() {
    this.auth.user$.subscribe(user => {
      if (user?.sub) {
        this.fetchPersonByAuth0Id(user.sub);
      } else {
        this.currentPerson.set(null);
      }
    });
  }

  private fetchPersonByAuth0Id(auth0Id: string): void {
    this.http.get<Person>(`${this.apiBase}/persons/auth0/${auth0Id}`).subscribe({
      next: (person) => {
        this.currentPerson.set(person);
      },
      error: (err) => {
        console.error('Failed to fetch person from auth0 ID: ', err);

        this.currentPerson.set(null);
      }
    });
  }
}
