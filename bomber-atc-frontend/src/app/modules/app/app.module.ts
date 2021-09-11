import { CommonModule, registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import en from '@angular/common/locales/en';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
import { SharedModule } from '../shared/shared.module';
import { ActiveAppsTreeCardComponent } from './components/active-apps-tree/active-apps-tree-card.component';
import { AppLabelComponent } from './components/app-label/app-label.component';
import { SettingsLabelComponent } from './components/settings-label/settings-label.component';
import { StageBadgeStatusPipe } from './pipes/stage-badge-status.pipe';

registerLocaleData(en);


@NgModule({
  declarations: [
    ActiveAppsTreeCardComponent,
    AppLabelComponent,
    SettingsLabelComponent,
    StageBadgeStatusPipe
  ],
  imports: [
    CommonModule,

    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    HttpClientModule,

    NzBadgeModule,
    NzButtonModule,
    NzCardModule,
    NzGridModule,
    NzTableModule,
    NzTreeModule,
    NzPageHeaderModule,
    NzSpinModule,
    NzTreeViewModule,

    SharedModule,
  ],
  exports: [
    ActiveAppsTreeCardComponent,
    AppLabelComponent
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: []
})
export class AppModule {
}
