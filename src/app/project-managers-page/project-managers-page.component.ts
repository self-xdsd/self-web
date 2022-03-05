import { Component, OnInit } from '@angular/core';
import {ProjectManager} from "../projectManager";
import {Router} from "@angular/router";
import {ProjectManagersService} from "../project-managers.service";
import {UserService} from "../user.service";
import {
  AbstractControl,
  FormControl,
  FormGroup, ValidationErrors,
  ValidatorFn
} from "@angular/forms";
import {notBlank} from "../validators/commonValidators";

@Component({
  selector: 'app-project-managers-page',
  templateUrl: './project-managers-page.component.html',
  styleUrls: ['./project-managers-page.component.css']
})
export class ProjectManagersPageComponent implements OnInit {

  projectManagers?: ProjectManager[];

  addNewPmForm = new FormGroup({
    provider: new FormControl(
      'github',
      [notBlank(), this.allowedProviders(['github', 'gitlab'])]
    ),
    username: new FormControl('', [notBlank(), this.withoutAtSymbol()]),
    userId: new FormControl('', notBlank()),
    projectCommission: new FormControl('', notBlank()),
    contributorCommission: new FormControl('', notBlank()),
    token: new FormControl('', notBlank())
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

  /**
   * Custom validator for provider field.
   * @param providers Allowed providers.
   */
  allowedProviders(providers: string[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const allowed = providers.includes(control.value);
      return allowed ? null : {forbiddenProvider: {value: control.value}};
    };
  }

  /**
   * Custom validator for the username field (shouldn't start with '@').
   * @param providers Allowed providers.
   */
  withoutAtSymbol(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const allowed = !control.value?.startsWith('@');
      return allowed ? null : {forbiddenUsername: {value: control.value}};
    };
  }
}
