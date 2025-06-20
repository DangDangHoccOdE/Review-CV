import { CommonModule, JsonPipe } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { UserServiceService } from '../../service/user-service.service';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, JsonPipe,RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  userForm: FormGroup;
  @Output() registerSuccess = new EventEmitter<void>();

  constructor(private fb: FormBuilder, private userService: UserServiceService, private router: Router,private toastr: ToastrService) {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      password: ['', [Validators.required, this.passwordValidator()]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordsMatchValidator.bind(this)
    });
  }

  // Kiểm tra password có chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt hay không
  passwordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null; // Nếu giá trị của control là rỗng thì trả về null

      // Kiểm tra xem password có chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt hay không
      const hasLetter = /[a-zA-Z]/.test(value);
      const hasDigit = /\d/.test(value);
      const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
      const isValidLength = value.length >= 8;

      // Tạo đổi tượng errors để chứa các lỗi của form
      const errors: ValidationErrors = {};// ValidationError là 1 đối tượng chứa các lỗi của form và là đối tượng JavaScript với các cặp key-value tương ứng với tên lỗi và giá trị lỗi

      // Check each condition
      if (!hasLetter) errors['noLetter'] = true;
      if (!hasDigit) errors['noDigit'] = true;
      if (!hasSpecialChar) errors['noSpecialChar'] = true;
      if (!isValidLength) errors['invalidLength'] = true;

      console.log(Object.keys(errors)); // trả về mảng các key của object errors

      // Nếu có lỗi thì trả về object errors, ngược lại trả về null
      return Object.keys(errors).length ? errors : null;
    };
  }

  passwordsMatchValidator(formGroup: FormGroup): ValidationErrors | null {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordsMismatch: true };
  }

  // Lấy thông báo lỗi của email
  getPasswordErrorMessage(): string {
    const control = this.userForm.get('password');
    const errors = [];

    if (control?.hasError('required')) {
      errors.push(' is required');
    }
    if (control?.hasError('invalidLength')) {
      errors.push('must be at least 8 characters long');
    }
    if (control?.hasError('noLetter')) {
      errors.push('must contain at least one letter');
    }
    if (control?.hasError('noDigit')) {
      errors.push('must contain at least one number');
    }
    if (control?.hasError('noSpecialChar')) {
      errors.push('must contain at least one special character');
    }

    // Nếu có lỗi thì trả về chuỗi thông báo lỗi, ngược lại trả về chuỗi rỗng
    return errors.length ? `Password ${errors.join(' and ')}` : '';
  }

  submitForm() {
    if (this.userForm.valid) {
      // // Phát tín hiệu thành công sau khi đăng ký
      // this.registerSuccess.emit();  
      this.signUpUser();
    }
    else {
      this.toastr.error('Vui lòng điền đầy đủ thông tin hợp lệ.', 'Form không hợp lệ');
    }
  }

  signUpUser() {
    this.userService.signUpUser(this.userForm.value).subscribe(
      (data: any) => {
        // Phát tín hiệu thành công sau khi đăng ký
        this.registerSuccess.emit();
        this.toastr.success('Registration completed successfully!', 'Success');
        this.router.navigateByUrl('/login');
      },
      (error) => {
        if (error.status === 409 || error.error.message === 'Email đã tồn tại') { // Xử lý lỗi email đã tồn tại
          this.userForm.get('email')?.setErrors({ emailExists: true });
        } else {
          // Xử lý lỗi khác nếu cần
          this.toastr.error('Registration failed. Please try again.', 'Error');
        }
      }
    );
  }
}
