import { TestCase } from './test-case';

export interface TestSuite {
  name: string;
  testCases: TestCase[];
}
