import {Component, OnInit} from '@angular/core';
import {User} from "./user";
import {UserService} from "./user.service";
import {PingService} from "./ping.service";
import {AppInfo} from "./appInfo";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  authenticatedUser?: User;
  appInfo?: AppInfo;

  constructor(
    private userService: UserService,
    private pingService: PingService
  ) { }

  ngOnInit(): void {
    this.getAppInfo();
    this.getAuthenticatedUser();
  }

  getAuthenticatedUser(): void {
    this.userService.getAuthenticatedUser()
      .subscribe(user => {
        this.authenticatedUser = user
      });
    console.log("APP COMPONENT INIT!!!");
  }

  getAppInfo(): void {
    this.pingService.getAppInfo()
      .subscribe(appInfo => {
        this.appInfo = appInfo;
      });
  }

}
