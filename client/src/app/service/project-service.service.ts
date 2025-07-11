import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Project } from '../model/project';
import { Apiresponse } from '../apiresponse';

@Injectable({
  providedIn: 'root'
})
export class ProjectServiceService {
  private baseURL = 'http://localhost:8080/project/user/';

  constructor(private http:HttpClient) { }
  
  getProjectByUser(id:number):Observable<Project[]>{
    const headers = this.createAuthorizationHeader();
    return this.http.get<Apiresponse<Project[]>>('http://localhost:8088/project/getByUser', {headers})
    .pipe(map(response=>{
      if(response.success){
        return response.data;
      }
      else{
        throw new Error(response.message);
      }
    }));
  }
  
  conventToProject(project :any):Project{
      return {
        id:project.id,
        title:project.title,
        description:project.description,
        createAt:project.createAt,
        url:project.url,
        display:project.display,
        idProfile:project.idProfile
      }
  }
  createProjectByUser(project:Project):Observable<Project>{
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const authHeaders = this.createAuthorizationHeader();
    if (authHeaders.has('Authorization')) {
      headers = headers.set('Authorization', authHeaders.get('Authorization')!);
    }
    return this.http.post<Apiresponse<Project>>(`${this.baseURL}save`, project, { headers }).pipe(
      map(response => {
        if (response.success) {
          return response.data;
        } else {
          throw new Error(response.message);
        }
      })
    );
  }

  updateProjectByUser(project:Project):Observable<Project>{
    const headers = this.createAuthorizationHeader();
    return this.http.post<Apiresponse<Project>>(`${this.baseURL}update`,project, {headers}).pipe(
      map(response=>{
        if(response.success){
          return this.mapToProject(response.data);
        }
        else{
          throw new Error(response.message);
        }
      }));
  }

  deleteProjectByUser(id?:number):Observable<string>{
    const headers = this.createAuthorizationHeader();
    return this.http.delete<Apiresponse<string>>(`${this.baseURL}delete/${id}`, {headers}).pipe(
      map(response => {
        if (!response.success) {
          throw new Error(response.message);
        }else{
          return response.data;
        }
      })
    );
  }

  getProjectByIdProfile(id?:number):Observable<Project[]>{
    const headers = this.createAuthorizationHeader();
    console.log("Id run: ",id)
    return this.http.get<Apiresponse<Project[]>>(`${this.baseURL}getProject?id=${id}`, {headers}).pipe(
      map(response=>{
        if(response.success){
          console.log("Data: ", response)
          return response.data.map(this.mapToProject);
        }
        else{
          throw new Error(response.message);
        }
      }));
  }

    getProjectIsDisplayedByIdProfile(id?:number):Observable<Project[]>{
    const headers = this.createAuthorizationHeader();
    console.log("Id run: ",id)
    return this.http.get<Apiresponse<Project[]>>(`${this.baseURL}getProject?id=${id}`, {headers}).pipe(
      map(response=>{
        if(response.success){
          return response.data.map(this.mapToProject);
        }
        else{
          throw new Error(response.message);
        }
      }));
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if(token){
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