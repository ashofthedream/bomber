import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardPageComponent} from "./modules/main/components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./modules/main/components/account-settings-page/account-settings-page.component";
import {ActiveInstancesPageComponent} from "./modules/main/components/active-instances-page/active-instances-page.component";
import {AllTestRunsPageComponent} from "./modules/main/components/all-test-runs-page/all-test-runs-page.component";
import {ActiveTestRunPageComponent} from "./modules/main/components/active-test-run-page/active-test-run-page.component";


const routes: Routes = [
  { path: '',                   redirectTo: 'dashboard', pathMatch: 'full'},
  { path: 'dashboard',          component: DashboardPageComponent, pathMatch: 'full'},
  { path: 'instances/active',   component: ActiveInstancesPageComponent, pathMatch: 'full'},
  { path: 'test-runs/active',   component: ActiveTestRunPageComponent, pathMatch: 'full'},
  { path: 'test-runs/all',      component: AllTestRunsPageComponent, pathMatch: 'full'},
  { path: 'settings/account',   component: AccountSettingsPageComponent, pathMatch: 'full'}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
