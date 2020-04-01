import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardPageComponent} from "./modules/main/components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./modules/main/settings/components/account-settings-page/account-settings-page.component";
import {ActiveCarriersPageComponent} from "./modules/main/carriers/components/active-carriers-page/active-carriers-page.component";
import {AllFlightsPageComponent} from "./modules/main/flight/components/all-flights-page/all-flights-page.component";
import {ActiveFlightPageComponent} from "./modules/main/flight/components/active-flight-page/active-flight-page.component";


const routes: Routes = [
  { path: '',                   redirectTo: 'dashboard', pathMatch: 'full'},
  { path: 'dashboard',          component: DashboardPageComponent, pathMatch: 'full'},
  { path: 'carriers/active',    component: ActiveCarriersPageComponent, pathMatch: 'full'},
  { path: 'flights/active',     component: ActiveFlightPageComponent, pathMatch: 'full'},
  { path: 'flights/all',        component: AllFlightsPageComponent, pathMatch: 'full'},
  { path: 'settings/account',   component: AccountSettingsPageComponent, pathMatch: 'full'}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
