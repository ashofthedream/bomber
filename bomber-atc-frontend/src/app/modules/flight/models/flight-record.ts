import {ApplicationState} from "../../main/models/application-state";

export class FlightRecord {
  timestamp: number;
  type: string;
  testSuite: string;
  testCase: string;
  state: ApplicationState;
}
