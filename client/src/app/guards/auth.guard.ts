import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})

export class AuthGuard implements CanActivate{
  constructor(private router: Router){}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
      const userString = localStorage.getItem('userCurrent')

      if(!userString){
        this.router.navigate(['/login'])
        return false;
      }

      const user = JSON.parse(userString);
      const allowedRoles: string[] = route.data['role'];

      if(allowedRoles && !allowedRoles.includes(user.role)){
        this.router.navigate(['/access-denied'])
        return false;
      }

      return true;
  }
}
