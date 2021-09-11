import { ApplicationState } from '../../app/models/application-state';
import { HistogramPoint } from './flight-record';
import { TestFlight } from './test-flight';

export interface Flight {
  plan: TestFlight;
  events: SinkEvent[];
  progress: Map<string, SinkEvent>;
  histogram: Map<string, Map<number, HistogramPoint[]>>;
}

export interface SinkEvent {
  id: number;
  timestamp: number;
  type: string;
  carrierId: string;
  testApp: string;
  testSuite: string;
  testCase: string;
  stage: string;
  state: ApplicationState;
  histograms?: HistogramPoint[];
}
