import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AtcRoutingModule } from './atc-routing.module';
import { AtcComponent } from './atc.component';
import { en_US, NZ_I18N, NzLayoutModule } from 'ng-zorro-antd';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { MainModule } from "./modules/main/main.module";
import { SharedModule } from "./modules/shared/shared.module";
import { RequestLogHttpInterceptor } from "./modules/shared/interceptors/request-log.interceptor";
import { CarrierModule } from "./modules/carrier/carrier.module";
import { AuthRequiredInterceptor } from "./modules/shared/interceptors/auth-required.interceptor";
import { StoreModule } from "@ngrx/store";
import { atcReducers } from "./modules/shared/store/reducers/atc.reducers";
import { EffectsModule } from "@ngrx/effects";
import { UserEffects } from "./modules/shared/store/effects/user.effects";
import { StoreDevtoolsModule } from "@ngrx/store-devtools";
import { CarrierEffects } from "./modules/carrier/store/effects/carrier.effects";
import { AuthEffects } from "./modules/auth/store/effects/auth.effects";

registerLocaleData(en);

@NgModule({
  declarations: [
    AtcComponent
  ],
  imports: [
    CarrierModule,
    SharedModule,
    MainModule,
    BrowserModule,
    AtcRoutingModule,
    NzLayoutModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    StoreModule.forRoot(atcReducers),
    EffectsModule.forRoot([UserEffects, CarrierEffects, AuthEffects]),
    StoreDevtoolsModule.instrument()
  ],
  providers: [
    {provide: NZ_I18N, useValue: en_US},
    {provide: HTTP_INTERCEPTORS, useClass: RequestLogHttpInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AuthRequiredInterceptor, multi: true}
  ],
  bootstrap: [AtcComponent]
})
export class AtcModule {
}
