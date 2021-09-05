import { Flight } from '../models/flight';

export interface FlightState {
  active: Flight | null;
  all: Flight[];
}


export const initialFlightState: FlightState = {
  active: null,
  all: []
};
