import {Component, inject, Input, OnInit} from '@angular/core';
import {PayoutMethod} from "../payout-method";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-stripe-payout-method',
  templateUrl: './stripe-payout-method.component.html',
  styleUrl: './stripe-payout-method.component.css',
  standalone: false
})
export class StripePayoutMethodComponent implements OnInit {
  private modalService = inject(NgbModal);
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

  deleteConfirmation(): void {
    const modalRef = this.modalService.open(NgbdModalConfirmAutofocus, {container: '.modal-container'});
    modalRef.result.then(
      () => {
        this.delete();
      },
      () => {}
    );
  }
  delete(): void {
    console.log("ACTUAL DELETE HERE");
  }
}

@Component({
  selector: 'ngbd-modal-confirm-autofocus',
  standalone: true,
  template: `
		<div class="modal-header">
			<h4 class="modal-title" id="modal-title">Stripe account deletion</h4>
		</div>
    <div class="modal-body">
      <p>
        Are you sure you want to delete your Stripe Connect account?
      </p>
      <p>
        <span class="text-danger">This operation can not be undone.</span>
      </p>
    </div>
		<div class="modal-footer">
			<button type="button" class="btn btn-outline-secondary" (click)="modal.dismiss('cancel click')">Cancel</button>
			<button type="button" ngbAutofocus class="btn btn-danger" (click)="modal.close()">Ok</button>
		</div>
	`,
})
export class NgbdModalConfirmAutofocus {
  modal = inject(NgbActiveModal);
}
