import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { currentTestApps } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-create',
  templateUrl: './create-flight-page.component.html'
})
export class CreateFlightPageComponent {

  testApps = this.store.select(currentTestApps);

  constructor(private readonly store: Store<AtcState>) {
  }

}
