import {NgModule} from '@angular/core';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {CommonModule, registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {FlightRoutingModule} from "./flight-routing.module";
import {ActiveFlightPageComponent} from "./components/active-flight-page/active-flight-page.component";
import {AllFlightsPageComponent} from "./components/all-flights-page/all-flights-page.component";
import {LatestFlightsCardComponent} from "./components/latest-flights-card/latest-flights-card.component";


registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveFlightPageComponent,
    AllFlightsPageComponent,
    LatestFlightsCardComponent
  ],
  imports: [
    CommonModule,
    NgZorroAntdModule,
    FlightRoutingModule
  ],
  exports: [
    LatestFlightsCardComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class FlightModule {
}
