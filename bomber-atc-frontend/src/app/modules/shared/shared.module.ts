import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';

registerLocaleData(en);


@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
  ],
  exports: [],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class SharedModule {
}
