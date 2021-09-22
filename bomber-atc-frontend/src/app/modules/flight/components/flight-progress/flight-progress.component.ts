import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlightProgress } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-progress',
  templateUrl: './flight-progress.component.html'
})
export class FlightProgressComponent {
  progress = this.store.select(activeFlightProgress);

  constructor(private readonly store: Store<AtcState>) {
  }
}
