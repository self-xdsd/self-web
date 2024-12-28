import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'app-homepage',
    templateUrl: './homepage.component.html',
    styleUrls: ['./homepage.component.css'],
    standalone: false
})
export class HomepageComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    console.log("HOMEPAGE INIT!!!");
  }

}
