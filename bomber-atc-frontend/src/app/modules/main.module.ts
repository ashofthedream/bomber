import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CommonModule, registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {MainMenuComponent} from "./main/components/main-menu/main-menu.component";
import {DashboardPageComponent} from "./main/components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./main/settings/components/account-settings-page/account-settings-page.component";
import {SharedModule} from "./shared/shared.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SettingsLabelComponent} from "./main/components/settings-label/settings-label.component";
import {CarrierModule} from "./carrier/carrier.module";
import {FlightModule} from "./flight/flight.module";
import {LoginPageComponent} from "./main/components/login-page/login-page.component";


registerLocaleData(en);


@NgModule({
  declarations: [
    AccountSettingsPageComponent,
    DashboardPageComponent,
    LoginPageComponent,
    MainMenuComponent,
    SettingsLabelComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgZorroAntdModule,
    ReactiveFormsModule,

    CarrierModule,
    FlightModule,
    AppRoutingModule,
    SharedModule
  ],
  exports: [
    AccountSettingsPageComponent,
    DashboardPageComponent,
    MainMenuComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class MainModule {
}
