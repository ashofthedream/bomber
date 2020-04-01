import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {MainMenuComponent} from "./main/components/main-menu/main-menu.component";
import {DashboardPageComponent} from "./main/components/dashboard-page/dashboard-page.component";
import {AccountSettingsPageComponent} from "./main/settings/components/account-settings-page/account-settings-page.component";
import {ActiveCarriersPageComponent} from "./main/carriers/components/active-carriers-page/active-carriers-page.component";
import {LibsModule} from "./libs/libs.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SettingsLabelComponent} from "./main/components/settings-label/settings-label.component";
import {AllFlightsPageComponent} from "./main/flight/components/all-flights-page/all-flights-page.component";
import {ActiveFlightPageComponent} from "./main/flight/components/active-flight-page/active-flight-page.component";
import {ActiveCarriersCardComponent} from "./main/carriers/components/active-carriers-card/active-carriers-card.component";
import {LatestFlightsCardComponent} from "./main/flight/components/latest-flights-card/latest-flights-card.component";


registerLocaleData(en);


@NgModule({
  declarations: [
    AccountSettingsPageComponent,
    ActiveCarriersCardComponent,
    ActiveCarriersPageComponent,
    ActiveFlightPageComponent,
    AllFlightsPageComponent,
    LatestFlightsCardComponent,
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
    ActiveCarriersPageComponent,
    ActiveFlightPageComponent,
    AllFlightsPageComponent,
    DashboardPageComponent,
    MainMenuComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class MainModule {
}
