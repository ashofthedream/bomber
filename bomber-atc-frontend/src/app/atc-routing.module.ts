import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './modules/auth/guards/auth.guard';
import { DashboardPageComponent } from './modules/main/components/dashboard-page/dashboard-page.component';
import { LoginPageComponent } from './modules/main/components/login-page/login-page.component';
import { AccountSettingsPageComponent } from './modules/main/settings/components/account-settings-page/account-settings-page.component';


const routes: Routes = [
  {path: 'login',             component: LoginPageComponent, pathMatch: 'full'},
  {path: 'dashboard',         component: DashboardPageComponent, pathMatch: 'full', canActivate: [AuthGuard]},
  {path: 'settings/account',  component: AccountSettingsPageComponent, canActivate: [AuthGuard]},
  {path: '**',                redirectTo: 'dashboard'},
];


@NgModule({
    imports: [RouterModule.forRoot(routes, {
        enableTracing: false,
        relativeLinkResolution: 'legacy'
    })],
    exports: [RouterModule]
})
export class AtcRoutingModule {
}
