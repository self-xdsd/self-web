import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepoBadgeComponent } from './repo-badge.component';

describe('RepoBadgeComponent', () => {
  let component: RepoBadgeComponent;
  let fixture: ComponentFixture<RepoBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RepoBadgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RepoBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
