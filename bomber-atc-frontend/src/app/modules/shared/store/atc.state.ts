import { CarrierState, initialCarrierState } from '../../carrier/store/carrier.state';
import { initialUserState, UserState } from './user.state';

export interface AtcState {
  users: UserState;
  carriers: CarrierState;
}

export const initialAtcState: AtcState = {
  users: initialUserState,
  carriers: initialCarrierState
};
