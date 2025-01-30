import {Component, inject} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {NgbPagination, NgbPopover, NgbTooltipModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ContributorService} from "../../contributor.service";
import {combineLatest, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {toSignal} from "@angular/core/rxjs-interop";
import {Contract} from "../../models/Contributor/contributor.types";

declare var bootstrap: any

@Component({
  selector: 'app-contributor-contracts',
  imports: [
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
  //#endregion

  constructor() {
    this.subscribeToContributorData();
  }

  //#region Lifecycle Hooks
  ngOnInit(): void {
    // Bootstrap tooltip initialization
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl)
    })
  }
  ngOnDestroy(): void {
    this.unsubscribeAll$.next();
    this.unsubscribeAll$.complete();
  }
  //#endregion

  //#region Private Methods
  private subscribeToContributorData(): void {
    this.contributorService.getTasks().subscribe({
      next:(res) => {
        console.log(res);
      }

    })
    this.contributorService.getContributor().subscribe({
      next:(res) => {
        console.log(res);
      }

    })
    this.contributorService.getInvoices().subscribe({
      next:(res) => {
        console.log(res);
      }

    })
    // combineLatest([
    //   this.contributorService.getContributor(),
    //   this.contributorService.getTasks(),
    //   this.contributorService.getInvoices()
    // ])
    //   .pipe(takeUntil(this.unsubscribeAll$))
    //   .subscribe({
    //     next:([contributor, tasks, invoices]) => {
    //       console.log(contributor);
    //       console.log(tasks);
    //       console.log(invoices);
    //     },
    //     error:(err) => {
    //
    //     }
    //   })
  }
  //#endregion

  //#region Event Handlers
  hndSearch(inputSearch: string): void {

  }

  hndLoadTasksAndInvoices(contract: Contract) {
    console.log(contract);
  }

  hndMarkProject(contract: Contract) {
    console.log(contract);
  }
  //#endregion

}
