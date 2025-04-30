import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, throwError } from 'rxjs';
import { Company } from '../model/company';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { ApiResponse } from '../response/apiresponse';
import { response } from 'express';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class CompanyServiceService {
  private companySource = new BehaviorSubject<Company|null>(null);
  company$ = this.companySource.asObservable();
  
  constructor(
    private http: HttpClient,
    private router: Router 
  ) { }

  private baseUrl = "http://localhost:8080/manager/";

  changeCompany(company: Company){
    this.companySource.next(company);
  }

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

  private mapToCompany(companyDTO: any): Company{
    return {
      id: companyDTO.id,
      name: companyDTO.name,
      type: companyDTO.type,
      description: companyDTO.description,
      street: companyDTO.street,
      email: companyDTO.email,
      phone: companyDTO.phone,
      country: companyDTO.country,
      idManager: companyDTO.idManager,
      idHR: companyDTO.idHr,
      idJobs: companyDTO.idJobs,
      image: companyDTO.image
    }
  }

  createCompany(company: FormData): Observable<Company>{
    // Không cần set 'Content-Type' cho FormData, trình duyệt sẽ tự động thiết lập nó
    const authHeaders = this.createAuthorizationHeader();

    let headers = authHeaders;
    return this.http.post<ApiResponse<Company>>(`${this.baseUrl}admin/company/create`,company, {headers}).pipe(
      map(response => {
        if(response.success){
          return this.mapToCompany(response.data);
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
        }
      )
    )
  }

  updateCompany(company: Company): Observable<Company>{
    let headers = new HttpHeaders({'Content-Type': 'application/json'});
    const authHeaders = this.createAuthorizationHeader();

    if (authHeaders.has('Authorization')) {
      headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }

    return this.http.post<ApiResponse<Company>>(`$this.baseUrl}manager/company/update`, company, {headers}).pipe(
      map(response => {
        if(response.success){
          return this.mapToCompany(response.data);
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
        }
      )    
    )
  }

  deleteCompany(id: number): void {
    const authHeaders = this.createAuthorizationHeader();

    this.http.post<ApiResponse<Company>>(`{this.baseUrl}admin/company/delete?id=${id}`, { headers: authHeaders }).pipe(
      map(response => {
        if (response.success) {
          alert('Delete company completed successfully')
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
          console.error('Error deleting company:', error);
          return throwError(() => new Error('Something went wrong!'));
        }
      )
    )
  }

  getCompanyById(id?: number): Observable<Company>{
    const headers = this.createAuthorizationHeader();

    return this.http.get<ApiResponse<Company>>(`${this.baseUrl}user/company/getbyid?id=${id}`, {headers}).pipe(
      map(response => {
        if(response.success){
          return this.mapToCompany(response.data);
        }else{
          throw new Error(response.message);
        }
      },
      ) 
      )
    }

  getCompanyByIddHr(id:number): Observable<Company> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Company>>(`${this.baseUrl}hr/findByIdHr?id=${id}`, { headers}).pipe(
      map(response=>{
        if (response.success) {
          return this.mapToCompany(response.data);
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

  getCompanyByManager(idManager?: number): Observable<Company> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Company>>(`${this.baseUrl}company/getcompanybyidmanager?managerId=${idManager}`, { headers}).pipe(
      map(response=>{
        if (response.success) {
          return this.mapToCompany(response.data);
        } else {
          throw new Error(response.message);
        }
      }),
      // catchError(
      //   error => {
      //     if (error instanceof HttpErrorResponse && error.status === 401) {
      //       console.error('Unauthorized:', error);
      //       this.router.navigate(['/login']);
      //       localStorage.removeItem('authToken');
      //       localStorage.removeItem('userCurrent');
      //     }
      //     console.error('Error fetching profiles:', error);
      //     return throwError(() => new Error('Something went wrong!'));
      //   }
      // )
    );
  }

  setHrToCompany(user:User, idCompany: number): Observable<Company>{
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    if (authHeaders.has('Authorization')) {
        headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }
    return this.http.put<ApiResponse<Company>>(`${this.baseUrl}manager/sethrtocompany?idCompany=${idCompany}`, user, { headers }).pipe(
      map(response=>{
        if (response.success) {
          return this.mapToCompany(response.data);
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

  setManagerToCompany(user:User, idCompany: number): Observable<Company>{
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    if (authHeaders.has('Authorization')) {
        headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }
    return this.http.put<ApiResponse<Company>>(`${this.baseUrl}manager/setmaanagertocompany?idCompany=${idCompany}`, user, { headers }).pipe(
      map(response=>{
        if (response.success) {
          return this.mapToCompany(response.data);
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

  getAllCompanies(): Observable<Company[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Company[]>>(`${this.baseUrl}user/company/getcompany`, { headers}).pipe(
      map((response) => {
        if (response.success) {
          return response.data.map(this.mapToCompany);
        } else {
          throw new Error(response?.message || 'Unknown error');
        }
      }),
      // catchError(
      //   error => {
      //     if (error instanceof HttpErrorResponse && error.status === 401) {
      //       console.error('Unauthorized:', error);
      //       this.router.navigate(['/login']);
      //       localStorage.removeItem('authToken');
      //       localStorage.removeItem('userCurrent');
      //     }
      //     console.error('Error fetching profiles:', error);
      //     return throwError(() => new Error('Something went wrong!'));
      //   }
      // )
    );
  }

  getCompanyByType(type:string): Observable<Company[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Company[]>>(`${this.baseUrl}user/company/getcompanybytype?type=${type}`, { headers }).pipe(
      map(response=>{
        if (response.success) {
          return response.data.map(this.mapToCompany);
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
}
