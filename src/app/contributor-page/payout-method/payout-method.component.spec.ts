import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayoutMethodComponent } from './payout-method.component';

describe('PayoutMethodComponent', () => {
  let component: PayoutMethodComponent;
  let fixture: ComponentFixture<PayoutMethodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PayoutMethodComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayoutMethodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
