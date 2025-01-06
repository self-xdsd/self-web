import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-repo-badge',
  templateUrl: './repo-badge.component.html',
  styleUrl: './repo-badge.component.css',
  standalone: false
})
export class RepoBadgeComponent implements OnInit {
  activeTab?: string;
  @Input() provider!: String;
  @Input() repoFulLName!: String;

  ngOnInit(): void {
    this.activeTab = 'markdown';
  }
}
