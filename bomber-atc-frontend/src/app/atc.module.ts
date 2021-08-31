import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { AtcRoutingModule } from './atc-routing.module';
import { AtcComponent } from './atc.component';
import { AuthEffects } from './modules/auth/store/auth.effects';
import { CarrierModule } from './modules/carrier/carrier.module';
import { CarrierEffects } from './modules/carrier/store/carrier.effects';
import { MainModule } from './modules/main/main.module';
import { AuthRequiredInterceptor } from './modules/shared/interceptors/auth-required.interceptor';
import { RequestLogHttpInterceptor } from './modules/shared/interceptors/request-log.interceptor';
import { SharedModule } from './modules/shared/shared.module';
import { atcReducers } from './modules/shared/store/atc.reducers';
import { UserEffects } from './modules/shared/store/user.effects';

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
    { provide: NZ_I18N, useValue: en_US },
    { provide: HTTP_INTERCEPTORS, useClass: RequestLogHttpInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthRequiredInterceptor, multi: true }
  ],
  bootstrap: [AtcComponent]
})
export class AtcModule {
}
