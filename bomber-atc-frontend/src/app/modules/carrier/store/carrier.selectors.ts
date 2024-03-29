import { createSelector } from '@ngrx/store';
import { AtcState } from '../../shared/store/atc.state';

const carriersState = (state: AtcState) => state.carriers;

export const activeCarriers = createSelector(carriersState, state => state.active);
export const activeCarrierIds = createSelector(activeCarriers, carriers => carriers.map(carrier => carrier.id));
export const activeCarriersCount = createSelector(activeCarriers, carriers => carriers.length);
export const hasActiveCarriers = createSelector(activeCarriersCount, count => count > 0);

export const activeCarriersLoading = createSelector(carriersState, state => state.loadingActive);


export const isStartDisabled = createSelector(hasActiveCarriers, hasActive => !hasActive);
export const isStopDisabled = createSelector(hasActiveCarriers, hasActive => !hasActive);
