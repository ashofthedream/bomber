import { ApplicationState } from '../../main/models/application-state';

export interface FlightRecord {
  timestamp: number;
  type: string;
  testSuite: string;
  testCase: string;
  state: ApplicationState;
}
