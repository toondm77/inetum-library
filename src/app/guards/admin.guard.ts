import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoleService } from '../services/role.service';
import { map } from 'rxjs';

export const adminGuard: CanActivateFn = () => {
  const roleService = inject(RoleService);
  const router = inject(Router);

  return roleService.isAdmin$.pipe(
    map(isAdmin => isAdmin ? true : router.createUrlTree(['/home']))
  );
};
