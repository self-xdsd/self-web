import {Component, inject, signal} from '@angular/core';
import {CommonModule, NgClass, NgForOf} from "@angular/common";
import {NgbPagination, NgbPopover, NgbTooltipModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ContributorService} from "../../contributor.service";
import {combineLatest, of, Subject} from "rxjs";
import {finalize, switchMap, takeUntil, tap} from "rxjs/operators";
import {toSignal} from "@angular/core/rxjs-interop";
import {Contract, Contributor} from "../../models/Contributor/contributor.types";


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
    NgClass
  ],
  templateUrl: './contributor-contracts.component.html',
  styleUrl: './contributor-contracts.component.css'
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
  selectedContract = signal<Contract | null>(null);
  //#endregion
  //#region Control Variables
  isLoading = signal<boolean>(false);
  isContractLoading = signal<boolean>(false);
  //#endregion

  constructor() {
    this.subscribeToContributorData();
  }

  //#region Lifecycle Hooks
  ngOnInit(): void {

  }
  ngOnDestroy(): void {
    this.unsubscribeAll$.next();
    this.unsubscribeAll$.complete();
  }
  //#endregion

  //#region Private Methods
  private subscribeToContributorData(): void {
    this.isLoading.set(true);
    this.contributorService.getContributor()
      .pipe(
        takeUntil(this.unsubscribeAll$),
        switchMap((contributor: Contributor) => {
          this.isLoading.set(false);
          this.isContractLoading.set(true);
          const repoName = contributor.contracts[0].id.repoFullName;
          const role = contributor.contracts[0].id.role;
          if(contributor.contracts.length > 0) {
            this.selectedContract.set(contributor.contracts[0]);
            return combineLatest([
              this.contributorService.getTasks(repoName, role),
              this.contributorService.getInvoices(repoName, role)
            ]);
          }
          return of(null);

        }),
        finalize(() => this.isContractLoading.set(false))
      )
      .subscribe({
      next:(res) => {
        console.log(res);
      }

    })
  }
  //#endregion

  //#region Event Handlers
  hndSearch(inputSearch: string): void {

  }

  hndLoadTasksAndInvoices(contract: Contract) {
    // /api/contributor/contracts/Maiorusergiu/repoTest1/invoices?role=PO
    const scrollingElement = (document.scrollingElement || document.body);
    document.getElementById("tasks-table")?.scrollIntoView()!;
    document.getElementById("invoices-table")?.scrollIntoView()!;
    this.isLoading.set(true);
    this.selectedContract.set(contract);
    const repoName = contract.id.repoFullName;
    const role = contract.id.role;
    combineLatest([
      this.contributorService.getTasks(repoName, role),
      this.contributorService.getInvoices(repoName, role)
    ])
      .pipe(
        takeUntil(this.unsubscribeAll$),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next:([tasks, invoices]) => {
          console.log(tasks);
          console.log(invoices);
        },
        error:() => {

        }
      })
  }

  hndMarkProject(contract: Contract) {
    let contractToMark = this.contributor()?.contracts.find((c: Contract) => c === contract);
    if(!contractToMark) return;
    const repoName = contractToMark.id.repoFullName;
    const role = contractToMark.id.role;
    this.contributorService.markContract(repoName, role)
      .subscribe({
        next:(res: Contract) => {
          contractToMark = res;
        }

      });
  }
  //#endregion

}
