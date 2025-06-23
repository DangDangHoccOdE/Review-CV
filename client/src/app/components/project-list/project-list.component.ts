import { CommonModule, DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { ProjectServiceService } from '../../service/project-service.service';
import { Project } from '../../model/project';
import { RouterOutlet } from '@angular/router';
import { PostProjectComponent } from "../post-project/post-project.component";
import { Profile } from '../../model/profile';
import { ProfileServiceService } from '../../service/profile-service.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, RouterOutlet, PostProjectComponent],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css',
  providers: [DatePipe] 
})
export class ProjectListComponent implements OnInit {
  projects:Project[]=[]
  project:Project=new Project;
  selectedProject: Project | null = null;
  isFormVisible: boolean = false;
  userCurrent: any;
  profile?: Profile;
  @Input() idProfile?:number | null;

  constructor(private projectService:ProjectServiceService,    public router: Router, private datePipe: DatePipe,private profileService:ProfileServiceService, private toastr: ToastrService){}

  ngOnInit(): void {
    const userCurrentString = localStorage.getItem('userCurrent');
    if (userCurrentString) {
      this.userCurrent = JSON.parse(userCurrentString);
    }
    if(this.idProfile){
      this.getProjectIsDisplayByIdProfile(Number(this.idProfile));
    }else{
      this.getProfileByIdUser(this.userCurrent.id);
    }
  }

  getFormattedDate(createAt?:string) {
    return this.datePipe.transform(createAt, 'MMM d, y');
  }

  getProjectByUser(id:number){
    this.projectService.getProjectByUser(id).subscribe(data=>{
      this.projects=data
      this.isCurrentUserProfile();
    })
  }

  getProfileByIdUser(id:number){
    this.profileService.getProfileByUserId(id).subscribe(data=>{
      this.profile=data;
      this.idProfile = data.id
      if(this.profile.id)
      {
        this.getProjectByIdProfile(this.profile.id);
      }
    })
  }

  getProjectByIdProfile(id:number){
    this.projectService.getProjectByIdProfile(id).subscribe(data=>{
      this.projects=data;
    })
  }

  getProjectIsDisplayByIdProfile(id:number){
    this.projectService.getProjectByIdProfile(id).subscribe(data=>{
      this.projects = data.filter(project => project.display);
    })
  }

  getProfileById(idProfile: number) {
    this.profileService.getProfileById(idProfile).subscribe(data => {
      this.profile = data;
      if(this.profile.idUser)
      {
        this.getProjectByUser(this.profile.idUser);
      }
    });
  }

  deleteProject(project:Project){
    Swal.fire({
      title: "Are you sure you want to delete the project?",
      text: `Project: ${project.title}`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6'
    }).then((result) => {
      if(result.isConfirmed){
        this.projectService.deleteProjectByUser(project.id).subscribe(data=>{
        if(data){
          Swal.fire('Deleted','Delete project successfully','success')
          this.getProjectByIdProfile(this.idProfile!);

          setTimeout(() => {
            const userCurrentString = localStorage.getItem('userCurrent');
            if (userCurrentString) {
              this.userCurrent = JSON.parse(userCurrentString);
            }

            if (this.idProfile) {
              this.getProjectByIdProfile(Number(this.idProfile));
            } else {
              this.getProfileByIdUser(this.userCurrent.id);
            }
          }, 1000);
        }
        })
      }
    })
  }

  showEditForm(project:Project | null): void {
    this.selectedProject = project;
    this.isFormVisible = true;
  }

  hideEditForm(): void {
    this.selectedProject = null; 
    this.isFormVisible = false;
  }

  isCurrentUserProfile(): boolean {
    return this.profile?.idUser === this.userCurrent?.id;
  }

}
