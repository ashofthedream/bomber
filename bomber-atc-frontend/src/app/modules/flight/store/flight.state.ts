import { Flight } from '../models/flight';
import { HistogramPoint } from '../models/flight-record';
import { TestFlightPlan } from '../models/test-flight-plan';

export interface CreatePlanState {
  plan: TestFlightPlan;
}

export interface FlightState {
  active: Flight | null;
  all: Flight[];
  createPlan: CreatePlanState;
  histogram: HistogramPoint[];
}



export const initialFlightState: FlightState = {
  active: null,
  all: [],
  createPlan: {
    plan: {
      testApps: []
    }
  },
  histogram: []
};
