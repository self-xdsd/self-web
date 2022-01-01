import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthenticatedMenuComponent } from './authenticated-menu.component';

describe('AuthenticatedMenuComponent', () => {
  let component: AuthenticatedMenuComponent;
  let fixture: ComponentFixture<AuthenticatedMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AuthenticatedMenuComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthenticatedMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
