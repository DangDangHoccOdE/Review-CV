import { Component, OnInit } from '@angular/core';
import { CompanyServiceService } from '../../service/company-service.service';
import { Company } from '../../model/company';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../model/user';
import { UserServiceService } from '../../service/user-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-company-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './company-list.component.html',
  styleUrls: ['./company-list.component.css']
})
export class CompanyListComponent implements OnInit {
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
  managers: { [key: number]: User } = {};
  previewUrl: string | ArrayBuffer | null = null;
  selectedImageFile?: File;

  constructor(
    private companyService: CompanyServiceService,
    private formBuilder: FormBuilder,
    private userService:UserServiceService,
    private toastr: ToastrService
  ) {
    this.userForm = this.formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
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
      address: ['',Validators.required],
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

  onImageSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    const file = fileInput.files?.[0];

    if (file) {
      this.selectedImageFile = file; // lưu vào model
      
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.selectedImageFile);
    }
  }

  getAllManager(){

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

  closeModal(): void {
    this.isShowingCompany = false;
    this.isModalOpen = false;
  }

  openModal(action: 'add' | 'edit', manager?: User, companyId?: number): void {
    this.isEdit = action === 'edit';
    this.modalTitle = this.isEdit ? 'Edit Manager' : 'Add Manager';
    this.modalButtonLabel = this.isEdit ? 'Update Manager' : 'Add Manager';
    if (manager) {
      this.userForm.patchValue(manager);
    } else {
      this.userForm.reset();
    }
    if (companyId) {
      this.idCompany = companyId; // Ensure companyId is passed and used
    }
    this.isModalOpen = true;
  }

  saveCompany(): void {    
    const formData = new FormData();
    formData.append('name', this.companyForm.get('name')?.value);
    formData.append('type', this.companyForm.get('type')?.value);
    formData.append('description', this.companyForm.get('description')?.value);
    formData.append('email', this.companyForm.get('email')?.value);
    formData.append('phone', this.companyForm.get('phone')?.value);
    formData.append('street',this.companyForm.get('address')?.value);
    // Nếu có ảnh được chọn
    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile);
    }
  
    this.companyService.createCompany(formData).subscribe(() => {
      this.isShowingCompany = false;
      this.initForm();
      this.getAllCompany();
      this.toastr.success('Company details updated successfully!', 'Success');
    }, error => {
      console.error('Error saving company:', error);
      this.toastr.error('Failed to update company details.', 'Error');
    });
  }
  
  onSubmit(): void {
    if (this.userForm.valid) {
        this.saveUser();
    } else {
      this.toastr.warning('Please fill out all required fields correctly.', 'Warning');
    }
  }

  saveUser(): void {
    const newUser: User = this.userForm.value;
    newUser.role = 'manager'; // Ensure the role is set to manager
    if (!newUser.name || !newUser.email || !newUser.password) {
      this.toastr.warning('Please fill out all required fields!', 'Warning');
      return;
    }
    if (this.idCompany) {
      this.companyService.setManagerToCompany(newUser, this.idCompany).subscribe({
        next: data => {
          this.getAllCompany();
          this.closeModal();
          this.toastr.success('User added successfully!', 'Success');
        },
        error: error => {
          const errorMessage = error?.message;
          if (error.status === 409 || errorMessage === 'Email already exists') {
            this.userForm.get('email')?.setErrors({ emailExists: true });
          }
        }
      });
    }
  }
}
