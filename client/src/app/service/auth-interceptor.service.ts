import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor{

  constructor(
    private router: Router, private ngZone: NgZone
  ) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      return next.handle(req).pipe(
        catchError((error: HttpErrorResponse) => {
          if(error.status === 401){
            this.ngZone.run(()=>{
              this.router.navigate(['/login']);
            })
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          return throwError(() => new Error("Unauthorized"));
        })
      )
  }
}
