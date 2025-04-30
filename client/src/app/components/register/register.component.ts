import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { UserServiceService } from '../../service/user-service.service';
import { Router } from '@angular/router';
import e from 'express';
import { console } from 'inspector';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, JsonPipe
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  userForm: FormGroup;
  @Output() registerSuccess = new EventEmitter<void>();

  constructor(
    private fb: FormBuilder,
    private userService: UserServiceService,
    private router: Router
  ){
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      password: ['', [Validators.required, this.passwordValidator()]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordsMatchValidator.bind(this) });
  }

  // Kiểm tra password có chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt hay không
  passwordValidator() : ValidatorFn{
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if(!value) 
        return null

      // Kiểm tra xem password có chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt hay không
      const hasLetter = /[a-zA-Z]/.test(value);
      const hasDigit = /\d/.test(value);
      const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
      const isValidLength = value.length >= 8;

      // Tạo đối tượng errors:
      const errors: ValidationErrors = {};

      // Check từng điều kiện và thêm vào errors
      if (!hasLetter) {
        errors['noLetter'] = true;
      }

      if( !hasDigit) {
        errors['noDigit'] = true;
      }
      if( !hasSpecialChar) {
        errors['noSpecialChar'] = true;
      }
      if( !isValidLength) {
        errors['invalidLength'] = true;
      }
      console.log(Object.keys(errors));

      // Nếu có lỗi thì trả về object errors, nếu không thì trả về null
      return Object.keys(errors).length ? errors : null;
    }
  }

  passwordsMatchValidator(formGroup: FormGroup): ValidationErrors | null{
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordsMismatch: true };
  }

  getPasswordErrorMessage(): string {
    const control = this.userForm.get('password');
    const errors = [];

    if(control?.hasError('required')) {
      errors.push(' is required');
    }

    if(control?.hasError('invalidLength')){
      errors.push(' must be at least 8 characters long');
    }

    if(control?.hasError('noLetter')){
      errors.push(' must contain at least one letter');
    }

    if(control?.hasError('noDigit')){
      errors.push(' must contain at least one digit');
    }

    if(control?.hasError('noSpecialChar')){
      errors.push(' must contain at least one special character');
    }

    return errors.length ? `Password${errors.join(', ')}` : '';
  }

  submitForm(){
    if(this.userForm.valid){
      console.log(this.userForm.value + " submitForm");
      this.signUpUser();
    }else{
      console.log('Form is invalid');
    }
  }

  signUpUser(){
    this.userService.signUpUser(this.userForm.value).subscribe(
      (data: any) => {
        console.log("Người dùng đăng ký thành công", data);

        this.registerSuccess.emit(); // Emit the event to notify the parent component
        this.router.navigateByUrl('/login'); 
      },
      (error) => {
        console.error("Đăng ký không thành công", error);
        if(error.status === 409 || error.error.message === "Email đã tồn tại"){
          this.userForm.get('email')?.setErrors({emailExists: true});
        }else{
          // other error
        }
      }
    )
  }
}