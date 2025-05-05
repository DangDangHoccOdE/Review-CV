import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Company } from '../../model/company';
import { User } from '../../model/user';
import { CompanyServiceService } from '../../service/company-service.service';
import { privateDecrypt } from 'crypto';
import { UserServiceService } from '../../service/user-service.service';

@Component({
  selector: 'app-company-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './company-list.component.html',
  styleUrl: './company-list.component.css'
})
export class CompanyListComponent implements OnInit{
  isShowingCompany: boolean = false;
  isModalOpen: boolean = false;
  companyForm!: FormGroup;
  userForm: FormGroup;
  listCompany: Company[] = [];
  isEdit: boolean = false;
  idCompany?: number;
  modalTitle: string = '';
  modalButtonLabel: string = '';
  company?: Company;
  manager?: User | undefined;
  managers: { [key: number]: User} = {};

  constructor(
    private companyService: CompanyServiceService,
    private formBuilder: FormBuilder,
    private userService: UserServiceService
  ){
    this.userForm = this.formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    })
  }

  ngOnInit(): void {
    console.log("Company start");
    this.initForm();
    this.getAllCompany();
  }

  initForm(): void {
    this.companyForm = this.formBuilder.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      description: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\+?\d{10,}$/)]],
      address: ['',Validators.required]
    });
  }

  getAllCompany(): void {
    this.companyService.getAllCompanies().subscribe(data => {
      console.log(data);
      this.listCompany = data;
      this.listCompany.forEach(company => {
        if (company.idManager) {
          this.getManagerById(company.idManager);
        }
      });
    });
  }

  getManagerById(id: number): void {
    this.userService.getUserById(id).subscribe(data => {
      this.managers[id] = data;
      console.log(this.managers[id] + "  Managers");
      console.log(this.managers[id].email + " Managers");
    });
  }

  editCompany(company?: Company): void {
    if (company) {
      this.companyForm.patchValue(company);
      this.idCompany = company.id;
      this.company = company;
      if(company.idManager)
      {
        this.getManagerById(company.idManager)
      }
    }
    this.isShowingCompany = true;
  }
}
