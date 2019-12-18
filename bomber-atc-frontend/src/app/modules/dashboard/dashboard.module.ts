import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {AppRoutingModule} from '../../app-routing.module';

registerLocaleData(en);


@NgModule({
  declarations: [
    DashboardComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  exports: [
    DashboardComponent,
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class DashboardModule {
}
