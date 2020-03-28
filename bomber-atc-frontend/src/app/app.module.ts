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
import {LibsModule} from "./modules/libs/libs.module";
import {RequestLogHttpInterceptor} from "./modules/libs/interceptors/request-log.interceptor";

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    LibsModule,
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
      {provide: HTTP_INTERCEPTORS, useClass: RequestLogHttpInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
