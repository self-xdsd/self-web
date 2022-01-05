import {Component, Input, OnInit} from '@angular/core';
import {User} from "../user";
import {AppInfo} from "../appInfo";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Input() appInfo?: AppInfo;
  @Input() authenticatedUser?: User;

  constructor() { }

  ngOnInit(): void {
  }

}
