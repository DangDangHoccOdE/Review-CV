import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../model/user';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ApiResponse } from '../response/apiresponse';
import { Router } from '@angular/router';
import { response } from 'express';

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {

  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  private baseURL="http://localhost:8080/auth";  
  signUpUser(user:User):Observable<User>{
    console.log(user+"user-service");
    return this.http.post<ApiResponse<User>>(this.baseURL+"/signup",user).pipe(
      map(response=>{
        if(response.success){
          return response.data;
        }else{
          throw new Error(response.message);
        }
      })
    );
  }

  signInUser(user:User):Observable<User>{
    return this.http.post<any>(this.baseURL+"signin",user).pipe(
      map(response=>{
        if(response.success){
          return response.data;
        }else{
          throw new Error(response.message);
        }
      })
    )
  }

  updateUser(user: User): Observable<User>{
    const token = localStorage.getItem('authToken');
    console.log(token+"token in user service");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<ApiResponse<User>>(`${this.baseURL}/updateactive`, user, {headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  update(user: User): Observable<User>{
    const token = localStorage.getItem('authToken');
    console.log(token+"token in user service");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<ApiResponse<User>>(`${this.baseURL}/update`, user,{headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  deleteUser(id: number): Observable<User>{
    const token = localStorage.getItem('authToken');
    console.log(token+"token in user service");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.delete<ApiResponse<User>>(`${this.baseURL}/delete?id=${id}`,{headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getUserById(id: number): Observable<User>{
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<ApiResponse<User>>(`${this.baseURL}/findbyid?id=${id}`,{headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.log('Unauthorized:',error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profile: ', error);
          return throwError(()=>new Error('Something went wrong'));
        }
    ));
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<ApiResponse<User>>(this.baseURL + '/getCurrentUser').pipe(
      map(response => {
        if(response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      }),
    catchError(
      error => {
        if(error instanceof HttpErrorResponse && error.status === 401){
          console.error('Unauthorized:',error);
          this.router.navigate(['/login']);
          localStorage.removeItem('authToken'); 
          localStorage.removeItem('userCurrent');
        }
        console.error('Error fetching profile: ', error);
        return throwError(()=>new Error('Something went wrong'));
      }
    ));
  }

  getAllUser(): Observable<User[]>{
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<ApiResponse<User[]>>(`${this.baseURL}/getAll`,{headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
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
    
  getListUserById(ids: number[]): Observable<User[]> {
    const token = localStorage.getItem('authToken');
    return this.http.get<ApiResponse<User[]>>(`${this.baseURL}/getlistuserbyid?token=${token}&ids=${ids}`).pipe(
        map(response => {
            if(response.success){
                return response.data;
            } else {
                throw new Error(response.message);
            }
        })
    );
  }
}
