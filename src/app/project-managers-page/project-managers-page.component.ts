import { Component, OnInit } from '@angular/core';
import {ProjectManager} from "../projectManager";
import {Router} from "@angular/router";
import {ProjectManagersService} from "../project-managers.service";
import {UserService} from "../user.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-project-managers-page',
  templateUrl: './project-managers-page.component.html',
  styleUrls: ['./project-managers-page.component.css']
})
export class ProjectManagersPageComponent implements OnInit {

  projectManagers?: ProjectManager[];

  addNewPmForm = new FormGroup({
    provider: new FormControl('github', [Validators.required]),
    username: new FormControl('', Validators.required),
    userId: new FormControl('', Validators.required),
    projectCommission: new FormControl('', Validators.required),
    contributorCommission: new FormControl('', Validators.required),
    token: new FormControl('', Validators.required)
  });

  blockSubmitButton?: boolean;

  constructor(
    private router: Router,
    private projectManagersService: ProjectManagersService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user && user.role === 'admin') {
          this.loadProjectManagers();
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
    this.blockSubmitButton = false;
  }

  loadProjectManagers(): void {
    this.projectManagersService.getProjectManagers().subscribe(
      projectManagers => this.projectManagers = projectManagers
    );
  }

  onSubmit() {
    this.blockSubmitButton = true;
    this.projectManagersService.addNewProjectManager(
      {
        userId: this.addNewPmForm.get('userId')?.value,
        username: this.addNewPmForm.get('username')?.value,
        provider: this.addNewPmForm.get('provider')?.value,
        commission: Number.parseFloat(this.addNewPmForm.get('projectCommission')?.value),
        contributorCommission: Number.parseFloat(this.addNewPmForm.get('contributorCommission')?.value),
        token: this.addNewPmForm.get('token')?.value
      } as ProjectManager
    ).subscribe(
      addedPm => {
        if(this.projectManagers && addedPm) {
          this.projectManagers.push(addedPm);
        }
        this.blockSubmitButton = false;
        this.addNewPmForm.reset(
          {
            provider: 'github'
          }
        );
      }
    )
  }

  addNewProjectManager(
    provider: string,
    username: string,
    userId: string,
    commission: string,
    contributorCommission: string,
    token: string
  ): void {
    this.projectManagersService.addNewProjectManager(
      {
        userId: userId,
        username: username,
        provider: provider,
        commission: Number.parseFloat(commission),
        contributorCommission: Number.parseFloat(contributorCommission),
        token: token
      } as ProjectManager
    ).subscribe(
      addedPm => {
        if(this.projectManagers && addedPm) {
          this.projectManagers.push(addedPm);
        }
      }
    )
  }
}
