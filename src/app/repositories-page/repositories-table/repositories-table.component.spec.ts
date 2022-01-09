import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoriesTableComponent } from './repositories-table.component';

describe('RepositoriesTableComponent', () => {
  let component: RepositoriesTableComponent;
  let fixture: ComponentFixture<RepositoriesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepositoriesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoriesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
