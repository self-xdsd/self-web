import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {catchError} from "rxjs/operators";
import {PlatformInvoice} from "./platformInvoice";

@Injectable({
  providedIn: 'root'
})
export class PlatformInvoicesService {

  constructor(private http: HttpClient) { }

  getPlatformInvoices(): Observable<PlatformInvoice[]> {
    return this.http.get<PlatformInvoice[]>("/api/invoices").pipe(
      catchError(this.handleError<PlatformInvoice[]>('getPlatformInvoices', undefined))
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
