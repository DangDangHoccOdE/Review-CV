import { ProfileUserComponent } from './../components/profile-user/profile-user.component';
import { Routes } from "@angular/router";
import { ManagerLayoutComponent } from "../manager/manager-layout/manager-layout.component";
import { JobLayoutComponent } from "../manager/job-layout/job-layout.component";
import { EmployeeLayoutComponent } from "../manager/employee-layout/employee-layout.component";
import { HrLayoutComponent } from "../manager/hr-layout/hr-layout.component";
import { CompanyComponent } from "../manager/company/company.component";

export const managerRoutes: Routes = [
    {
        path: '',
        component: ManagerLayoutComponent,
        children:[
            {
                path: 'job',
                component: JobLayoutComponent
            },
            {
                path: 'employee',
                component: EmployeeLayoutComponent
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
]