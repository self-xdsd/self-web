import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StripePayoutMethodComponent } from './stripe-payout-method.component';

describe('StripePayoutMethodComponent', () => {
  let component: StripePayoutMethodComponent;
  let fixture: ComponentFixture<StripePayoutMethodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StripePayoutMethodComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StripePayoutMethodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
