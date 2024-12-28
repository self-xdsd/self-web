import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import { HttpClient } from "@angular/common/http";
import {catchError} from "rxjs/operators";
import {PayoutMethod} from "./payout-method";

@Injectable({
  providedIn: 'root'
})
export class PayoutMethodService {

  constructor(private http: HttpClient) { }

  getPayoutMethods(): Observable<PayoutMethod[]> {
    return this.http.get<PayoutMethod[]>("/api/contributor/payoutmethods").pipe(
      catchError(this.handleError<PayoutMethod[]>('getPayoutMethods', []))
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
