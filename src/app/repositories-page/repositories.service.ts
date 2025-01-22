import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, of} from "rxjs";
import { HttpClient } from "@angular/common/http";
import {catchError, tap} from "rxjs/operators";
import {Repository} from "./repository";

@Injectable({
  providedIn: 'root'
})
export class RepositoriesService {

  //#region Observables
  private onReposChange$ = new BehaviorSubject<Repository[]>([]);

  repos$ = this.onReposChange$.asObservable();
  //#endregion

  constructor(private http: HttpClient) { }

  getManagedRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/managed").pipe(
      tap((repos) => this.onReposChange$.next(repos)),
      catchError(this.handleError<Repository[]>('getManagedRepos', []))
    );
  }

  getPersonalRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/personal").pipe(
      tap((repos) => this.onReposChange$.next(repos)),
      catchError(this.handleError<Repository[]>('getPersonalRepos', []))
    );
  }

  getOrganizationRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/orgs").pipe(
      tap((repos) => this.onReposChange$.next(repos)),
      catchError(this.handleError<Repository[]>('getOrganizationRepos', []))
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      if(error.status != 200) {
        console.log(`${operation} failed: ${error.message}`);
      }
      return of(result as T);
    };
  }
}
