import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProfileServiceService } from '../../service/profile-service.service';
import { Profile } from '../../model/profile';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../model/user';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-create-profile',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './create-profile.component.html',
  styleUrl: './create-profile.component.css'
})
export class CreateProfileComponent implements OnInit {
  profileForm: FormGroup;
  profileEdit: Profile = new Profile();
  selectedFile: File | null = null;
  createProfile !: boolean;
  userCurrent: User = new User();
  previewUrl: string | ArrayBuffer | null = null;

  @Input() profile: Profile | undefined;
  @Input() idProfile?: number | null;
  @Output() closeForm = new EventEmitter<void>();

  constructor(private fb: FormBuilder, private profileService: ProfileServiceService,private toastr: ToastrService) {
    this.profileForm = this.fb.group({
      id: [''],
      title: ['', Validators.required],
      objective: [''],
      education: [''],
      workExperience: [''],
      skills: [''],
      typeProfile: [''],
      url: [''],
      idUser: [''],
      contact: this.fb.group({
        address: [''],
        phone: [''],
        email: ['']
      }),
      imageFile: [null]
    });
  }

  ngOnInit(): void {
    const userCurrentStrin = localStorage.getItem('userCurrent');
    if (userCurrentStrin) {
      this.userCurrent = JSON.parse(userCurrentStrin);
      
      // Thiết lập giá trị idUser trong form
      this.profileForm.patchValue({
        idUser: this.userCurrent.id
      });
    }
    if (this.profile) {
      this.profileForm.patchValue(this.profile);
      if (this.profile.contact) {
        this.profileForm.get('contact')?.patchValue(this.profile.contact);
      }
    }
  }


  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  updateProfile(): void {

    this.createProfile = this.isCreateProfile();
    if (this.profileForm.valid) {
      const formData: FormData = new FormData();

      const profileData = this.profileForm.value;
      for (const key in profileData) {
        if (profileData.hasOwnProperty(key) && key !== 'contact' && key !== 'imageFile') {
          formData.append(key, profileData[key]);
        }
      }

      if (profileData.contact) {
        for (const contactKey in profileData.contact) {
          if (profileData.contact.hasOwnProperty(contactKey)) {
            formData.append(`contact.${contactKey}`, profileData.contact[contactKey]);
          }
        }
      }

      const imageFile = this.profileForm.get('imageFile')?.value;
      if (this.selectedFile) {
        formData.append('image', this.selectedFile);
      }
 
      if (this.createProfile) {
        this.profileService.createProfile(formData).subscribe({
          next: response => {
            this.toastr.success('Profile created successfully', 'Success');
            window.location.reload();
            setTimeout(() => {
              window.location.reload();
            }, 1000);
          },
          error: error => {
            console.error('Error creating profile', error);
            this.toastr.error('Failed to create profile', 'Error');
          }
        });
      } else {
        this.profileService.updateProfile(formData).subscribe({
          next: response => {
            this.toastr.success('Profile updated successfully', 'Success');
            setTimeout(() => {
              window.location.reload();
            }, 1000);
          },
          error: error => {
            console.error('Error updating profile', error);
            this.toastr.error('Failed to update profile', 'Error');
          }
        });
      }
    }
  }

  onClose(): void {
    this.closeForm.emit();
  }

  isCreateProfile(): boolean {
    return !this.profile;
  }
}
