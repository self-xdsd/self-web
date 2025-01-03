import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-project-page',
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css',
  standalone: false
})
export class ProjectPageComponent {
  @Input() owner!: string;
  @Input() name!: string;
}
