import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {catchError} from "rxjs/operators";
import {ProjectManager} from "./projectManager";

@Injectable({
  providedIn: 'root'
})
export class ProjectManagersService {

  constructor(private http: HttpClient) { }

  getProjectManagers(): Observable<ProjectManager[]> {
    return this.http.get<ProjectManager[]>("/api/managers").pipe(
      catchError(this.handleError<ProjectManager[]>('getProjectManagers', undefined))
    );
  }

  addNewProjectManager(newPm: ProjectManager): Observable<ProjectManager> {
    return this.http.post<ProjectManager>(
      "/api/managers/new",
      newPm,
      {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      }
    ).pipe(
      catchError(this.handleError<ProjectManager>('addNewProjectManager', undefined))
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
