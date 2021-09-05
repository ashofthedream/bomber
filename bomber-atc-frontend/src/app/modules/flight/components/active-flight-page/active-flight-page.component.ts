import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { ApplicationState } from '../../../main/models/application-state';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlight } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flights-active-page',
  templateUrl: './active-flight-page.component.html'
})
export class ActiveFlightPageComponent {
  flight = this.store.select(activeFlight);

  constructor(private readonly store: Store<AtcState>) {
  }

  iterationsProgress(state: ApplicationState): number {
    return Math.round(this.currentIterations(state) / state.settings.totalIterationsCount * 100);
  }

  timeProgress(state: ApplicationState): number {
    return Math.round(this.currentTime(state) / state.settings.duration * 100);
  }

  currentTime(state: ApplicationState): number {
    return state.settings.duration - state.remainTime;
  }

  currentIterations(state: ApplicationState): number {
    return state.settings.totalIterationsCount - state.remainTotalIterations;
  }
}
