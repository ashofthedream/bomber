import { CommonModule, registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzWaveModule } from 'ng-zorro-antd/core/wave';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NgxEchartsModule } from 'ngx-echarts';
import { AppModule } from '../app/app.module';
import { ActiveFlightPageComponent } from './components/active-flight-page/active-flight-page.component';
import { AllFlightsPageComponent } from './components/all-flights-page/all-flights-page.component';
import { CreateFlightPageComponent } from './components/create-flight-page/create-flight-page.component';
import { FlightGraphComponent } from './components/flight-graph/flight-graph.component';
import { FlightLogTableComponent } from './components/flight-log-table/flight-log-table.component';
import { LatestFlightsCardComponent } from './components/latest-flights-card/latest-flights-card.component';
import { FlightRoutingModule } from './flight-routing.module';


registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveFlightPageComponent,
    AllFlightsPageComponent,
    CreateFlightPageComponent,
    FlightGraphComponent,
    FlightLogTableComponent,
    LatestFlightsCardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,

    NzButtonModule,
    NzCardModule,
    NzEmptyModule,
    NzPageHeaderModule,
    NzTableModule,
    NzProgressModule,


    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),

    AppModule,

    FlightRoutingModule,
    NzGridModule,
    NzWaveModule,
    NzTagModule,
    NzCheckboxModule
  ],
  exports: [
    LatestFlightsCardComponent
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class FlightModule {
}
