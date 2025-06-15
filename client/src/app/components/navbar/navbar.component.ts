import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { NotificationComponent } from '../notification/notification.component';
import { CommonModule } from '@angular/common';
import { Profile } from '../../model/profile';
import { ProfileServiceService } from '../../service/profile-service.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule,RouterOutlet,NotificationComponent,CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  idProfileNumber:number | undefined;
  userCurrent: any;
  profile?: Profile;
  constructor (private router: Router , private profileService: ProfileServiceService) {}
  
  ngOnInit(): void {
    const userCurrentString = localStorage.getItem('userCurrent');
    if (userCurrentString) {
      this.userCurrent = JSON.parse(userCurrentString);
      console.log('User Current:', this.userCurrent);
      this.getUserProfile();
      console.log(this.userCurrent.id);      
    }
  }

  showNotifications = false;
  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  logout(): void {
    localStorage.removeItem('userCurrent');
    localStorage.removeItem('authToken');
    localStorage.removeItem('idCompany');
    localStorage.removeItem('idProfile');
    localStorage.removeItem('idProfileUser');
    this.router.navigate(['/login']);
  }

  getProfileImage(): string {
    if (this.profile?.url) {
      return this.profile.url;
    }
    return 'https://res.cloudinary.com/dhv7v0enl/image/upload/v1746451749/t%E1%BA%A3i_xu%E1%BB%91ng_knhjpn.jpg';
  }

  getUserProfile(): void {
    if (this.userCurrent) {
      this.profileService.getProfileByUserId(this.userCurrent.id).subscribe((profile: Profile) => {
        this.profile = profile;
        this.idProfileNumber = profile.id;
        if(this.idProfileNumber){
          localStorage.setItem('idProfileUser',this.idProfileNumber+'');
        }
        console.log('Profile:', this.profile);
      });
    }
  }

  
  
}
