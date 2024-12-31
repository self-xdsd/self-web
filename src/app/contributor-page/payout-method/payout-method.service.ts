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
    // return this.http.get<PayoutMethod[]>("/api/contributor/payoutmethods").pipe(
    //   catchError(this.handleError<PayoutMethod[]>('getPayoutMethods', []))
    // );
    return of(
      [
          {
            "type": "STRIPE",
            "identifier": "acct_1IPXNF2ZuNcX66fY",
            "account": {
              "id": "acct_1IPXNF2ZuNcX66fY",
              "object": "account",
              "business_profile": {
                "annual_revenue": null,
                "estimated_worker_count": null,
                "mcc": "8999",
                "name": null,
                "support_address": null,
                "support_email": null,
                "support_phone": null,
                "support_url": null,
                "url": "https://amihaiemil.com"
              },
              "capabilities": {
                "card_payments": "active",
                "transfers": "active"
              },
              "charges_enabled": true,
              "controller": {
                "fees": {
                  "payer": "application_express"
                },
                "is_controller": true,
                "losses": {
                  "payments": "application"
                },
                "requirement_collection": "stripe",
                "stripe_dashboard": {
                  "type": "express"
                },
                "type": "application"
              },
              "country": "RO",
              "created": 1614449922,
              "default_currency": "eur",
              "details_submitted": true,
              "email": "amihaiemil@gmail.com",
              "external_accounts": {
                "object": "list",
                "data": [
                  {
                    "id": "ba_1IPXPg2ZuNcX66fYaC8pNase",
                    "object": "bank_account",
                    "account": "acct_1IPXNF2ZuNcX66fY",
                    "account_holder_name": null,
                    "account_holder_type": null,
                    "account_type": null,
                    "available_payout_methods": [
                      "standard"
                    ],
                    "bank_name": "BANCA TRANSILVANIA S.A.",
                    "country": "RO",
                    "currency": "eur",
                    "default_for_currency": true,
                    "fingerprint": "FX5JBMq6X1MmygYE",
                    "future_requirements": {
                      "currently_due": [],
                      "errors": [],
                      "past_due": [],
                      "pending_verification": []
                    },
                    "last4": "1701",
                    "metadata": {},
                    "requirements": {
                      "currently_due": [],
                      "errors": [],
                      "past_due": [],
                      "pending_verification": []
                    },
                    "routing_number": "BTRLRO22",
                    "status": "new"
                  }
                ],
                "has_more": false,
                "total_count": 1,
                "url": "/v1/accounts/acct_1IPXNF2ZuNcX66fY/external_accounts"
              },
              "future_requirements": {
                "alternatives": [],
                "current_deadline": null,
                "currently_due": [],
                "disabled_reason": null,
                "errors": [],
                "eventually_due": [],
                "past_due": [],
                "pending_verification": []
              },
              "login_links": {
                "object": "list",
                "data": [],
                "has_more": false,
                "total_count": 0,
                "url": "/v1/accounts/acct_1IPXNF2ZuNcX66fY/login_links"
              },
              "metadata": {
                "address": "Arany Janos 10",
                "city": "Oradea",
                "country": "RO",
                "firstName": "Mihai",
                "isCompany": "false",
                "lastName": "Andronache",
                "zipCode": "410211"
              },
              "payouts_enabled": true,
              "requirements": {
                "alternatives": [],
                "current_deadline": null,
                "currently_due": [],
                "disabled_reason": null,
                "errors": [],
                "eventually_due": [],
                "past_due": [],
                "pending_verification": []
              },
              "settings": {
                "bacs_debit_payments": {
                  "display_name": null,
                  "service_user_number": null
                },
                "branding": {
                  "icon": null,
                  "logo": null,
                  "primary_color": null,
                  "secondary_color": null
                },
                "card_issuing": {
                  "tos_acceptance": {
                    "date": null,
                    "ip": null
                  }
                },
                "card_payments": {
                  "decline_on": {
                    "avs_failure": false,
                    "cvc_failure": false
                  },
                  "statement_descriptor_prefix": null,
                  "statement_descriptor_prefix_kana": null,
                  "statement_descriptor_prefix_kanji": null
                },
                "dashboard": {
                  "display_name": "Self XDSD",
                  "timezone": "Etc/UTC"
                },
                "invoices": {
                  "default_account_tax_ids": null
                },
                "payments": {
                  "statement_descriptor": "AMIHAIEMIL.COM",
                  "statement_descriptor_kana": null,
                  "statement_descriptor_kanji": null
                },
                "payouts": {
                  "debit_negative_balances": true,
                  "schedule": {
                    "delay_days": 7,
                    "interval": "daily"
                  },
                  "statement_descriptor": null
                },
                "sepa_debit_payments": {}
              },
              "tos_acceptance": {
                "date": 1614450080
              },
              "type": "express"
            }
          }
      ]
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
