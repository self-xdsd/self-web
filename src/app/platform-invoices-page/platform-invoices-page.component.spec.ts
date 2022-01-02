import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformInvoicesPageComponent } from './platform-invoices-page.component';

describe('PlatformInvoicesPageComponent', () => {
  let component: PlatformInvoicesPageComponent;
  let fixture: ComponentFixture<PlatformInvoicesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlatformInvoicesPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlatformInvoicesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
