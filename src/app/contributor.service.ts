import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Contract, Contributor, Invoice, ITask} from "./models/Contributor/contributor.types";
import {BehaviorSubject, Observable} from "rxjs";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ContributorService {
  //#region Injection Services
  private readonly http = inject(HttpClient);
  //#endregion
  //#region Constants
  private readonly contributorEndpoint: string = '/api/contributor';
  private readonly invoicesEndpoint: string = '/api/contributor/contracts/Maiorusergiu/repoTest1/invoices?role=PO';
  private readonly tasksEndpoint: string = '/api/contributor/contracts/Maiorusergiu/repoTest1/tasks?role=PO';
  //#endregion
  //#region Observables
  private onContributorChange$ = new BehaviorSubject<Contributor | null>(null);
  private onTasksChange$ = new BehaviorSubject<ITask[]>([]);
  private onInvoicesChange$ = new BehaviorSubject<Invoice[]>([]);


  tasks$ = this.onTasksChange$.asObservable();
  invoices$ = this.onInvoicesChange$.asObservable();
  contributor$ = this.onContributorChange$.asObservable();
  //#endregion
  constructor() { }

  //#region Public Methods
  getContributor(): Observable<Contributor> {
    return this.http.get<Contributor>(this.contributorEndpoint)
      .pipe(
        tap(
          (contributor ) => this.onContributorChange$.next(contributor)
        )
      );
  }

  getTasks(): Observable<ITask[]> {
    return this.http.get<ITask[]>(this.tasksEndpoint)
      .pipe(
        tap(
          (tasks ) => this.onTasksChange$.next(tasks)
        )
      );
  }

  getInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.invoicesEndpoint)
      .pipe(
        tap(
          (invoices ) => this.onInvoicesChange$.next(invoices)
        )
      );
  }
  //#endregion
}
