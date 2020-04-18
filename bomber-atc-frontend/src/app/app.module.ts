import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {MainModule} from "./modules/main.module";
import {SharedModule} from "./modules/shared/shared.module";
import {RequestLogHttpInterceptor} from "./modules/shared/interceptors/request-log.interceptor";
import {CarrierModule} from "./modules/carrier/carrier.module";
import {AuthRequiredInterceptor} from "./modules/shared/interceptors/auth-required.interceptor";

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    CarrierModule,
    SharedModule,
    MainModule,
    BrowserModule,
    AppRoutingModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers: [
      {provide: NZ_I18N, useValue: en_US},
      {provide: HTTP_INTERCEPTORS, useClass: RequestLogHttpInterceptor, multi: true},
      {provide: HTTP_INTERCEPTORS, useClass: AuthRequiredInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
