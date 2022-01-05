import { Component, OnInit } from '@angular/core';
import {ProjectManager} from "../projectManager";
import {Router} from "@angular/router";
import {ProjectManagersService} from "../project-managers.service";
import {UserService} from "../user.service";

@Component({
  selector: 'app-project-managers-page',
  templateUrl: './project-managers-page.component.html',
  styleUrls: ['./project-managers-page.component.css']
})
export class ProjectManagersPageComponent implements OnInit {

  projectManagers?: ProjectManager[];

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
  }

  loadProjectManagers(): void {
    this.projectManagersService.getProjectManagers().subscribe(
      projectManagers => this.projectManagers = projectManagers
    );
  }
}
