import { Component, Input, OnInit } from '@angular/core';
import { ContactComponent } from '../contact/contact.component';
import { CreateProfileComponent } from '../create-profile/create-profile.component';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { Profile } from '../../model/profile';
import { ProfileServiceService } from '../../service/profile-service.service';
import { ProjectServiceService } from '../../service/project-service.service';
import { Project } from '../../model/project';
import { CommonModule, DatePipe } from '@angular/common';
import { ProjectListComponent } from '../project-list/project-list.component';
import { ProfileListComponent } from '../profile-list/profile-list.component';
import { User } from '../../model/user';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ContactComponent, CreateProfileComponent, CommonModule, ProjectListComponent, RouterLink, RouterOutlet,ProfileListComponent],
 
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{
  @Input() param?: number;
  constructor(private route: ActivatedRoute , private profileService: ProfileServiceService, private projectService: ProjectServiceService, private datePipe: DatePipe) { }
  id?:number;
  profile: Profile = new Profile;
  projects: Project[] = [];
  userCurrent: User = new User();
  
  ngOnInit(): void {
    const idProfile = localStorage.getItem('idProfileUser');
    if(idProfile){
      this.getProfileById(Number(idProfile));
    }
    // this.route.params.subscribe(params => {
    //   console.log("Route params:", params);
    //   if (params['id']) {
    //     this.param = +params['id'];
    //     this.getProfileById(this.param);
    //   } else {
    //     this.id = 810437498; // default value or logic
    //     this.getProfileById(this.id);
    //   }
    // });
  }

  /* getByUser(id: number) {
    this.profileService.getProfileByUser(id).subscribe(data => {
      this.profile = data;
    })
  } */
  getProfileById(id:number){
    this.profileService.getProfileById(id).subscribe(data=>{
      this.profile=data;
      this.getProjectByIdProfile(id);
    })
  }

  getProjectByIdProfile(id?:number){
    this.projectService.getProjectByIdProfile(id).subscribe(data=>{
      this.projects = data.filter(project => project.display);
    })
  }

  getFormattedDate(createAt?:string) {
    if(createAt!=null){
    return this.datePipe.transform(createAt, 'MMM d, y');
    }else{
      return null;
    }
  }

  scrollToSection(sectionId: string): void {
    const section = document.getElementById(sectionId);
    if (section) {
        const navbarHeight = 50;
        const sectionPosition = section.getBoundingClientRect().top + window.pageYOffset;
        
        window.scrollTo({
            top: sectionPosition - navbarHeight,
            behavior: 'smooth'
        });
    }
}

}
