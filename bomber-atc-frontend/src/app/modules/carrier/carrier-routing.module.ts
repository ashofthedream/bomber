import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ActiveCarriersPageComponent} from './components/active-carriers-page/active-carriers-page.component';
import {AuthGuard} from '../auth/guards/auth.guard';


const routes: Routes = [
  { path: 'carriers',
    canActivateChild: [AuthGuard],
    children: [
      {path: 'active', component: ActiveCarriersPageComponent}
    ]
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
})
export class CarrierRoutingModule {
}
