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
  repositoriesPage?: Repository[];
  loading?: boolean;

  page?: number;// = 1;
  pageSize?: number;//=10;
  collectionSize?: number;

  constructor(private repositoriesService: RepositoriesService) { }

  ngOnInit(): void {
    this.page = 1;
    this.pageSize = 10;
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
        this.collectionSize = this.repositories.length;
        this.refreshRepositoriesPage();
        this.loading = false;
      }
    )
  }

  getPersonalRepos(): void {
    this.repositoriesService.getPersonalRepos().subscribe(
      repos => {
        this.repositories = repos
        this.collectionSize = this.repositories.length;
        this.refreshRepositoriesPage();
        this.loading = false;
      }
    )
  }

  getOrganizationRepos(): void {
    this.repositoriesService.getOrganizationRepos().subscribe(
      repos => {
        this.repositories = repos
        this.collectionSize = this.repositories.length;
        this.refreshRepositoriesPage();
        this.loading = false;
      }
    )
  }

  refreshRepositoriesPage(): void {
    if(this.repositories && this.pageSize && this.page) {
      this.repositoriesPage = this.repositories
        .map((repo, i) => ({id: i + 1, ...repo}))
        .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
    }
  }

}
