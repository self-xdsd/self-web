import { TestBed } from '@angular/core/testing';

import { PlatformInvoicesService } from './platform-invoices.service';

describe('PlatformInvoicesService', () => {
  let service: PlatformInvoicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlatformInvoicesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
