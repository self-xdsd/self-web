import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectManagersPageComponent } from './project-managers-page.component';

describe('ProjectManagersPageComponent', () => {
  let component: ProjectManagersPageComponent;
  let fixture: ComponentFixture<ProjectManagersPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectManagersPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectManagersPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
