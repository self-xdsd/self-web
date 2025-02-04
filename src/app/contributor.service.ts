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

  getTasks(repoName: string, role: string): Observable<ITask[]> {
    const endpoint: string = `/api/contributor/contracts/${repoName}/tasks?role=${role}`;
    return this.http.get<ITask[]>(endpoint)
      .pipe(
        tap(
          (tasks ) => this.onTasksChange$.next(tasks)
        )
      );
  }

  getInvoices(repoName: string, role: string): Observable<Invoice[]> {
    const endpoint: string = `/api/contributor/contracts/${repoName}/invoices?role=${role}`;
    return this.http.get<Invoice[]>(endpoint)
      .pipe(
        tap(
          (invoices ) => this.onInvoicesChange$.next(invoices)
        )
      );
  }
  //#endregion
}
