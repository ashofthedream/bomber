import { createSelector } from '@ngrx/store';
import { AtcState } from '../../shared/store/atc.state';

const carriersState = (state: AtcState) => state.carriers;

export const activeCarriers = createSelector(carriersState, state => state.active);

