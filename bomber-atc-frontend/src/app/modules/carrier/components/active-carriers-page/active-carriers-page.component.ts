import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { StartAll, StopFlight } from '../../../flight/store/flight.actions';
import { AtcState } from '../../../shared/store/atc.state';
import { isStartDisabled, isStopDisabled } from '../../store/carrier.selectors';


@Component({
  selector: 'atc-carriers-active',
  templateUrl: './active-carriers-page.component.html'
})
export class ActiveCarriersPageComponent {
  isStartDisabled = this.store.select(isStartDisabled);
  isStopDisabled = this.store.select(isStopDisabled);

  constructor(private readonly store: Store<AtcState>) {
  }

  startAll() {
    this.store.dispatch(new StartAll());
  }

  stopAll() {
    this.store.dispatch(new StopFlight());
  }
}
