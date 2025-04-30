import { ApiResponse } from './../response/apiresponse';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Project } from '../model/project';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectServiceService {
  private baseUrl = 'http://localhost:8080/project/user';
  constructor(
    private http: HttpClient,
  ) {}

  getProjectByUser(id: number):Observable<Project[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Project[]>>('http://localhost:8088/project/getByUser', { headers })
    .pipe(map(response=>{
      if(response.success){
        return response.data;
    }else{
      throw new Error(response.message);
    }
    }));
  }

  convertToProject(project: any): Project{
    return{
      id: project.id,
      title: project.title,
      description: project.description,
      createAt: project.createAt,
      url: project.url,
      display: project.display,
      idProfile: project.idProfile,
    }
  }

  createProjectByUser(project: Project): Observable<Project> {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    })

    const authHeaders = this.createAuthorizationHeader();
    if(authHeaders.has('Authorization')){
      headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }
    return this.http.post<ApiResponse<Project>>(`${this.baseUrl}save`, project, { headers })
    .pipe(map(response => {
      if (response.success) {
        return response.data;
      } else {
        throw new Error(response.message);
      }
    }
    ));
  }

  updateProjectByUser(project: Project): Observable<Project> {
    const headers = this.createAuthorizationHeader();
    return this.http.post<ApiResponse<Project>>(`${this.baseUrl}update`, project, { headers })
    .pipe(map(response => {
      if (response.success) {
        return this.mapToProject(response.data);
      } else {
        throw new Error(response.message);
      }
    }
    ));
  }

  deleteProjectByUser(id?: number): Observable<Project> {
    const headers = this.createAuthorizationHeader();
    return this.http.delete<ApiResponse<Project>>(`${this.baseUrl}delete?id=${id}`, { headers })
    .pipe(map(response => {
      if (response.success) {
        return response.data;
      } else {
        throw new Error(response.message);
      }
    }
    ));
  }

  getProjectByIdProfile(id?: number): Observable<Project[]> {
    const headers = this.createAuthorizationHeader();
    return this.http.get<ApiResponse<Project[]>>(`${this.baseUrl}getProject?id=${id}`, { headers })
    .pipe(map(response => {
      if (response.success) {
        return response.data.map(this.mapToProject);
      } else {
        throw new Error(response.message);
      }
    }
    ));
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

  private mapToProject(projectDTO: any): Project{
    return {
      id: projectDTO.id,
      title: projectDTO.title,
      description: projectDTO.description,
      createAt: projectDTO.createAt,
      url: projectDTO.url,
      display: projectDTO.display,
      idProfile: projectDTO.idProfile
    }
  }
}
