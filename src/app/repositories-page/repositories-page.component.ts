import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {User} from "../user";
import {Router} from "@angular/router";

@Component({
  selector: 'app-repositories-page',
  templateUrl: './repositories-page.component.html',
  styleUrls: ['./repositories-page.component.css']
})
export class RepositoriesPageComponent implements OnInit {

  activeTab?: string;
  authenticatedUser?: User;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.activeTab = 'managed';
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
