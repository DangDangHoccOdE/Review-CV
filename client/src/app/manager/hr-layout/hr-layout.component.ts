import { Company } from './../../model/company';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UserServiceService } from '../../service/user-service.service';
import { User } from '../../model/user';
import { Form, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompanyServiceService } from '../../service/company-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-hr-layout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './hr-layout.component.html',
  styleUrl: './hr-layout.component.css'
})
export class HrLayoutComponent implements OnInit {
  userCurrent: User = new User();
  company:Company = new Company();
  idCompany?:number;
  users: User[] = [];
  filteredUsers: User[] = [];
  userForm: FormGroup;
  isModalOpen: boolean = false;
  isEdit: boolean = false;
  modalTitle: string = 'New Employee';
  modalButtonLabel: string = 'Save';
  id: number | undefined;
  searchTerm: string = '';
  searchCriteria: string = 'id';

  constructor(private userService: UserServiceService, private fb: FormBuilder, private companyService: CompanyServiceService, private toastr: ToastrService) {
    this.userForm = this.fb.group({
      id: [''],
      idEmployee: ['', Validators.required],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  ngOnInit() {
    const userCurrentStrin = localStorage.getItem('userCurrent');
    if (userCurrentStrin) {
      this.userCurrent = JSON.parse(userCurrentStrin);
    }
    const userCurrentString = localStorage.getItem('idCompany');
    if(userCurrentString){
      this.idCompany = Number(userCurrentString);
    }
    if(this.idCompany){
      this.getCompanyById(this.idCompany);
    }
  }

  getCompanyById(id:number){
    this.companyService.getCompanyById(id).subscribe(data => {
      this.company = data;
      if(this.company.idHR){
        this.getListHrByIdCompany(this.company.idHR);
      }
    });
  }

  getListHrByIdCompany(id:number[]){
    this.userService.getListUserById(id).subscribe(data => {
      this.users = data;
      this.filteredUsers = this.users;
      console.log(this.filteredUsers)
    });
  }

  getAllUsers(): void {
    this.userService.getAllUser().subscribe(data => {
      this.users = data.filter(user => user.role === 'hr');
      this.filteredUsers = this.users;
      console.log(this.filteredUsers)
    });
  }

  openModal(action: 'add' | 'edit', user?: User): void {
    this.isModalOpen = true;
    this.isEdit = action === 'edit';
    this.modalTitle = this.isEdit ? 'Edit Hr' : 'New Hr';
    this.modalButtonLabel = this.isEdit ? 'Update' : 'Save';

    if (this.isEdit && user) {
      this.userForm.patchValue({
        id: user.id,
        idEmployee: user.idEmployee,
        name: user.name,
        email: user.email,
        password: user.password,
      });
    } else {
      this.userForm.reset();
    }
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      if (this.isEdit) {
        this.updateUser();
      } else {
        this.saveUser();
      }
    } else {
      this.toastr.warning("Please fill out all required fields correctly.", "Invalid Input")
    }
  }
  

  saveUser(): void {
    const newUser: User = this.userForm.value;
    newUser.role = 'hr';
    // Kiểm tra xem có trường dữ liệu nào còn trống không
    if (!newUser.idEmployee || !newUser.name || !newUser.email || !newUser.password) {
      this.toastr.warning("Please fill out all required fields!!!","warning")
      return;
    }
    if(this.idCompany){
        this.companyService.setHrToCompany(newUser, this.idCompany).subscribe(data => {
        this.filteredUsers = this.users;
        this.getAllUsers();
        this.closeModal();
        this.toastr.success("User added successfully", "Success")
      }, error => {
        console.error('Lỗi khi đăng ký người dùng:', error);
        if (error.status === 409 || error.error.message === 'Email đã tồn tại') {
          this.userForm.get('email')?.setErrors({ emailExists: true });
        }
      });
      console.log(JSON.stringify(newUser));
    }else{
      console.log("K co id company")
    }
    
  }

  updateUser(): void {
    // Lấy dữ liệu từ form
    const updatedUser: User = this.userForm.value;
    updatedUser.role = 'hr';
  
    // Tìm dữ liệu hiện tại của người dùng
    const currentUser = this.users.find(user => user.idEmployee === this.userForm.get('idEmployee')?.value);
    if (!currentUser) {
      this.toastr.warning("User not found", "Warning");
      return;
    }

    if(currentUser.id === updatedUser.id && currentUser.email === updatedUser.email && currentUser.name === updatedUser.name && currentUser.password === updatedUser.password){
      this.toastr.info("Nothing to update!!!", "Info");
      return;
    }

  
    // Kiểm tra xem có trường dữ liệu nào còn trống không
    if (!updatedUser.id || !updatedUser.name || !updatedUser.email || !updatedUser.password) {
      this.toastr.warning("Please fill out all required fields!!!", "Warning");
      return;
    }
  
    this.userService.update(updatedUser).subscribe(data => {
      if (data) {
        const index = this.users.findIndex(user => user.id === updatedUser.id);
        if (index !== -1) {
          // Cập nhật danh sách người dùng
          this.users[index] = updatedUser;
          this.getAllUsers(); // Tải lại danh sách người dùng
          this.filteredUsers = this.users;
          this.toastr.success("User updated successfully", "Success");
        }
      }
      this.closeModal();
    }, error => {
      console.error('Error updating user:', error);
      if (error.status === 409 || error.error.message === 'Email already exists') {
        this.userForm.get('email')?.setErrors({ emailExists: true });
      }
    });
  }


  deleteUser(id: number): void {
    const confirmDelete = confirm('Are you sure you want to delete this user?');
    if (confirmDelete) {
      this.userService.deleteUser(id).subscribe(data => {
        this.users = this.users.filter(user => user.id !== id);
        this.getAllUsers();
        this.filteredUsers = this.users;
        this.toastr.success("User deleted successfully", "Success");
      }, error => {
        console.error('Error deleting user:', error);
        this.toastr.error("Error deleting user", "Error");
      });
    } else {
      console.log('User deletion canceled');
    }
  }

  onSearch(): void {
    if (!this.searchTerm) {
      this.filteredUsers = this.users;
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user => {
      if (this.searchCriteria === 'id') {
        return user.id?.toString().toLowerCase().includes(term);
      } else if (this.searchCriteria === 'name') {
        return user.name?.toLowerCase().includes(term);
      } else if (this.searchCriteria === 'email') {
        return user.email?.toLowerCase().includes(term);
      }
      return false;
    });
  }

  onCriteriaChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.searchCriteria = target.value;
    this.onSearch();
  }


}
