import { TestBed } from '@angular/core/testing';

import { ProjectManagersService } from './project-managers.service';

describe('ProjectManagersService', () => {
  let service: ProjectManagersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectManagersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
