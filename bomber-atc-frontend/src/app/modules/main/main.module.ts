import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {
  en_US,
  NgZorroAntdModule,
  NZ_I18N,
  NzButtonModule, NzCardModule,
  NzFormModule,
  NzGridModule, NzIconModule, NzInputGroupComponent, NzInputModule, NzMentionModule, NzMenuModule,
  NzPageHeaderModule, NzToolTipModule
} from 'ng-zorro-antd';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CommonModule, registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {MainMenuComponent} from "./components/main-menu/main-menu.component";
import {DashboardPageComponent} from "./components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./settings/components/account-settings-page/account-settings-page.component";
import {SharedModule} from "../shared/shared.module";
import {AtcRoutingModule} from "../../atc-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SettingsLabelComponent} from "./components/settings-label/settings-label.component";
import {CarrierModule} from "../carrier/carrier.module";
import {FlightModule} from "../flight/flight.module";
import {LoginPageComponent} from "./components/login-page/login-page.component";


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

    NzButtonModule,
    NzIconModule,
    NzGridModule,
    NzFormModule,
    NzPageHeaderModule,
    NzMenuModule,
    NzToolTipModule,
    ReactiveFormsModule,
    NzInputModule,
    CarrierModule,
    FlightModule,
    AtcRoutingModule,
    SharedModule,
    NzCardModule
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
