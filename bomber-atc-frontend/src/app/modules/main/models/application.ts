import {TestSuite} from "./test-suite";
import {ApplicationState} from "./application-state";

export class Application {
  name: string;
  state: ApplicationState;
  testSuites: TestSuite[];
}
