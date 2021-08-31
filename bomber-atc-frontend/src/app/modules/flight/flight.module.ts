import { NgModule } from '@angular/core';

import { CommonModule, registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzTableModule } from 'ng-zorro-antd/table';
import { FlightRoutingModule } from './flight-routing.module';
import { ActiveFlightPageComponent } from './components/active-flight-page/active-flight-page.component';
import { AllFlightsPageComponent } from './components/all-flights-page/all-flights-page.component';
import { LatestFlightsCardComponent } from './components/latest-flights-card/latest-flights-card.component';
import { RouterModule } from '@angular/router';
import { NzCardModule } from 'ng-zorro-antd/card';


registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveFlightPageComponent,
    AllFlightsPageComponent,
    LatestFlightsCardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,

    NzCardModule,
    NzEmptyModule,
    NzPageHeaderModule,
    NzTableModule,
    NzProgressModule,
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
