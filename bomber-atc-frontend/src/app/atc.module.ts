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
import { AppModule } from './modules/app/app.module';
import { AppEffects } from './modules/app/store/app.effects';
import { AuthModule } from './modules/auth/auth.module';
import { AuthEffects } from './modules/auth/store/auth.effects';
import { CarrierModule } from './modules/carrier/carrier.module';
import { CarrierEffects } from './modules/carrier/store/carrier.effects';
import { FlightModule } from './modules/flight/flight.module';
import { FlightEffects } from './modules/flight/store/flight.effects';
import { MainModule } from './modules/main/main.module';
import { AuthRequiredInterceptor } from './modules/shared/interceptors/auth-required.interceptor';
import { RequestLogHttpInterceptor } from './modules/shared/interceptors/request-log.interceptor';
import { SharedModule } from './modules/shared/shared.module';
import { atcReducers } from './modules/shared/store/atc.reducers';
import { TraceEffects } from './modules/shared/store/trace.effects';
import { UserEffects } from './modules/shared/store/user.effects';

registerLocaleData(en);

@NgModule({
  declarations: [
    AtcComponent
  ],
  imports: [
    BrowserModule,

    NzLayoutModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,

    AppModule,
    CarrierModule,
    AuthModule,
    FlightModule,
    SharedModule,
    MainModule,
    AtcRoutingModule,

    StoreModule.forRoot(atcReducers),
    EffectsModule.forRoot([
        TraceEffects,
        AppEffects,
        AuthEffects,
        CarrierEffects,
        FlightEffects,
        UserEffects
    ]),
    StoreDevtoolsModule.instrument({
      maxAge: 100
    })
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
