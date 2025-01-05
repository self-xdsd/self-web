import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../user.service";
import {ProjectService} from "./project.service";
import {Project} from "./project";

@Component({
  selector: 'app-project-page',
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css',
  standalone: false
})
export class ProjectPageComponent implements OnInit {
  loading?: boolean;
  loadingActivate?: boolean;
  @Input() owner!: string;
  @Input() name!: string;
  activeTab?: string;
  project?: Project;

  constructor(
    private router: Router,
    private userService: UserService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user) {
          this.projectService.getProject(this.owner, this.name).subscribe(
            project => {
              this.loading = false;
              this.activeTab = 'overview';
              this.project = project;
            }
          );
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
  }

  activateRepo(): void {
    this.loadingActivate = true;
    this.projectService.activateRepo(this.owner, this.name).subscribe(
      project => {
        this.loadingActivate = false;
        this.project = project;
      }
    )
  }
}
