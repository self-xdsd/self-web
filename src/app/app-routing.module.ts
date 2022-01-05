import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomepageComponent} from "./homepage/homepage.component";
import {RepositoriesPageComponent} from "./repositories-page/repositories-page.component";
import {ContributorPageComponent} from "./contributor-page/contributor-page.component";
import {PlatformInvoicesPageComponent} from "./platform-invoices-page/platform-invoices-page.component";
import {ProjectManagersPageComponent} from "./project-managers-page/project-managers-page.component";

const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'repositories', component: RepositoriesPageComponent },
  { path: 'contributor', component: ContributorPageComponent },
  { path: 'admin/pms', component: ProjectManagersPageComponent },
  { path: 'admin/invoices', component: PlatformInvoicesPageComponent },
  { path: '**', redirectTo: '' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
