import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {HeaderComponent} from "./layout/components/header/header.component";
import {MainMenuComponent} from "./layout/components/main-menu/main-menu.component";
import {FooterComponent} from "./layout/components/footer/footer.component";


registerLocaleData(en);


@NgModule({
  declarations: [
    FooterComponent,
    HeaderComponent,
    MainMenuComponent
  ],
  imports: [
    BrowserModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  exports: [
    FooterComponent,
    HeaderComponent,
    MainMenuComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class LayoutModule {
}
