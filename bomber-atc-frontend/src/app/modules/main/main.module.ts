import { CommonModule, registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { AtcRoutingModule } from '../../atc-routing.module';
import { CarrierModule } from '../carrier/carrier.module';
import { FlightModule } from '../flight/flight.module';
import { SharedModule } from '../shared/shared.module';
import { DashboardPageComponent } from './components/dashboard-page/dashboard-page.component';
import { LoginPageComponent } from './components/login-page/login-page.component';
import { MainMenuComponent } from './components/main-menu/main-menu.component';
import { AccountSettingsPageComponent } from './settings/components/account-settings-page/account-settings-page.component';

registerLocaleData(en);


@NgModule({
  declarations: [
    AccountSettingsPageComponent,
    DashboardPageComponent,
    LoginPageComponent,
    MainMenuComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,

    NzButtonModule,
    NzCardModule,
    NzIconModule,
    NzGridModule,
    NzFormModule,
    NzPageHeaderModule,
    NzMenuModule,
    NzToolTipModule,
    NzInputModule,

    AtcRoutingModule,
    CarrierModule,
    FlightModule,

    SharedModule
  ],
  exports: [
    AccountSettingsPageComponent,
    DashboardPageComponent,
    MainMenuComponent
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class MainModule {
}
