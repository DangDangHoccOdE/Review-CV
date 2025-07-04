import { title } from 'process';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Job } from '../model/job';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Apiresponse } from '../apiresponse';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class JobServiceService {
  private job: Job | undefined;

  constructor(private httpClient: HttpClient, private router: Router,private toastr: ToastrService) { }
  private baseURL = "http://localhost:8080/manager/";

  createJob(job: Job): Observable<Job> {
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    headers = headers.set('Authorization', authHeaders.get('Authorization') || '');

    return this.httpClient.post<Apiresponse<Job>>(`${this.baseURL}hr/job/create`, job, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
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

  updateJob(job: Job): Observable<Job> {
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    headers = headers.set('Authorization', authHeaders.get('Authorization') || '');

    return this.httpClient.post<Apiresponse<Job>>(`${this.baseURL}hr/job/update`, job, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
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
  deleteJob(id: number): void {
    const headers = this.createAuthorizationHeader();
    this.httpClient.post<Apiresponse<any>>(
      `${this.baseURL}hr/job/delete?id=${id}`, {}, { headers: headers }).subscribe(
      response => {
        if (!response.success) {
          throw new Error(response.message);
        } else {
          this.toastr.success('Deleted Job successfully', 'Success');
          window.location.reload();
        }
      },
      error => {
        if (error.status === 401) {
          this.toastr.error('Unauthorized access. Please log in again.', 'Unauthorized');
        } else {
          console.error('Error deleting job:', error);
          this.toastr.error('Failed to delete the job. Please try again.', 'Error');
        }
      },

    );
  }


  applyJobs(idJob: number, idProfile: number): Observable<Job> {
    const headers = this.createAuthorizationHeader();
    let params = new HttpParams();
    params = params.append('jobDTO', idJob.toString());
    params = params.append('idProfile', idProfile.toString());
    return this.httpClient.put<Apiresponse<Job>>(`${this.baseURL}/user/job/apply`, null, { params, headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
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


  getJobById(id: number): Observable<Job> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Job>>(`${this.baseURL}user/job/findbyid?id=${id}`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getAllJobs(): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Job[]>>(`${this.baseURL}user/job/getall`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getJobByCompany(idCompany: number): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Job[]>>(`${this.baseURL}user/job/getjobbycompany?id=${idCompany}`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getJobPending(idProfile: number): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Job[]>>(`${this.baseURL}user/job/getjobpending?id=${idProfile}`, { headers }).pipe(
      map(response => {
        if (response.success) {
          return response.data.map(this.mapToJob);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  getJobAccepted(idProfile: number): Observable<Job[]> {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.get<Apiresponse<Job[]>>(`${this.baseURL}user/job/getjobaccepted?id=${idProfile}`, { headers }).pipe(
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
    return this.httpClient.get<Apiresponse<Job[]>>(`${this.baseURL}user/job/getnewjob?id=${idProfile}`, { headers }).pipe(
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
    return this.httpClient.put<Apiresponse<Job>>(`${this.baseURL}hr/job/accept?jobDTO=${idJob}&idProfile=${idProfile}`, {}, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          console.log("Response: ", response)
          throw new Error(response.message);
        }
      })
    );
  }

  rejectProfileJob(idJob: number, idProfile: number) {
    const headers = this.createAuthorizationHeader();
    return this.httpClient.put<Apiresponse<Job>>(`${this.baseURL}hr/job/reject?jobDTO=${idJob}&idProfile=${idProfile}`, {}, { headers }).pipe(
      map(response => {
        if (response.success) {
          return this.mapToJob(response.data);
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  setJob(job: Job) {
    this.job = job;
  }

  getJob(): Job | undefined {
    return this.job;
  }


  private mapToJob(jobDTO: any): Job {
    return {
      id: jobDTO.id,
      title: jobDTO.title,
      description: jobDTO.description,
      typeJob: jobDTO.typeJob,
      size: jobDTO.size,
      idProfiePending: jobDTO.idProfiePending,
      idProfile: jobDTO.idProfile,
      idCompany: jobDTO.idCompany
    }
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
}
