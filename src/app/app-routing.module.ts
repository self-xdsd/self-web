import { NgModule } from '@angular/core';
import {provideRouter, RouterModule, Routes, withComponentInputBinding} from '@angular/router';
import {HomepageComponent} from "./homepage/homepage.component";
import {RepositoriesPageComponent} from "./repositories-page/repositories-page.component";
import {ContributorPageComponent} from "./contributor-page/contributor-page.component";
import {PlatformInvoicesPageComponent} from "./platform-invoices-page/platform-invoices-page.component";
import {ProjectManagersPageComponent} from "./project-managers-page/project-managers-page.component";
import {UserPageComponent} from "./user-page/user-page.component";
import {ProjectPageComponent} from "./project-page/project-page.component";

const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'user', component: UserPageComponent },
  { path: 'repositories', component: RepositoriesPageComponent },
  { path: 'contributor', component: ContributorPageComponent },
  { path: 'admin/pms', component: ProjectManagersPageComponent },
  { path: 'admin/invoices', component: PlatformInvoicesPageComponent },
  { path: 'github/:owner/:name', component: ProjectPageComponent },
  { path: 'gitlab/:owner/:name', component: ProjectPageComponent },
  { path: '**', redirectTo: '' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [provideRouter(
    routes,
    withComponentInputBinding()
  )]
})
export class AppRoutingModule { }
