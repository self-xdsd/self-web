import {Component, Input, OnInit} from '@angular/core';
import {RepositoriesService} from "../repositories.service";
import {Repository} from "../repository";

@Component({
  selector: 'app-repositories-table',
  templateUrl: './repositories-table.component.html',
  styleUrls: ['./repositories-table.component.css']
})
export class RepositoriesTableComponent implements OnInit {

  /**
   * Managed repos, personal repos or Organization repos.
   */
  @Input() type?: string;
  repositories?: Repository[];
  loading?: boolean;

  constructor(private repositoriesService: RepositoriesService) { }

  ngOnInit(): void {
    this.loading = true;
    switch(this.type) {
      case 'personal': {
        this.getPersonalRepos()
        break;
      }
      case 'organization': {
        this.getOrganizationRepos()
        break;
      }
      default: {
        this.getManagedRepos()
        break;
      }
    }
  }

  getManagedRepos(): void {
    this.repositoriesService.getManagedRepos().subscribe(
      repos => {
        this.repositories = repos
        this.loading = false;
      }
    )
  }

  getPersonalRepos(): void {
    this.repositoriesService.getPersonalRepos().subscribe(
      repos => {
        this.repositories = repos
        this.loading = false;
      }
    )
  }

  getOrganizationRepos(): void {
    this.repositoriesService.getOrganizationRepos().subscribe(
      repos => {
        this.repositories = repos
        this.loading = false;
      }
    )
  }

}
