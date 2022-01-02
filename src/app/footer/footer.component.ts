import {Component, Input, OnInit} from '@angular/core';
import {AppInfo} from "../appInfo";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  @Input() appInfo?: AppInfo;

  constructor() { }

  ngOnInit(): void {
  }

}
