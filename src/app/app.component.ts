import {Component, OnInit} from '@angular/core';
import {User} from "./user";
import {UserService} from "./user.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  authenticatedUser?: User;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getAuthenticatedUser();
  }

  getAuthenticatedUser(): void {
    this.userService.getAuthenticatedUser()
      .subscribe(user => {
        this.authenticatedUser = user
      });
  }
}
