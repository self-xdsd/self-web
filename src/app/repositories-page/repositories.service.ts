import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {catchError} from "rxjs/operators";
import {Repository} from "./repository";

@Injectable({
  providedIn: 'root'
})
export class RepositoriesService {

  constructor(private http: HttpClient) { }

  getManagedRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/managed").pipe(
      catchError(this.handleError<Repository[]>('getManagedRepos', []))
    );
  }

  getPersonalRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/personal").pipe(
      catchError(this.handleError<Repository[]>('getPersonalRepos', []))
    );
  }

  getOrganizationRepos(): Observable<Repository[]> {
    return this.http.get<Repository[]>("/api/repositories/orgs").pipe(
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
