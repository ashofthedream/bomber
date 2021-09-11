import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { ApplicationState } from '../../../app/models/application-state';
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
