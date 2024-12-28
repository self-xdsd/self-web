import { Component, OnInit } from '@angular/core';
import {User} from "../user";
import {Router} from "@angular/router";
import {UserService} from "../user.service";

@Component({
    selector: 'app-contributor-page',
    templateUrl: './contributor-page.component.html',
    styleUrls: ['./contributor-page.component.css'],
    standalone: false
})
export class ContributorPageComponent implements OnInit {

  activeTab?: string;
  authenticatedUser?: User;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.activeTab = 'contracts';
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user) {
          this.authenticatedUser = user;
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
  }

}
