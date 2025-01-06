import {Component, Input} from '@angular/core';
import {Project} from "../project";
import {formatEur} from "../../util/money";

@Component({
  selector: 'app-project-overview',
  templateUrl: './project-overview.component.html',
  styleUrl: './project-overview.component.css',
  standalone: false
})
export class ProjectOverviewComponent {

  @Input() project!: Project;

  automaticPaymentValue(): number {
    return 100 + this.project.manager.commission;
  }

  protected readonly formatEur = formatEur;
}
