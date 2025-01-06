import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletChartComponent } from './wallet-chart.component';

describe('WalletChartComponent', () => {
  let component: WalletChartComponent;
  let fixture: ComponentFixture<WalletChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
