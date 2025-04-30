import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Contact } from '../model/contact';
import { ApiResponse } from '../response/apiresponse';

@Injectable({
  providedIn: 'root'
})
export class ContactServiceService {

  constructor(
    private http:HttpClient,private router:Router
  ) { }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if(token){
      console.log('Token found in local store:', token);
      return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }
    else
    {
      console.log('Token not found in local store');
    }
    return new HttpHeaders();
  }

  getContactByUser(id: number): Observable<Contact>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Contact>>('http://localhost:8080/contact/getByUser?idUser'+id, {headers}).pipe(
      map(response=>{
        if(response.success){
          return response.data;
        }
        else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profiles:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      )
    );
  }

  createContactByUser(contact: Contact):Observable<Contact>{
    const headers = this.createAuthorizationHeader();
    return this.http.post<ApiResponse<Contact>>('http://localhost:8080/contact/save', contact, {headers}).pipe(
      map(response=>{
        if(response.success){
          return response.data;
        }
        else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profiles:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      )
    );
  }

  updateContactByUser(contact:Contact):Observable<Contact>{
    const headers = this.createAuthorizationHeader();
    return this.http.put<ApiResponse<Contact>>('http://localhost:8080/contact/update',contact, {headers}).pipe(
      map(response=>{
        if(response.success){
          return response.data;
        }
        else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profiles:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      )
    );
  }
}
