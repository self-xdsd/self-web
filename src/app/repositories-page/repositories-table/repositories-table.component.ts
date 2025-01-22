import {Component, computed, effect, input, OnInit, signal, untracked} from '@angular/core';
import {RepositoriesService} from "../repositories.service";
import {Repository} from "../repository";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {toSignal} from "@angular/core/rxjs-interop";

@Component({
    selector: 'app-repositories-table',
    templateUrl: './repositories-table.component.html',
    styleUrls: ['./repositories-table.component.css'],
    standalone: false
})
export class RepositoriesTableComponent implements OnInit {

  /**
   * Managed repos, personal repos or Organization repos.
   */
  type = input.required<string>();
  // repositories?: Repository[];
  repositoriesPage?: Repository[];
  loading: boolean = false;

  page?: number;// = 1;
  pageSize?: number;//=10;
  collectionSize?: number;

  //#region Observables
  unsubscribeAll$ = new Subject<void>();
  //#endregion

  //#region Signals
  repositories = toSignal(this.repositoriesService.repos$);
  searchQuery = signal<string>('');
  filteredRepositories = computed(() => {
    const sq = this.searchQuery();
    return this.repositories()?.filter(
      (x:Repository) => x.repoFullName.toLowerCase().includes(sq.toLowerCase())
    );
  });
  //#endregion




  constructor(private repositoriesService: RepositoriesService) {
    effect(() => {
      const searchQuery = this.searchQuery();
      this.refreshRepositoriesPage();
    })
  }

  ngOnInit(): void {
    this.page = 1;
    this.pageSize = 10;
    this.loading = true;
    switch(this.type()) {
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

  ngOnDestroy(): void {
    this.unsubscribeAll$.next();
    this.unsubscribeAll$.complete();
  }

  getManagedRepos(): void {
    this.repositoriesService.getManagedRepos()
      .pipe(takeUntil(this.unsubscribeAll$))
      .subscribe({
        next:(repos) => {
          // if(!repos) return;
          // this.repositories = repos
          this.collectionSize = this.filteredRepositories()?.length;
          this.refreshRepositoriesPage();
          this.loading = false;
        },
        error:() => {
        this.loading = false;
        }

      });
  }

  getPersonalRepos(): void {
    this.repositoriesService.getPersonalRepos()
      .pipe(takeUntil(this.unsubscribeAll$))
      .subscribe(
      repos => {
        // if(!repos) return;
        // this.repositories = repos
        this.collectionSize = this.filteredRepositories()?.length;
        this.refreshRepositoriesPage();
        this.loading = false;
      },
      error => {
        this.loading = false;
      }
    )
  }

  getOrganizationRepos(): void {
    this.repositoriesService.getOrganizationRepos()
      .pipe(takeUntil(this.unsubscribeAll$))
      .subscribe(
      repos => {
        this.collectionSize = this.filteredRepositories()?.length;
        this.refreshRepositoriesPage();
        this.loading = false;
      },
      error => {
        this.loading = false;
      }
    )
  }

  refreshRepositoriesPage(): void {
    if(this.pageSize && this.page) {
      this.repositoriesPage = this.filteredRepositories()
        ?.map((repo, i) => ({id: i + 1, ...repo}))
        .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
    }
  }

  //#region Event Handlers

  hndSearch(search: string): void {
    this.searchQuery.set(search);
  }
  //#endregion

}
