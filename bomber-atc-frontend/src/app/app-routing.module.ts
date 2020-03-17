import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from "./modules/dashboard/components/dashboard/dashboard.component";


const routes: Routes = [
  { path: '',           redirectTo: 'dashboard', pathMatch: 'full'},
  { path: 'dashboard',  component: DashboardComponent, pathMatch: 'full'
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}