import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributorPageComponent } from './contributor-page.component';

describe('ContributorPageComponent', () => {
  let component: ContributorPageComponent;
  let fixture: ComponentFixture<ContributorPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContributorPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributorPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
