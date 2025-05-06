import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Profile } from '../../model/profile';
import { Job } from '../../model/job';
import { User } from '../../model/user';
import { UserServiceService } from '../../service/user-service.service';
import { Router } from 'express';
import { JobServiceService } from '../../service/job-service.service';
import { ProfileServiceService } from '../../service/profile-service.service';

@Component({
  selector: 'app-employee-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee-layout.component.html',
  styleUrl: './employee-layout.component.css'
})
export class EmployeeLayoutComponent implements OnInit{
  profile: Profile = new Profile();
  idCompany?: number;
  jobs: Job[] = []
  jobUsersMap: Map<number, User[]> = new Map()

  constructor(
    private userService: UserServiceService,
    private router: Router,
    private jobService: JobServiceService,
    private profileService: ProfileServiceService
  ){}

  ngOnInit(): void {
    const userCurrentString = localStorage.getItem('idCompany')
    if(userCurrentString) {
      this.idCompany = Number(userCurrentString);
      this.getJobsByCompany(this.idCompany);
    }
  }

  getJobsByCompany(id: number){
    this.jobService.getJobCompany(id).subscribe(jobs=>{
      this.jobs = jobs;
      this.jobs.forEach(job => {
        if(job.idProfile && job.idProfile.length > 0){
          console.log("(job.idProfile && job.idProfile.length > 0 "+ job.idProfile);
          this.getProfileById(job.id!, job.idProfile)
        }
      })
    })
  }

  getProfileById(jobId: number, profileIds: number[]):void{
    this.profileService.getListProfileByIdPendingJob(profileIds).subscribe(profiles => {
      profiles.forEach(profile => {
        if(profile.idUser){
          console.log("(profile.idUser) " + profile.idUser);
          this.getUserById(jobId, profile.idUser)
        }
      })
    })
  }

  getUserById(jobId: number, userId: number): void{
    this.userService.getUserById(userId).subscribe(user => {
      let usersForJob = this.jobUsersMap.get(jobId);
      console.log("usersForJob "+ usersForJob);
      if(!usersForJob){
        usersForJob = [];
        this.jobUsersMap.set(jobId, usersForJob);
      }
      usersForJob.push(user);
    })
  }  

  deleteUser(id: number): void{
    this.userService.deleteUser(id).subscribe(() => {
      alert("User deleted successfully")
      if(this.idCompany){
        this.getJobsByCompany(this.idCompany);
      }
    })
  }

  openModal(action: string, user: User): void {
    // Handle modal open logic
  }
}
