import { ApplicationState } from './application-state';
import { TestSuite } from './test-suite';

export interface Application {
  name: string;
  state: ApplicationState;
  testSuites: TestSuite[];
}
