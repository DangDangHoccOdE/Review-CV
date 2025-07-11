import { Apiresponse } from './../apiresponse';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Company } from '../model/company';
import { BehaviorSubject, catchError, map, Observable, observeOn, throwError } from 'rxjs';
import { User } from '../model/user';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class CompanyServiceService {

  private companySource = new BehaviorSubject<Company|null>(null);
  company$ = this.companySource.asObservable();

  constructor(private httpClient: HttpClient,private router:Router,private toastr: ToastrService) { }

  private baseURL="http://localhost:8080/manager/";

  changeCompany(company: Company){
    this.companySource.next(company);
  }

  createCompany(company: FormData): Observable<Company> {
      // No need to set 'Content-Type' header manually; the browser will handle it.
      const authHeaders = this.createAuthorizationHeader();

      // Merge authorization headers if present
      let headers = authHeaders;
    return this.httpClient.post<Apiresponse<Company>>(`${this.baseURL}admin/company/create`, company, { headers }).pipe(
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

  updateCompany(formData: FormData): Observable<Company>{
    const authHeaders = this.createAuthorizationHeader();

    return this.httpClient.post<Apiresponse<Company>>(`${this.baseURL}manager/company/update`, formData, { headers: authHeaders }).pipe(
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

  deleteCompany(id:number): void{
    const authHeaders = this.createAuthorizationHeader();
    this.httpClient.post<Apiresponse<String>>(`${this.baseURL}admin/company/delete?id=${id}`, { authHeaders }).pipe(
      map(response=>{
        if (!response.success) {
          throw new Error(response.message);
        }
        this.toastr.success('Delete company completed successfully', 'Success');
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

  getCompanyById(id?: number): Observable<Company> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Company>>(`${this.baseURL}user/company/getbyid?id=${id}`, { headers }).pipe(
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

  getCompanyByIddHr(id:number): Observable<Company> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Company>>(`${this.baseURL}hr/findByIdHr?id=${id}`, { headers}).pipe(
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
    return this.httpClient.get<Apiresponse<Company>>(`${this.baseURL}company/getcompanybyidmanager?managerId=${idManager}`, { headers}).pipe(
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
    return this.httpClient.put<Apiresponse<Company>>(`${this.baseURL}manager/sethrtocompany?idCompany=${idCompany}`, user, { headers }).pipe(
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
    return this.httpClient.put<Apiresponse<Company>>(`${this.baseURL}manager/setmaanagertocompany?idCompany=${idCompany}`, user, { headers }).pipe(
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
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          return throwError(() => error.error); 
        }
      )
    );
  }

  getAllCompanies(): Observable<Company[]> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Company[]>>(`${this.baseURL}user/company/getcompany`, { headers}).pipe(
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
    return this.httpClient.get<Apiresponse<Company[]>>(`${this.baseURL}user/company/getcompanybytype?type=${type}`, { headers }).pipe(
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


  private mapToCompany(companyDTO: any): Company {
    return {
      id: companyDTO.id,
      name: companyDTO.name,
    type: companyDTO.type,
    description: companyDTO.description,
    street: companyDTO.street,
    email: companyDTO.email,
    phone: companyDTO.phone,
    city: companyDTO.city,
    country: companyDTO.country,
    idManager: companyDTO.idManager,
    idHR: companyDTO.idHR,
    idJobs: companyDTO.idJobs,
    url: companyDTO.url
    };
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

}
