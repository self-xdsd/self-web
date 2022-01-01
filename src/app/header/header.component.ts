import {Component, Input, OnInit} from '@angular/core';
import {User} from "../user";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  /**
   * Authenticated user.
   */
  @Input() authenticatedUser?: User;

  constructor() { }

  ngOnInit(): void {
  }

}
