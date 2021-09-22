import { Flight, FlightProgress, HistogramPoint } from '../models/flight';
import { TestFlightPlan } from '../models/test-flight-plan';

export interface CreatePlanState {
  carriers: string[];
  plan: TestFlightPlan;
}

export interface FlightState {
  active: Flight | null;
  all: Flight[];
  createPlan: CreatePlanState;
  histogram: HistogramPoint[];
  progress: FlightProgress;
}



export const initialFlightState: FlightState = {
  active: null,
  all: [],
  createPlan: {
    carriers: [],
    plan: {
      testApps: []
    }
  },
  histogram: [],
  progress: null
};
