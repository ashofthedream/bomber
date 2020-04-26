import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ActiveFlightPageComponent} from "./components/active-flight-page/active-flight-page.component";
import {AllFlightsPageComponent} from "./components/all-flights-page/all-flights-page.component";
import {AuthGuard} from "../auth/guards/auth.guard";


const routes: Routes = [
  { path: 'flights',
    canActivateChild: [AuthGuard],
    children: [
      { path: 'active',   component: ActiveFlightPageComponent},
      { path: 'all',      component: AllFlightsPageComponent},
    ]
  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)]
})
export class FlightRoutingModule {
}
