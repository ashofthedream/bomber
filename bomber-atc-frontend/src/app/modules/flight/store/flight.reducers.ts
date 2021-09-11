import { HistogramPoint } from '../models/flight-record';
import { FlightAction, FlightActions } from './flight.actions';
import { FlightState, initialFlightState } from './flight.state';

export const flightReducers = (state = initialFlightState, action: FlightActions): FlightState => {
  switch (action.type) {
    case FlightAction.GetActiveFlightSuccess:

      let onlyHistograms: HistogramPoint[] = [];
      if (action.flight) {
      //   console.log(action.flight.data);
        Object.values(action.flight.histogram)
            .forEach((byTime: Map<number, HistogramPoint[]>) => {

              Object.values(byTime)
                  .forEach(value => {
                    onlyHistograms.push(value);
                  });
            });
      }

      return {
        ...state,
        active: action.flight,
        histogram: onlyHistograms
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
