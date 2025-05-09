import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Job } from '../model/job';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ApiResponse } from '../response/apiresponse';

@Injectable({
  providedIn: 'root'
})
export class JobServiceService {
  private baseUrl = 'http://localhost:8080/manager/';
  private job: Job | undefined;

  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  private mapToJob(jobDTO: any): Job{
    return{
      id: jobDTO.id,
      title: jobDTO.title,
      description: jobDTO.description,
      typeJob: jobDTO.typeJob,
      size: jobDTO.size,
      idProfile: jobDTO.idProfile,
      idCompany:jobDTO.idCompany,
      idProfilePending: jobDTO.idProfilePending
    }
  }

  setJob(job: Job){
    this.job = job;
  }

  getJob(): Job | undefined{
    return this.job;
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if (token) {
      console.log('Token found in local store:', token);
      return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }
    else {
      console.log('Token not found in local store');
    }
    return new HttpHeaders();
  }

  createJob(job: Job): Observable<Job>{
    let headers = new HttpHeaders({
      'Content-Type':'application/json'
    })
    const authHeaders = this.createAuthorizationHeader();
    headers = headers.set('Authorization', authHeaders.get('Authorization') || '');
    
    return this.http.post<ApiResponse<Job>>(`${this.baseUrl}hr/job/create`,job, {headers}).pipe(
      map(response => {
        if(response.success){
          return this.mapToJob(response.data);
        }else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if(error instanceof HttpErrorResponse && error.status === 401){
            console.error('Unauthorized: ', error);
            this.router.navigate(["/login"]);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profiles:', error);
          return throwError(() => new Error('Something went wrong!'));
          }
      )
    )
  }

  updateJob(job: Job): Observable<Job>{
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    headers = headers.set('Authorization', authHeaders.get('Authorization') || '');
    
    return this.http.post<ApiResponse<Job>>(`${this.baseUrl}hr/job/update`, job, {headers}).pipe(
      map(response => {
        if(response.success){
          return this.mapToJob(response.data);
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

  deleteJob(id: number): void {
    const headers = this.createAuthorizationHeader();
    this.http.post<ApiResponse<any>>(
      `${this.baseUrl}hr/job/delete?id=${id}`, {}, { headers: headers }).subscribe(
      response => {
        if (!response.success) {
          throw new Error(response.message);
        } else {
          alert('Deleted Job successfully');
          window.location.reload();
        }
      },
      error => {
        if (error.status === 401) {
          alert('Unauthorized access. Please log in again.');
        } else {
          console.error('Error deleting job:', error);
          alert('Failed to delete job.');
        }
      },
      
    );
  }

  applyJobs(idJob: number, idProfile: number): Observable<Job>{
    const headers = this.createAuthorizationHeader();
    let params = new HttpParams();
    params = params.append('jobDTO', idJob.toString());
    params = params.append('idProfile', idProfile.toString());

    return this.http.put<ApiResponse<Job>>(`${this.baseUrl}/user/job/apply`, null,  {params, headers}).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      }),catchError(
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

  getJobById(id: number): Observable<Job>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job>>(`${this.baseUrl}user/job/fingbyid=$${id}`,{headers}).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getAllJobs(): Observable<Job[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}user/job/getall`,{headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getJobCompany(idCompany: number): Observable<Job[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}user/job/getjobbycompany?id=${idCompany}`, {headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    )
  }

  getJobPending(idProfile: number): Observable<Job[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}user/job/getjobpending?id=${idProfile}`, {headers}).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    )
  }

  getJobAccepted(idProfile: number): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}user/job/getjobaccepted?id=${idProfile}`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getNewJob(idProfile: number): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Job[]>>(`${this.baseUrl}user/job/getnewjob?id=${idProfile}`, { headers }).pipe(
      map(response => {
        if (response.success) {
          console.log(response.data.length + " svjob")
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  acceptProfileJob(idJob: number, idProfile: number) {
    const headers = this.createAuthorizationHeader();
    return this.http.put<ApiResponse<Job>>(`${this.baseUrl}hr/job/accept?jobDTO=${idJob}&idProfile=${idProfile}`, {}, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  rejectProfileJob(idJob: number, idProfile: number) {
    const headers = this.createAuthorizationHeader();
    return this.http.put<ApiResponse<Job>>(`${this.baseUrl}hr/job/reject?jobDTO=${idJob}&idProfile=${idProfile}`, {}, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }
}
