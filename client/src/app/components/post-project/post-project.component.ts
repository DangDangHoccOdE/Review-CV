import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProjectServiceService } from '../../service/project-service.service';
import { Project } from '../../model/project';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-post-project',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './post-project.component.html',
  styleUrl: './post-project.component.css'
})
export class PostProjectComponent implements OnInit {
  updateProject:Project = new Project();
  projects:Project[]=[]
  project:Project=new Project;
  projectForm: FormGroup;
  @Input() idUser?:number;
  @Input() projectEdit: Project | null = null;
  @Input() idProfile?: number | null;
  @Output() closeForm = new EventEmitter<void>();
  constructor(private projectService:ProjectServiceService, private fb:FormBuilder, private router: Router, private toastr: ToastrService){
    this.projectForm = this.fb.group({
      id: [''],
      title: [''],
      description: [''],
      url: [''],
      createAt: [''],
      imageId: [''],
      display: [''],
      imageFile: [null],
      idProfile: [this.idProfile]
    })
  }

  ngOnInit(): void {
    if(this.projectEdit!=null){
      this.projectForm.patchValue(this.projectEdit)
    }
  }

  createProjectByUser(project:Project){
    this.projectService.createProjectByUser(project).subscribe(data=>{
      this.project=data
    })
  }

  updateProjectByUser(project:Project){
    this.projectService.updateProjectByUser(project).subscribe(data=>{
      this.project=data
    })
  }

  updatedProject(event: Event): void {
    event.preventDefault();
    if (this.projectForm.valid) {
      this.updateProject = this.projectForm.value;
      if (this.idProfile !== null && this.idProfile !== undefined) {
        this.updateProject.idProfile = this.idProfile;
    }
      if(this.updateProject?.id){
        this.projectService.updateProjectByUser(this.updateProject).subscribe({
        next: (response) => {
          this.toastr.success('Project updated successfully', 'Success');
          setTimeout(() => {
            this.router.navigate(['/list-project']).then(() => {
              window.location.reload();
            });
          }, 1000);
        },
        error: (error) => {
          console.error('An error occurred:', error);
          this.toastr.error('An error occurred while updating the project.', 'Error');
        }
      });
      }else{
        this.projectService.createProjectByUser(this.updateProject).subscribe({
          next: (response) => {
            this.toastr.success('Project created successfully', 'Success');
            this.router.navigate(['/list-project']).then(() => {
              window.location.reload();
            });
          },
          error: (error) => {
            console.error('An error occurred:', error);
            this.toastr.error('An error occurred while creating the project.', 'Error');
          }
        });
      }
      
    } else {
      console.error('Form is invalid');
      this.toastr.warning('Please fill in the form correctly before submitting.', 'Form Invalid');
    }
  }

  onClose() {
    this.closeForm.emit();
  }

}
