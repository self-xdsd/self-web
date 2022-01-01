import {Component, Input, OnInit} from '@angular/core';
import {User} from "../user";
import {UserService} from "../user.service";

@Component({
  selector: 'app-authenticated-menu',
  templateUrl: './authenticated-menu.component.html',
  styleUrls: ['./authenticated-menu.component.css']
})
export class AuthenticatedMenuComponent implements OnInit {

  @Input() authenticatedUser?: User;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
  }

  logout(): void {
    this.userService.logout().subscribe(user => {
      window.location.reload();
    });
  }

}
