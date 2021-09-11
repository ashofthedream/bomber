import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../auth/guards/auth.guard';
import { ActiveFlightPageComponent } from './components/active-flight-page/active-flight-page.component';
import { AllFlightsPageComponent } from './components/all-flights-page/all-flights-page.component';
import { CreateFlightPageComponent } from './components/create-flight-page/create-flight-page.component';


const routes: Routes = [
  {
    path: 'flights',
    canActivateChild: [AuthGuard],
    children: [
      { path: 'active', component: ActiveFlightPageComponent },
      { path: 'all', component: AllFlightsPageComponent },
      { path: 'create', component: CreateFlightPageComponent },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })]
})
export class FlightRoutingModule {
}
