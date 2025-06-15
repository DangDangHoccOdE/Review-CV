import { Component, OnInit } from '@angular/core';
import { CompanyServiceService } from '../../service/company-service.service';
import { Company } from '../../model/company';
import { User } from '../../model/user';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-company',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './company.component.html',
  styleUrl: './company.component.css'
})
export class CompanyComponent implements OnInit {
  company: Company = new Company();
  userCurrent: User = new User();
  isShowingCompany: boolean = false;
  previewUrl: string | ArrayBuffer | null = null;
  selectedImageFile: File | null | undefined = null;
  editedCompany: Company = new Company();

  constructor(private companyService: CompanyServiceService) {}

  ngOnInit(): void {
    const userCurrentString = localStorage.getItem('userCurrent');
    if (userCurrentString) {
      this.userCurrent = JSON.parse(userCurrentString);
    }

    if (this.userCurrent.role === 'manager' && this.userCurrent.id) {
      this.getCompanyByManager(this.userCurrent.id);
    } else if (this.userCurrent.role === 'hr' && this.userCurrent.id) {
      this.getCompanyByIdHR(this.userCurrent.id);
    }
  }

  getCompanyByIdHR(id: number): void {
    this.companyService.getCompanyByIddHr(id).subscribe(data => {
      this.company = data;
    });
  }

  getCompanyByManager(idManager: number): void {
    this.companyService.getCompanyByManager(idManager).subscribe(data => {
      this.company = data;
      localStorage.setItem('idCompany', String(this.company.id));
    });
  }

  editCompany(): void {
    this.editedCompany = { ...this.company }; // clone dữ liệu
    this.previewUrl = typeof this.company.url === 'string' ? this.company.url : null; // giữ ảnh cũ nếu không chọn mới
    this.selectedImageFile = null;
    this.isShowingCompany = true;
  }

  closeModal(): void {
    this.isShowingCompany = false;
  }

  onImageSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedImageFile = fileInput.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.selectedImageFile);
    }
  }

  saveCompany(): void {
    const formData = new FormData();
    formData.append('companyDTO', new Blob([JSON.stringify(this.editedCompany)], { type: 'application/json' }));
    
    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile);
    }

    this.companyService.updateCompany(formData).subscribe(() => {
      this.company = { ...this.editedCompany };
      this.isShowingCompany = false;
      // Load lại dữ liệu công ty mới nhất
      if (this.userCurrent.role === 'manager' && this.userCurrent.id) {
        this.getCompanyByManager(this.userCurrent.id);
      } else if (this.userCurrent.role === 'hr' && this.userCurrent.id) {
        this.getCompanyByIdHR(this.userCurrent.id);
      }

      alert('Company details updated successfully');
    });
  }
}
