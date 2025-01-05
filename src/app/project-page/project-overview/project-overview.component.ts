import {Component, Input} from '@angular/core';
import {Project} from "../project";

@Component({
  selector: 'app-project-overview',
  templateUrl: './project-overview.component.html',
  styleUrl: './project-overview.component.css',
  standalone: false
})
export class ProjectOverviewComponent {

  @Input() project!: Project;

}
