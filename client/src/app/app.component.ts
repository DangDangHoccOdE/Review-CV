import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { UserComponent } from './components/user/user.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ManagerLayoutComponent } from './manager/manager-layout/manager-layout.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink,CommonModule, AdminLayoutComponent, NavbarComponent,UserComponent, LoginComponent, RegisterComponent, ManagerLayoutComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'angular';
  showMainNavbar = true;
  showAdminNavbar = false;
  showUser = false;
  showLogin = false;
  showManagerNavbar = false;

  constructor(
    private router: Router
  ){}

  ngOnInit():void{
    this.router.events.subscribe((event) => {
      if(event instanceof NavigationEnd){
        if(this.router.url.startsWith('/admin')){
          this.showMainNavbar = false;
          this.showAdminNavbar = true;
          this.showUser = false;
          this.showLogin = false;
          this.showManagerNavbar = false;
        } } else if (this.router.url.startsWith('/register') || this.router.url.startsWith('/user')) {
          this.showMainNavbar = false;
          this.showAdminNavbar = false;
          this.showUser = true;
          this.showLogin = false;
          this.showManagerNavbar = false;
        } else if(this.router.url.startsWith('/login')) {
          this.showMainNavbar = false;
          this.showAdminNavbar = false;
          this.showUser = false;
          this.showLogin = true;
          this.showManagerNavbar = false;
        } else if(this.router.url.startsWith('/manager')){
          this.showMainNavbar = false;
          this.showAdminNavbar = false;
          this.showUser = false;
          this.showLogin = false;
          this.showManagerNavbar = true;
        }
        else {
          this.showMainNavbar = true;
          this.showAdminNavbar = false;
          this.showUser = false;
          this.showLogin = false;
          this.showManagerNavbar = false;
        }
        
      }
    )
  }
}

