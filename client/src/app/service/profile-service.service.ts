import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Profile } from '../model/profile';
import { ApiResponse } from '../response/apiresponse';
import { Router } from '@angular/router';
import { response } from 'express';

@Injectable({
  providedIn: 'root'
})
export class ProfileServiceService {
  private baseURl = 'http://localhost:8080/profile/';
  constructor(
    private http: HttpClient,
    private router: Router
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

  getProfileList(): Observable<Profile[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Profile[]>>(`${this.baseURl}user/getAll`, { headers })
      .pipe(map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      }
      ),
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
      ));
  }

  getListProfileByIdPendingJob(id: number[]): Observable<Profile[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Profile[]>>(`${this.baseURl}manager/getProfileByIdPending?id=${id}`, { headers })
      .pipe(map(response => {
        if (response.success) {
          return response.data.map(this.mapToProfile);
        } else {
          throw new Error(response.message);
        }
      }
      ),
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
      ));
  }

  private mapToProfile(profileDTO: any): Profile{
    return{
      id: profileDTO.id,
      objective: profileDTO.objective,
      education: profileDTO.education,
      workExperience: profileDTO.workExperience,
      idUser: profileDTO.idUser,
      title: profileDTO.title,
      contact: profileDTO.contact,
      skills: profileDTO.skills,
      typeProfile: profileDTO.typeProfile,
      url: profileDTO.url,
    }
  }

  createProfile(profile: FormData): Observable<Profile> {
    console.log('Creating profile: ', profile);
    let headers = new HttpHeaders();
    console.log('profile: ', profile);
    const authHeaders = this.createAuthorizationHeader();
    if(authHeaders.has('Authorization')){
      headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }
    return this.http.post<any>(`${this.baseURl}user/save`,profile,{headers})
    .pipe(map(this.mapToProfile),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401){
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');   
            localStorage.removeItem('userCurrent');
          }
          console.error('Error creating profile:', error);
          return throwError(() => new Error('Something went wrong!'));    
        }
      )
    );
  }

  updateProfile(profile: FormData): Observable<Profile>{
    const headers = this.createAuthorizationHeader();

    return this.http.post<ApiResponse<Profile>>(`${this.baseURl}user/update`, profile, { headers })
    .pipe(map(response => {
      if(response.success){
        return this.mapToProfile(response.data);
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
        console.error('Error updating profile:', error);
        return throwError(() => new Error('Something went wrong!'));
      }
    ));
  }

  getProfileByType(type: string): Observable<Profile[]> {
    const headers = this.createAuthorizationHeader();

    return this.http.get<ApiResponse<Profile[]>>(`${this.baseURl}user/findProfileByType?typeProfile=${type}`,{ headers })
    .pipe(map(response => {
      if(response.success){
        return response.data.map(this.mapToProfile);
      }else{
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
      })
    );
  }

  getProfileById(id: number): Observable<Profile> {
    const headers = this.createAuthorizationHeader();

    return this.http.get<ApiResponse<any>>(`${this.baseURl}user/findById?id=${id}`, { headers })
      .pipe(map(response => {
        if (response.success) {
          return this.mapToProfile(response.data);
        } else {
          throw new Error(response.message);
        }
      }
      ),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profile:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      ));
  }

  getProfileByUserId(userId: number): Observable<Profile>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Profile>>(`${this.baseURl}user/findByUserId?userId=${userId}`, { headers })
      .pipe(map(response => {
        if (response.success) {
          return this.mapToProfile(response.data);
        } else {
          throw new Error(response.message);
        }
      }
      ),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            console.error('Unauthorized:', error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profile:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      ));
  }
}
