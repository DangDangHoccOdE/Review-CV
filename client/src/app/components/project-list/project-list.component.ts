import { CommonModule, DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Project } from '../../model/project';
import { Profile } from '../../model/profile';
import { ProjectServiceService } from '../../service/project-service.service';
import { ProfileServiceService } from '../../service/profile-service.service';
import { PostProjectComponent } from '../post-project/post-project.component';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, RouterOutlet, PostProjectComponent],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent {
  projects: Project[] = [];
  project: Project = new Project();
  selectedProject: Project | null = null; // Khởi tạo biến selectedProject là null
  isFormVisible: boolean = false; // Biến điều khiển hiển thị form
  userCurrent: any;
  profile?: Profile;
  @Input() idProfile?:number | null;
  
  constructor(
    private projectService: ProjectServiceService,
    private datePipe: DatePipe,
    private profileService: ProfileServiceService
  ){}

  ngOnInit() : void{
    const userCurrentString = localStorage.getItem('userCurrent');
    if (userCurrentString){
      this.userCurrent = JSON.parse(userCurrentString);
      console.log(this.userCurrent + "user current in project list component");
    }

    console.log("Project start")
    const idProfile = localStorage.getItem('idProfile');
    console.log(idProfile + "id profile in project list component")
    if(idProfile){
      this.getProjectByIdProfile(Number(idProfile));
    }else{
      this.getProfileByIdUser(this.userCurrent.id);
    }
  }
  
  getFormatDate(createAt?: string) {
    return this.datePipe.transform(createAt, 'MMM d, y');
  }

  getProjectByUser(id: number){
    this.projectService.getProjectByUser(id).subscribe(
      data => {
        this.projects = data;
        this.isCurrentUserProfile();
      }
    )
  }

  getProfileByIdUser(id: number){
    this.profileService.getProfileByUserId(id).subscribe(
      data => {
        this.profile = data;
        if(this.profile.id){
          this.getProjectByIdProfile(this.profile.id);
        }
      })
  }

  getProjectByIdProfile(id:number) {
    this.projectService.getProjectByIdProfile(id).subscribe(
      data => {
        this.projects = data
      }
    )
  }

  getProfileById(idProfile: number){
    this.profileService.getProfileById(idProfile).subscribe(
      data => {
        this.profile = data;
        if(this.profile.idUser){
          this.getProjectByUser(this.profile.idUser);
        }
      }
    )
  }

  deleteProject(project: Project){
    this.projectService.deleteProjectByUser(project.id).subscribe(
      data => {
        if(data){
          console.log(data)
          alert('Delete project successfully!')
          this.getProjectByIdProfile(this.profile?.id!);
        }
      }
    )
  }

  showEditForm(project: Project){
    console.log(project?.title)
    this.selectedProject = project; // Gán project được chọn vào biến selectedProject
    this.isFormVisible = true; // Hiển thị form
  }

  hideEditForm(): void{
    this.selectedProject = null;
    this.isFormVisible = false; // Ẩn form
  }

  isCurrentUserProfile(): boolean{
    return this.profile?.idUser === this.userCurrent?.id;
  }
}
