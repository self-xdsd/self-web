import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../user.service";

@Component({
  selector: 'app-project-page',
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css',
  standalone: false
})
export class ProjectPageComponent implements OnInit {
  @Input() owner!: string;
  @Input() name!: string;
  provider?: string;
  activeTab?: string;

  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.activeTab = 'overview';
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user) {
          this.provider = user.provider;
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
  }
}
