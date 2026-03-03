import { inject, Injectable } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { map } from 'rxjs';

const ROLES_CLAIM = 'https://rlibrary.com/roles';
const ADMIN_ROLE = 'Verantwoordelijke';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private readonly auth = inject(AuthService);

  readonly isAdmin$ = this.auth.idTokenClaims$.pipe(
    map(claims => {
      const roles: string[] = (claims as any)?.[ROLES_CLAIM] ?? [];
      return roles.includes(ADMIN_ROLE);
    })
  );
}
