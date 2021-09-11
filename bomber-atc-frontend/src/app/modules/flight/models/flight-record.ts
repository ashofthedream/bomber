import { ApplicationState } from '../../app/models/application-state';

export interface FlightRecord {
  timestamp: number;
  type: string;
  testSuite: string;
  testCase: string;
  state: ApplicationState;
  histograms: Histogram[];
}

export interface Histogram {
  label: string;
  timestamp: number;
  totalCount: number;
  errorsCount: number;
  percentiles: number[];
}
