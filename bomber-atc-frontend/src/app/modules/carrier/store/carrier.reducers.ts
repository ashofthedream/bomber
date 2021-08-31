import { CarrierState, initialCarrierState } from './carrier.state';
import { CarrierAction, CarrierActions } from './carrier.actions';

export const carrierReducers = (state = initialCarrierState, action: CarrierActions): CarrierState => {
  switch (action.type) {
    case CarrierAction.GetActiveCarriersSuccess:
      return {
        ...state, active: action.carries
      };

    default:
      return state;
  }
};
