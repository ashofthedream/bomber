import { ActionReducerMap } from '@ngrx/store';
import { appReducers } from '../../app/store/app.reducers';
import { carrierReducers } from '../../carrier/store/carrier.reducers';
import { flightReducers } from '../../flight/store/flight.reducers';
import { AtcState } from './atc.state';
import { userReducers } from './user.reducers';

export const atcReducers: ActionReducerMap<AtcState, any> = {
  app: appReducers,
  users: userReducers,
  carriers: carrierReducers,
  flights: flightReducers
};
