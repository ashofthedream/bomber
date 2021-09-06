import { ActionReducerMap } from '@ngrx/store';
import { carrierReducers } from '../../carrier/store/carrier.reducers';
import { flightReducers } from '../../flight/store/flight.reducers';
import { AtcState } from './atc.state';
import { userReducers } from './user.reducers';

export const atcReducers: ActionReducerMap<AtcState, any> = {
  users: userReducers,
  carriers: carrierReducers,
  flights: flightReducers
};