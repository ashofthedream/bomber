import { Flight } from '../models/flight';
import { HistogramPoint } from '../models/flight-record';

export interface FlightState {
  active: Flight | null;
  all: Flight[];

  histogram: HistogramPoint[];
}

export const initialFlightState: FlightState = {
  active: null,
  all: [],
  histogram: []
};
