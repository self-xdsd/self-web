import {Component, OnInit} from '@angular/core';
import {PayoutMethodService} from "./payout-method.service";
import {Repository} from "../../repositories-page/repository";
import {PayoutMethod} from "./payout-method";

@Component({
  selector: 'app-payout-method',
  templateUrl: './payout-method.component.html',
  styleUrl: './payout-method.component.css',
  standalone: false
})
export class PayoutMethodComponent implements OnInit {
  loading?: boolean;
  payoutMethods: PayoutMethod[];

  constructor(private payoutMethodService: PayoutMethodService) {
    this.payoutMethods = [];
  }

  ngOnInit(): void {
    this.loading = true;
    this.payoutMethodService.getPayoutMethods().subscribe(
      payoutMethods => {
        this.payoutMethods = payoutMethods
        this.loading = false;
        console.log("PMS HERE: " + payoutMethods.length);
      }
    )
  }

}
