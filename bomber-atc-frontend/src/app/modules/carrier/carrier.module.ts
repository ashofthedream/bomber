import { CommonModule, registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NzBadgeModule } from 'ng-zorro-antd/badge';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTreeModule } from 'ng-zorro-antd/tree';
import { CarrierRoutingModule } from './carrier-routing.module';
import { ActiveCarriersCardComponent } from './components/active-carriers-card/active-carriers-card.component';
import { ActiveCarriersPageComponent } from './components/active-carriers-page/active-carriers-page.component';
import { ApplicationLabelComponent } from './components/application-label/application-label.component';


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
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class CarrierModule {
}
