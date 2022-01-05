import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {PlatformInvoicesService} from "../platform-invoices.service";
import {PlatformInvoice} from "../platformInvoice";
import {UserService} from "../user.service";
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-platform-invoices-page',
  templateUrl: './platform-invoices-page.component.html',
  styleUrls: ['./platform-invoices-page.component.css']
})
export class PlatformInvoicesPageComponent implements OnInit {

  platformInvoices?: PlatformInvoice[];
  filter = new FormControl('');

  constructor(
    private router: Router,
    private userService: UserService,
    private platformInvoicesService: PlatformInvoicesService
  ) {}

  ngOnInit(): void {
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user && user.role === 'admin') {
          this.loadPlatformInvoices();
        } else {
          this.router.navigateByUrl("/");
        }
      }
    )
  }

  loadPlatformInvoices(): void {
    this.platformInvoicesService.getPlatformInvoices().subscribe(
      platformInvoices => this.platformInvoices = platformInvoices
    );
  }

  getTotal() {
    let grossRevenue = 0;
    let netRevenue = 0;
    let totalVat = 0;
    if(this.platformInvoices) {
      this.platformInvoices.forEach(
        platformInvoice => {
          grossRevenue += platformInvoice.total;
          netRevenue += platformInvoice.commission;
          if(platformInvoice.vat >= 0) {
            totalVat += platformInvoice.vat;
          }
        }
      )
    }
    return {
      grossRevenue: grossRevenue,
      netRevenue: netRevenue,
      totalVat: totalVat
    }
  }
}
