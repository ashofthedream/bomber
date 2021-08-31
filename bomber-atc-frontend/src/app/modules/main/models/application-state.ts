import {WorkerState} from './worker-state';
import {Settings} from './settings';

export interface ApplicationState {
  stage: Stage;
  settings: Settings;
  testSuite: string;
  testCase: string;
  testSuiteStart: number;
  testCaseStart: number;
  remainTotalIterations: number;
  elapsedTime: number;
  remainTime: number;
  errorsCount: number;
  workers: WorkerState[];
}


export enum Stage {
  Idle = 'Idle',
  WarmUp = 'WarmUp',
  Test = 'Test'
}
