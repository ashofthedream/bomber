import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlight, activeFlightHistogram } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-active',
  templateUrl: './active-flight-page.component.html'
})
export class ActiveFlightPageComponent {
  flight = this.store.select(activeFlight);
  histogram = this.store.select(activeFlightHistogram);

  constructor(private readonly store: Store<AtcState>) {
  }
}
