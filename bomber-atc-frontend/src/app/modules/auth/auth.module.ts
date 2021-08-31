import { registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzNotificationModule } from 'ng-zorro-antd/notification';

registerLocaleData(en);


@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    NzNotificationModule,
    HttpClientModule,
  ],
  exports: [],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class AuthModule {
}
