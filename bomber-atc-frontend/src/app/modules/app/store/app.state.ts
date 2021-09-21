import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';

export interface AppState {

}


export enum NodeType {
  TEST_APP = 'TEST_APP',
  TEST_SUITE = 'TEST_SUITE',
  TEST_CASE = 'TEST_CASE'
}

export interface AppTreeNode extends NzTreeNodeOptions {
  type: NodeType;
  testApp: string;
  testSuite: string;
  testCase: string;
  children: AppTreeNode[];
  carriers: string[];
}

export const initialAppState: AppState = {

};
