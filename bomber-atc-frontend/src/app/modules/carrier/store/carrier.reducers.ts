import { CarrierAction, CarrierActions } from './carrier.actions';
import { CarrierState, initialCarrierState } from './carrier.state';

export const carrierReducers = (state = initialCarrierState, action: CarrierActions): CarrierState => {
  switch (action.type) {
    case CarrierAction.GetActiveCarriers:
      return {
        ...state,
        activeLoading: true
      };

    case CarrierAction.GetActiveCarriersSuccess:
      return {
        ...state,
        active: action.carries,
        activeLoading: false
      };

    default:
      return state;
  }
};
