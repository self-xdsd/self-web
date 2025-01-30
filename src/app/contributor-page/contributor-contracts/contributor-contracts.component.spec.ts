import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributorContractsComponent } from './contributor-contracts.component';

describe('ContributorContractsComponent', () => {
  let component: ContributorContractsComponent;
  let fixture: ComponentFixture<ContributorContractsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContributorContractsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContributorContractsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
