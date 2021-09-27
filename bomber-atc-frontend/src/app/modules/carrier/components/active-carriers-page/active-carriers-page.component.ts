import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { StartDefaultFlight, StopFlight } from '../../../flight/store/flight.actions';
import { AtcState } from '../../../shared/store/atc.state';
import { GetActiveCarriers } from '../../store/carrier.actions';
import { isStartDisabled, isStopDisabled } from '../../store/carrier.selectors';


@Component({
  selector: 'atc-carrier-active',
  templateUrl: './active-carriers-page.component.html'
})
export class ActiveCarriersPageComponent implements OnInit {
  isStartDisabled = this.store.select(isStartDisabled);
  isStopDisabled = this.store.select(isStopDisabled);

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit(): void {
    this.store.dispatch(new GetActiveCarriers());
  }

  startAll() {
    this.store.dispatch(new StartDefaultFlight());
  }

  stopAll() {
    this.store.dispatch(new StopFlight());
  }
}
