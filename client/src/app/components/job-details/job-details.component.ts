import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Company } from '../../model/company';
import { Job } from '../../model/job';
import { JobServiceService } from '../../service/job-service.service';
import { CompanyServiceService } from '../../service/company-service.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-job-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './job-details.component.html',
  styleUrl: './job-details.component.css'
})
export class JobDetailsComponent implements OnInit{
  company: Company | undefined;
  job: Job | undefined;

  constructor(
    private JobService: JobServiceService,
    private companyService: CompanyServiceService,
    private route: ActivatedRoute
  ){}

  ngOnInit(): void {
      this.route.paramMap.subscribe(params => {
        const id = Number(params.get('id'));
        if (id){
          this.loadJobDetails(id);
        }
    })
  }

  loadJobDetails(id: number): void{
    this.JobService.getJobById(id).subscribe(job => {
      this.job = job;
      this.getCompanyByJob(job.idCompany);
    })
  }

  getCompanyByJob(id?: number){
    this.companyService.getCompanyById(id).subscribe(company => {
      this.company = company
    })
  }
}
