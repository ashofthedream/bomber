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
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTreeModule } from 'ng-zorro-antd/tree';
import { NzTreeViewModule } from 'ng-zorro-antd/tree-view';
import { CarrierRoutingModule } from './carrier-routing.module';
import { ActiveAppsTreeCardComponent } from './components/active-apps-tree/active-apps-tree-card.component';
import { ActiveCarriersListCardComponent } from './components/active-carriers-list-card/active-carriers-list-card.component';
import { ActiveCarriersPageComponent } from './components/active-carriers-page/active-carriers-page.component';
import { ActiveCarriersTableCardComponent } from './components/active-carriers-table-card/active-carriers-table-card.component';
import { AppLabelComponent } from './components/app-label/app-label.component';
import { CarrierLabelComponent } from './components/carrier-label/carrier-label.component';
import { StageBadgeStatusPipe } from './pipes/stage-badge-status.pipe';


registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveAppsTreeCardComponent,
    ActiveCarriersListCardComponent,
    ActiveCarriersTableCardComponent,
    ActiveCarriersPageComponent,
    AppLabelComponent,
    CarrierLabelComponent,
    StageBadgeStatusPipe
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
    CarrierRoutingModule,
    NzSpinModule,
    NzTreeViewModule
  ],
  exports: [
    ActiveCarriersListCardComponent
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class CarrierModule {
}
