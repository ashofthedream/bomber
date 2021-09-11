import { TestApp } from '../../app/models/test-app';


export interface TestFlight {
  id?: number;
  testApps: TestApp[];
}
