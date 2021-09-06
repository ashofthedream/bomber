import { Flight } from '../models/flight';

export interface FlightState {
  active: Flight | null;
  all: Flight[];

  histogram: Point[];
}


export interface Point {
  testSuite: string;
  testCase: string;
  timestamp: number;
  label: string;
  values: number[];
}

export const initialFlightState: FlightState = {
  active: null,
  all: [],
  histogram: []
};
