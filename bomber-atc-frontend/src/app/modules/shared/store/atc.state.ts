import { CarrierState, initialCarrierState } from '../../carrier/store/carrier.state';
import { FlightState, initialFlightState } from '../../flight/store/flight.state';
import { initialUserState, UserState } from './user.state';

export interface AtcState {
  users: UserState;
  carriers: CarrierState;
  flights: FlightState;
}

export const initialAtcState: AtcState = {
  users: initialUserState,
  carriers: initialCarrierState,
  flights: initialFlightState
};
