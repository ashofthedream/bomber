import {NgModule} from '@angular/core';
import {
  en_US,
  NgZorroAntdModule,
  NZ_I18N,
  NzBadgeModule,
  NzButtonModule,
  NzCardModule, NzGridModule, NzPageHeaderModule,
  NzTableModule, NzTreeModule
} from 'ng-zorro-antd';
import {CommonModule, registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {ActiveCarriersCardComponent} from "./components/active-carriers-card/active-carriers-card.component";
import {ActiveCarriersPageComponent} from "./components/active-carriers-page/active-carriers-page.component";
import {ApplicationLabelComponent} from "./components/application-label/application-label.component";
import {CarrierRoutingModule} from "./carrier-routing.module";
import {RouterModule} from "@angular/router";


registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveCarriersCardComponent,
    ActiveCarriersPageComponent,
    ApplicationLabelComponent
  ],
  imports: [
    CommonModule,
    RouterModule,

    NzBadgeModule,
    NzButtonModule,
    NzCardModule,
    NzGridModule,
    NzTableModule,
    NzTreeModule,
    NzPageHeaderModule,
    CarrierRoutingModule
  ],
  exports: [
    ActiveCarriersCardComponent
  ],
  providers: [{provide: NZ_I18N, useValue: en_US}],
  bootstrap: []
})
export class CarrierModule {
}
