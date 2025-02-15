import {
  Component,
  effect,
  inject,
  input,
  signal,
  untracked,
} from '@angular/core';
import { CommonModule, NgClass, NgForOf } from '@angular/common';
import {
  NgbPagination,
  NgbPopover,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ContributorService } from '../../contributor.service';
import { combineLatest, of, Subject } from 'rxjs';
import { finalize, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  Contract,
  Contributor,
  Invoice,
  ITask,
} from '../../models/Contributor/contributor.types';

type Tables = 'Contracts' | 'Invoices' | 'Tasks';

enum TableNames {
  Contracts = 'Contracts',
  Invoices = 'Invoices',
  Tasks = 'Tasks',
}

@Component({
  selector: 'app-contributor-contracts',
  imports: [
    CommonModule,
    NgForOf,
    NgbPopover,
    NgbPagination,
    ReactiveFormsModule,
    FormsModule,
    NgbTooltipModule,
    NgClass,
  ],
  templateUrl: './contributor-contracts.component.html',
  styleUrl: './contributor-contracts.component.css',
})
export class ContributorContractsComponent {
  //#region Injection Services
  private readonly contributorService = inject(ContributorService);
  //#endregion
  //#region Observables
  private unsubscribeAll$ = new Subject<void>();
  //#endregion
  //#region Signals
  invoices = toSignal(this.contributorService.invoices$);
  tasks = toSignal(this.contributorService.tasks$);
  contributor = toSignal(this.contributorService.contributor$);

  filteredContracts = signal<Contract[]>([]);
  filteredTasks = signal<ITask[]>([]);
  filteredInvoices = signal<Invoice[]>([]);

  selectedContract = signal<Contract | null>(null);
  //#endregion
  //#region Control Variables
  isLoading = signal<boolean>(false);
  isContractLoading = signal<boolean>(false);

  pageInvoices = signal<number>(1);
  pageSizeInvoices = signal<number>(10);
  collectionSizeInvoices = signal<number>(this.invoices()!.length || 0);

  pageTasks = signal<number>(1);
  pageSizeTasks = signal<number>(10);
  collectionSizeTasks = signal<number>(this.tasks()!.length || 0);

  pageContracts = signal<number>(1);
  pageSizeContracts = signal<number>(10);
  collectionSizeContracts = signal<number>(
    this.contributor()?.contracts.length || 0
  );
  //#endregion
  //#region Constants
  TableNames = TableNames;
  //#endregion

  constructor() {
    effect(() => {
      const contracts = this.contributor()?.contracts;
      const invoices = this.invoices();
      const tasks = this.tasks();

      untracked(() => {
        this.filteredInvoices.set(invoices || []);
        this.filteredContracts.set(contracts || []);
        this.filteredTasks.set(tasks || []);
        this.collectionSizeContracts.set(contracts?.length!);
        this.collectionSizeTasks.set(tasks?.length!);
        this.collectionSizeInvoices.set(invoices?.length!);
        this.hndChangePagination(TableNames.Contracts);
        this.hndChangePagination(TableNames.Invoices);
        this.hndChangePagination(TableNames.Tasks);
      });
    });
    this.subscribeToContributorData();
  }

  //#region Lifecycle Hooks
  ngOnInit(): void {}
  ngOnDestroy(): void {
    this.unsubscribeAll$.next();
    this.unsubscribeAll$.complete();
  }
  //#endregion

  //#region Private Methods
  private subscribeToContributorData(): void {
    this.isLoading.set(true);
    this.contributorService
      .getContributor()
      .pipe(
        takeUntil(this.unsubscribeAll$),
        switchMap((contributor: Contributor) => {
          this.isLoading.set(false);
          this.isContractLoading.set(true);
          const repoName = contributor.contracts[0].id.repoFullName;
          const role = contributor.contracts[0].id.role;
          if (contributor.contracts.length > 0) {
            this.selectedContract.set(contributor.contracts[0]);
            return combineLatest([
              this.contributorService.getTasks(repoName, role),
              this.contributorService.getInvoices(repoName, role),
            ]);
          }
          return of(null);
        }),
        finalize(() => this.isContractLoading.set(false))
      )
      .pipe(takeUntil(this.unsubscribeAll$))
      .subscribe({
        next: (res) => {
          console.log(res);
        },
      });
  }
  //#endregion

  //#region Private Methods
  private filterContracts(query: string): void {
    if (!query) {
      this.filteredContracts.set(this.contributor()?.contracts || []);
      return;
    }

    this.filteredContracts.set(
      this.contributor()?.contracts.filter((contract) => {
        return (
          contract.id.repoFullName.toLowerCase().includes(query) ||
          contract.id.role.toLowerCase().includes(query) ||
          contract.hourlyRate.toString().includes(query) ||
          contract.value.toString().includes(query)
        );
      }) || []
    );
  }

  private filterTasks(query: string): void {
    if (!query) {
      this.filteredTasks.set(this.tasks() || []);
      return;
    }
    this.filteredTasks.set(
      this.tasks()?.filter((task) => {
        console.log(task.deadline.toString().split('T')[0]);
        return (
          task.issueId.toLowerCase().includes(query) ||
          task.assignmentDate.toString().includes(query) ||
          task.deadline.toString().split('T')[0].includes(query) ||
          task.estimation.toString().split('T')[0].includes(query) ||
          task.value.toString().includes(query)
        );
      }) || []
    );
  }
  //#endregion

  //#region Event Handlers
  hndSearch(inputSearch: string): void {
    const query = inputSearch.toLowerCase().trim();

    // this.filterContracts(query);
    this.filterTasks(query);
  }

  hndLoadTasksAndInvoices(contract: Contract) {
    // /api/contributor/contracts/Maiorusergiu/repoTest1/invoices?role=PO
    const scrollingElement = document.scrollingElement || document.body;
    document.getElementById('tasks-table')?.scrollIntoView()!;
    document.getElementById('invoices-table')?.scrollIntoView()!;
    this.isLoading.set(true);
    this.selectedContract.set(contract);
    const repoName = contract.id.repoFullName;
    const role = contract.id.role;
    combineLatest([
      this.contributorService.getTasks(repoName, role),
      this.contributorService.getInvoices(repoName, role),
    ])
      .pipe(
        takeUntil(this.unsubscribeAll$),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: ([tasks, invoices]) => {
          console.log(tasks);
          console.log(invoices);
        },
        error: () => {},
      });
  }

  hndMarkProject(contract: Contract) {
    let contractToMark = this.contributor()?.contracts.find(
      (c: Contract) =>
        c.id.repoFullName === contract.id.repoFullName &&
        c.id.role === contract.id.role
    );
    if (!contractToMark) return;
    const repoName = contractToMark.id.repoFullName;
    const role = contractToMark.id.role;
    this.contributorService.markContract(repoName, role).subscribe({
      next: (res: Contract) => {
        let contractToUpdate = this.contributor()?.contracts.find(
          (c) => c === contract
        );
        if (!contractToUpdate) return;

        let index = this.contributor()?.contracts.indexOf(contractToUpdate!)!;

        this.contributor()!.contracts[index] = res;
      },
    });
  }

  hndChangePagination(table: Tables) {
    const startContractsPagination =
      (this.pageContracts() - 1) * this.pageSizeContracts();
    const endContractsPagination =
      startContractsPagination + this.pageSizeContracts();

    const startInvoicesPagination =
      (this.pageInvoices() - 1) * this.pageSizeInvoices();
    const endInvoicesPagination =
      startInvoicesPagination + this.pageSizeInvoices();

    const startTasksPagination = (this.pageTasks() - 1) * this.pageSizeTasks();
    const endTasksPagination = startTasksPagination + this.pageSizeTasks();

    switch (table) {
      case TableNames.Contracts:
        const contracts = this.contributor()?.contracts.slice(
          startContractsPagination,
          endContractsPagination
        );
        this.filteredContracts.set(contracts || []);
        break;
      case TableNames.Invoices:
        const invoices = this.invoices()!.slice(
          startInvoicesPagination,
          endInvoicesPagination
        );
        this.filteredInvoices.set(invoices || []);
        break;
      case TableNames.Tasks:
        const tasks = this.tasks()!.slice(
          startTasksPagination,
          endTasksPagination
        );
        this.filteredTasks.set(tasks || []);
        break;
    }
  }
  //#endregion
}
