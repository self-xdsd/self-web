import { Component, OnInit } from '@angular/core';
import {User} from "../user";
import {Router} from "@angular/router";
import {UserService} from "../user.service";

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.css']
})
export class UserPageComponent implements OnInit {
  authenticatedUser?: User;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user) {
          this.authenticatedUser = user;
          let provider = this.authenticatedUser.provider;
          this.authenticatedUser.provider = provider.charAt(0).toUpperCase() + provider.slice(1);
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
  }

}
