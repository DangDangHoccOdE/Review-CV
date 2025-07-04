import { UserServiceService } from './../../service/user-service.service';
import { CommonModule, NumberSymbol } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Job } from '../../model/job';
import { JobServiceService } from '../../service/job-service.service';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { User } from '../../model/user';
import { filter } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './job-list.component.html',
  styleUrl: './job-list.component.css'
})
export class JobListComponent implements OnInit {
  idProfileNumber:number | undefined;
  currentUrl?:string;
  userCurrent:User = new User();
  job: Job = new Job();
  jobs: Job[] = [];
  newJobs: Job[] = [];
  sentJobs: Job[] = [];
  acceptedJobs: Job[] = [];
  displayJob: Job[] = [];
  profilesPerPage = 6;
  currentPage = 0;
  @Input() user: User= new User();
  @Input() idCompany!: number | undefined ;
  @Input() isListJob: boolean = false;
  constructor (private jobService:JobServiceService, public router: Router,private toastr: ToastrService){
    const idProfileUser = localStorage.getItem('idProfileUser');
    this.idProfileNumber = idProfileUser ? Number(idProfileUser) : undefined;
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.currentUrl = this.router.url;
    });
  }

  ngOnInit(): void {
    const userCurrentString = localStorage.getItem('userCurrent');
    if (userCurrentString) {
      this.userCurrent = JSON.parse(userCurrentString);
    }
    this.currentUrl = this.router.url;
    if(this.idCompany){
      this.getJobsByIdCompany(this.idCompany);
    }
    if(this.idProfileNumber !== undefined){
      this.getNewJob(this.idProfileNumber);
      this.getJobPending(this.idProfileNumber);
      this.getJobAccepted(this.idProfileNumber);
    }
  }

  getJobs(): void {
    this.jobService.getAllJobs().subscribe(data => {
      this.newJobs = data;
      this.updateDisplayedProfiles();
    });
  }

  getJobsByIdCompany(id:number): void{
    this.jobService.getJobByCompany(id).subscribe(data=>{
      this.jobs=data;
    })
  }

  getNewJob(idProfile:number):void{
    this.jobService.getNewJob(idProfile).subscribe(data=>{
      this.newJobs=data;
      console.log(this.newJobs.length+" new jis")
      this.updateDisplayedProfiles();
    })
  }

  getJobPending(idProfile:number):void{
    this.jobService.getJobPending(idProfile).subscribe(data=>{
      this.sentJobs=data;
    })
  }

  getJobAccepted(idProfile:number):void{
    this.jobService.getJobAccepted(idProfile).subscribe(data=>{
      this.acceptedJobs=data;
    })
  }

  viewJobDetails(job:Job):void{
    if (job.id) {
      this.jobService.setJob(job);
      this.router.navigate(['job-details/', job.id]);
    }
  }

  applyJobs(idJob?:number){
    if(idJob && this.idProfileNumber){
      this.jobService.applyJobs(idJob, this.idProfileNumber).subscribe(data => {
        if(data){
           this.toastr.success('You have successfully applied for the job!', 'Success');
          if(this.idCompany){
            this.getJobsByIdCompany(this.idCompany);
          }
          if(this.idProfileNumber !== undefined){
            this.getNewJob(this.idProfileNumber);
            this.getJobPending(this.idProfileNumber);
            this.getJobAccepted(this.idProfileNumber);
          }
        }
    })
    }
    
  }
  
  updateDisplayedProfiles() {
    const startIndex = this.currentPage * this.profilesPerPage;
    this.displayJob = this.newJobs.slice(startIndex, startIndex + this.profilesPerPage);
    console.log(this.displayJob+" new jis this.displayJob")
  }

  nextPage() {
    if (this.currentPage < this.totalPages() - 1) {
      this.currentPage++;
      this.updateDisplayedProfiles();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.updateDisplayedProfiles();
    }
  }

  totalPages() {
    return Math.ceil(this.jobs.length / this.profilesPerPage);
  }

}
