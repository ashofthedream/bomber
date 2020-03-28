import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {MainMenuComponent} from "./main/components/main-menu/main-menu.component";
import {DashboardPageComponent} from "./main/components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./main/components/account-settings-page/account-settings-page.component";
import {ActiveInstancesPageComponent} from "./main/components/active-instances-page/active-instances-page.component";
import {LibsModule} from "./libs/libs.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SettingsLabelComponent} from "./main/components/settings-label/settings-label.component";
import {AllTestRunsPageComponent} from "./main/components/all-test-runs-page/all-test-runs-page.component";
import {ActiveTestRunPageComponent} from "./main/components/active-test-run-page/active-test-run-page.component";


registerLocaleData(en);


@NgModule({
  declarations: [
    AccountSettingsPageComponent,
    ActiveInstancesPageComponent,
    ActiveTestRunPageComponent,
    AllTestRunsPageComponent,
    DashboardPageComponent,
    MainMenuComponent,
    SettingsLabelComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgZorroAntdModule,
    ReactiveFormsModule,

    LibsModule
  ],
  exports: [
    AccountSettingsPageComponent,
    ActiveInstancesPageComponent,
    ActiveTestRunPageComponent,
    AllTestRunsPageComponent,
    DashboardPageComponent,
    MainMenuComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class MainModule {
}
