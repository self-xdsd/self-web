import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {catchError} from "rxjs/operators";
import {Project} from "./project";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private http: HttpClient) { }

  activateRepo(owner: string, name: string): Observable<Project> {
    return this.http.post<Project>("/api/projects/new", {owner: owner, name: name},
      {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      }).pipe(
      catchError(this.handleError<Project>('activateRepo', undefined))
    );
  }

  getProject(owner: string, name: string): Observable<Project> {
    return this.http.get<Project>("/api/projects/" + owner + "/" + name).pipe(
      catchError(this.handleError<Project>('getProject', undefined))
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
