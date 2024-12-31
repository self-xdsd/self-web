import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HeaderComponent } from './header/header.component';
import { LoginsComponent } from './logins/logins.component';
import { FooterComponent } from './footer/footer.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthenticatedMenuComponent } from './authenticated-menu/authenticated-menu.component';
import { HomepageComponent } from './homepage/homepage.component';
import { RepositoriesPageComponent } from './repositories-page/repositories-page.component';
import { ContributorPageComponent } from './contributor-page/contributor-page.component';
import { PlatformInvoicesPageComponent } from './platform-invoices-page/platform-invoices-page.component';
import { ProjectManagersPageComponent } from './project-managers-page/project-managers-page.component';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { RepositoriesTableComponent } from './repositories-page/repositories-table/repositories-table.component';
import { UserPageComponent } from './user-page/user-page.component';
import {PayoutMethodComponent} from "./contributor-page/payout-method/payout-method.component";
import {StripePayoutMethodComponent} from "./contributor-page/payout-method/stripe-payout-method/stripe-payout-method.component";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginsComponent,
    FooterComponent,
    AuthenticatedMenuComponent,
    HomepageComponent,
    RepositoriesPageComponent,
    ContributorPageComponent,
    PlatformInvoicesPageComponent,
    ProjectManagersPageComponent,
    RepositoriesTableComponent,
    UserPageComponent,
    PayoutMethodComponent,
    StripePayoutMethodComponent
  ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        NgbModule,
        FormsModule,
        ReactiveFormsModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
export class AppModule { }
