import { catchError, map, Observable, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Notification } from '../model/notification';
import { ApiResponse } from '../response/apiresponse';

@Injectable({
  providedIn: 'root'
})
export class NotificationServiceService {
  private baseURL = 'http://localhost:8080/notification/create';
  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  private createAuthorizationHeader(): HttpHeaders{
    const token = localStorage.getItem('authToken');

    if(token){
      console.log('Token found in local store:', token);
      return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }else{
      console.log('Token not found in local store');
    }
    return new HttpHeaders();
  }

  private mapToNotification(notificationDTO: any): Notification{
    return{
      id: notificationDTO.id,
      message: notificationDTO.message,
      createAt: notificationDTO.createAt,
      url: notificationDTO.url,
      idUser: notificationDTO.idUser,
      read: notificationDTO.read,
    }
  }

  createNotification(notification: Notification): Observable<Notification>{
    console.log('Creating notification:', notification);

    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    const authHeaders = this.createAuthorizationHeader();
    if(authHeaders.has('Authorization')){
      headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }

    return this.http.post<any>(this.baseURL, notification, { headers }).pipe(
      map(this.mapToNotification),
      catchError((error) => {
        if(error instanceof HttpErrorResponse && error.status === 401){
          console.error('Unauthorized request:', error);
          this.router.navigate(['/login']);
          localStorage.removeItem('authToken');
          localStorage.removeItem('userCurrent');
        }
        console.error('Error creating notification:', error);
        return throwError(() =>  new Error('Something went wrong'));
      })
    );
  }

  updateNotification(notification: Notification): Observable<Notification>{
    const headers = this.createAuthorizationHeader();

    return this.http.post<ApiResponse<Notification>>('http://localhost:8080/notification/update', notification,{headers}).pipe(
      map(response =>{
        if(response.success){
          return this.mapToNotification(response.data)
        }else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if (error instanceof HttpErrorResponse && error.status === 401){
            console.error('Unauthorized:' , error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }
          console.error('Error fetching profiles: ', error);
          return throwError(()=> new Error('Something went wrong'));
        }
      )
    )
  }

  getNotificationById(id: number): Observable<Notification[]>{
    const headers = this.createAuthorizationHeader();

    return this.http.get<ApiResponse<Notification[]>>(`http://localhost:8080/notification/user/findByUser?userId=${id}`, {headers}).pipe(
      map(response => {
        if(response.success){
          return response.data.map(this.mapToNotification)
        }else{
          throw new Error(response.message);
        }
      }),
      catchError(
        error => {
          if(error instanceof HttpErrorResponse && error.status === 401){
            console.log('Unauthorized: ',error);
            this.router.navigate(['/login']);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userCurrent');
          }

          console.error('Error fetching profiles: ',error);
          return throwError(() => new Error('Something went wrong'));
        }
      )
    )
  }
}
