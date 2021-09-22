import { TestFlight } from './test-flight';

export type HistogramPointByTime = {
  [key: number]: HistogramPoint[];
};

export type HistogramByCarrier = {
  [key: string]: FlightProgress;
};

export type ProgressByCarrier = {
  [key: string]: FlightProgress;
};

export interface Flight {
  plan: TestFlight;
  events: SinkEvent[];
  progress: ProgressByCarrier;
  histogram: HistogramPointByTime;
}

export interface SinkEvent {
  id: number;
  timestamp: number;
  type: string;
  carrierId: string;
  testApp: string;
  testSuite: string;
  testCase: string;
}

export interface FlightProgress {
  testApp: string;
  testSuite: string;
  testCase: string;
  timeElapsed: number;
  timeTotal: number;
  currentIterationsCount: number;
  totalIterationsCount: number;
  errorsCount: number;
}

export interface HistogramPoint {
  label: string;
  timestamp: number;
  totalCount: number;
  errorsCount: number;
  percentiles: number[];
}
