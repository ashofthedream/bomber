import { Settings } from './settings';
import { TestCase } from './test-case';

export interface TestSuite {
  name: string;
  loadTest: Settings;
  warmUp: Settings;
  testCases: TestCase[];
}
