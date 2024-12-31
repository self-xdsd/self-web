import {Component, Input, OnInit} from '@angular/core';
import {PayoutMethod} from "../payout-method";

@Component({
  selector: 'app-stripe-payout-method',
  templateUrl: './stripe-payout-method.component.html',
  styleUrl: './stripe-payout-method.component.css',
  standalone: false
})
export class StripePayoutMethodComponent implements OnInit {
  @Input() payoutMethod!: PayoutMethod;
  onboardingNeeded: boolean;
  extraRequirements: boolean;

  constructor() {
    this.onboardingNeeded = false;
    this.extraRequirements = false;
  }

  ngOnInit(): void {
    // this.onboardingNeeded = true;
    // this.extraRequirements = true;
    this.onboardingNeeded = !this.payoutMethod.account.details_submitted;
    this.extraRequirements = this.payoutMethod.account.details_submitted && this.payoutMethod.account.requirements.currently_due.length > 0
  }
}
