import { TestSuite } from './test-suite';

export interface TestApp {
  name: string;
  testSuites: TestSuite[];
}
