import { FlightAction, FlightActions } from './flight.actions';
import { FlightState, initialFlightState } from './flight.state';

export const flightReducers = (state = initialFlightState, action: FlightActions): FlightState => {
  switch (action.type) {
    case FlightAction.GetActiveFlightSuccess:
      return {
        ...state,
        active: action.flight
      };

    case FlightAction.GetAllFlightsSuccess:
      return {
        ...state,
        all: action.flights
      };

    default:
      return state;
  }
};
