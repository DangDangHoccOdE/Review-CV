import { Routes } from "@angular/router";
import { LoginComponent } from "../components/login/login.component";
import { ManagerLayoutComponent } from "../manager/manager-layout/manager-layout.component";
import { JobLayoutComponent } from "../manager/job-layout/job-layout.component";
import { ApplyLayoutComponent } from "../manager/apply-layout/apply-layout.component";
import { HrLayoutComponent } from "../manager/hr-layout/hr-layout.component";
import { CompanyComponent } from "../manager/company/company.component";
import { ProfileUserComponent } from "../components/profile-user/profile-user.component";
import { EmloyeeLayoutComponent } from "../manager/emloyee-layout/emloyee-layout.component";
import { AuthGuard } from "../guards/auth.guard";


export const managerRoutes: Routes = [
    {
        path: '',
        component: ManagerLayoutComponent,
        canActivate: [AuthGuard],
        data: {role: ['admin', 'hr','manager']},
        children:[
            {
                path: 'job',
                component: JobLayoutComponent
            },
            {
                path: 'emloyee',
                component: EmloyeeLayoutComponent
            },
            {
                path: 'hr',
                component: HrLayoutComponent
            }, 
            {
                path: 'about',
                component: CompanyComponent
            },
            {
                path: 'profile/profile-user/:id',
                component: ProfileUserComponent
            }
        ]
    }


];