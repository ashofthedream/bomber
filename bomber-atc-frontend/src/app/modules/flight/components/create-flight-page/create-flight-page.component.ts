import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { activeCarriers } from '../../../carrier/store/carrier.selectors';
import { AtcState } from '../../../shared/store/atc.state';
import { CancelCreatedFlight, StartCreatedFlight, ToggleCarrier } from '../../store/flight.actions';
import { createdFlightPlanApps, flightCanNotBeStarted } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-create',
  templateUrl: './create-flight-page.component.html'
})
export class CreateFlightPageComponent {

  testApps = this.store.select(createdFlightPlanApps);
  carriers = this.store.select(activeCarriers);
  flightCanNotBeStarted = this.store.select(flightCanNotBeStarted);

  constructor(private readonly store: Store<AtcState>) {
  }

  startFlight() {
    this.store.dispatch(new StartCreatedFlight());
  }

  cancelFlight() {
    this.store.dispatch(new CancelCreatedFlight());
  }

  toggleCarrier(carrier: string) {
    this.store.dispatch(new ToggleCarrier(carrier));
  }
}
