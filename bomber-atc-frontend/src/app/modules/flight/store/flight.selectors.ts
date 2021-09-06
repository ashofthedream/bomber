import { createSelector } from '@ngrx/store';
import { AtcState } from '../../shared/store/atc.state';

const flightsState = (state: AtcState) => state.flights;

export const activeFlight = createSelector(flightsState, state => state.active);


export const activeFlightHistogram = createSelector(flightsState, state => state.histogram);