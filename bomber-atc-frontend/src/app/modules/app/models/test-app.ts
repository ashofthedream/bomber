import { ApplicationState } from './application-state';
import { TestSuite } from './test-suite';

export interface TestApp {
  name: string;
  state?: ApplicationState;
  testSuites: TestSuite[];
}
