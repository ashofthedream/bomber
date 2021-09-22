import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlightLog } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-log-table',
  templateUrl: './flight-log-table.component.html'
})
export class FlightLogTableComponent {
  events = this.store.select(activeFlightLog);

  constructor(private readonly store: Store<AtcState>) {
  }
}
