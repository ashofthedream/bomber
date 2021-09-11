import { AppState, initialAppState } from '../../app/store/app.state';
import { CarrierState, initialCarrierState } from '../../carrier/store/carrier.state';
import { FlightState, initialFlightState } from '../../flight/store/flight.state';
import { initialUserState, UserState } from './user.state';

export interface AtcState {
  app: AppState;
  users: UserState;
  carriers: CarrierState;
  flights: FlightState;
}

export const initialAtcState: AtcState = {
  app: initialAppState,
  users: initialUserState,
  carriers: initialCarrierState,
  flights: initialFlightState
};
