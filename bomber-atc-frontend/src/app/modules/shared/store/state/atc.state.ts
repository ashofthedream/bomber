import { initialUserState, UserState } from "./user.state";
import { CarrierState, initialCarrierState } from "../../../carrier/store/state/carrier.state";

export interface AtcState {
  users: UserState;
  carriers: CarrierState
}

export const initialAtcState: AtcState = {
  users: initialUserState,
  carriers: initialCarrierState
}