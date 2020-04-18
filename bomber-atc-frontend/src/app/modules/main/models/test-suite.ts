import {TestCase} from "./test-case";
import {Settings} from "./settings";

export class TestSuite {
  name: string;
  loadTest: Settings;
  warmUp: Settings;
  testCases: TestCase[];
}
