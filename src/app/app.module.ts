import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HeaderComponent } from './header/header.component';
import { LoginsComponent } from './logins/logins.component';
import { FooterComponent } from './footer/footer.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { AuthenticatedMenuComponent } from './authenticated-menu/authenticated-menu.component';
import { HomepageComponent } from './homepage/homepage.component';
import { RepositoriesPageComponent } from './repositories-page/repositories-page.component';
import { ContributorPageComponent } from './contributor-page/contributor-page.component';
import { PlatformInvoicesPageComponent } from './platform-invoices-page/platform-invoices-page.component';
import { ProjectManagersPageComponent } from './project-managers-page/project-managers-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RepositoriesTableComponent } from './repositories-page/repositories-table/repositories-table.component';

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
    RepositoriesTableComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    NgbModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
