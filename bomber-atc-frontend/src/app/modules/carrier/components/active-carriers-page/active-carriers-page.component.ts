import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { FlightService } from '../../../flight/services/flight.service';
import { AtcState } from '../../../shared/store/atc.state';
import { Carrier } from '../../models/carrier';
import { isStartDisabled, isStopDisabled } from '../../store/carrier.selectors';


@Component({
  selector: 'atc-carriers-active-page',
  templateUrl: './active-carriers-page.component.html'
})
export class ActiveCarriersPageComponent {
  isStartButtonDisabled = this.store.select(isStartDisabled);
  isStopButtonDisabled = this.store.select(isStopDisabled);

  constructor(private readonly store: Store<AtcState>,
              private readonly flightService: FlightService) {
  }

  startAll() {
    this.flightService.startAll()
        .subscribe();
  }

  start(carrier: Carrier) {
    this.flightService.start(carrier, carrier.app)
        .subscribe();
  }

  stopAll() {
    this.flightService.stopAll()
        .subscribe();
  }

  stop(carrier: Carrier) {
    this.flightService.stop(carrier, carrier.app)
        .subscribe();
  }
}
