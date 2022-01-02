import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
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
    private location: Location,
    private userService: UserService,
    private platformInvoicesService: PlatformInvoicesService
  ) {}

  ngOnInit(): void {
    this.userService.getAuthenticatedUser().subscribe(
      user => {
        if(user && user.role === 'admin') {
          this.loadPlatformInvoices();
        } else {
          this.location.go("/");
        }
      }
    )
    this.loadPlatformInvoices();
  }

  loadPlatformInvoices(): void {
    this.platformInvoicesService.getPlatformInvoices().subscribe(
      platformInvoices => this.platformInvoices = platformInvoices
    );
  }
}
