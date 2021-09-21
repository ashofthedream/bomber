import { createSelector } from '@ngrx/store';
import { Carrier } from '../../carrier/models/carrier';
import { activeCarriers } from '../../carrier/store/carrier.selectors';
import { AtcState } from '../../shared/store/atc.state';
import { TestApp } from '../models/test-app';
import { TestCase } from '../models/test-case';
import { TestSuite } from '../models/test-suite';
import { AppTreeNode, NodeType } from './app.state';

const appState = (state: AtcState) => state.app;


export const applications = createSelector(activeCarriers, carriers => {
  return carriers
      .map(carrier => testAppNode(carrier))
      .reduce((a, b) => merge(a, b), []);
});



export function merge(left: AppTreeNode[], right: AppTreeNode[]): AppTreeNode[] {
  console.log('MERGE left right', left, right);
  const keys = new Set<string>();

  const leftByKey = new Map<string, AppTreeNode>();
  left.forEach(node => {
    leftByKey.set(node.key, node);
    keys.add(node.key);
  });

  const rightByKey = new Map<string, AppTreeNode>();
  right.forEach(node => {
    rightByKey.set(node.key, node);
    keys.add(node.key);
  });

  // console.log(keys, leftByKey, rightByKey);
  return Array.from(keys).map(key => {
    const l = leftByKey.get(key);
    const r = rightByKey.get(key);

    const exists = l != null ? l : r;
    const add = l == null ? l : r;

    // console.log("EXISTS VS ADD", exists, add);
    // console.log('key-l-r || exists-add', key, l, r, exists, add);


    // exists.carriers
    const carriers = new Set<string>();
    exists.carriers.forEach(carrier => carriers.add(carrier));
    if (add) {
      add.carriers.forEach(carrier => carriers.add(carrier));
    }

    // const carriers = [...exists.carriers, ...( add ? add.carriers : [] )];
    return {
      ...exists,
      children: merge(exists.children, add ? add.children : []),
      carriers: Array.from(carriers.values())
    };
  });
}

export function testAppNode(carrier: Carrier): AppTreeNode[] {
  return carrier.apps.map(testApp => {
    return {
      key: testApp.name,
      title: testApp.name,
      testApp: testApp.name,
      testSuite: null,
      testCase: null,
      expanded: true,
      children: testSuiteNodes(carrier, testApp),
      type: NodeType.TEST_APP,
      carriers: [carrier.id]
    };
  });
}


function testSuiteNodes(carrier: Carrier, testApp: TestApp): AppTreeNode[] {
  return testApp.testSuites.map(testSuite => testSuiteNode(carrier, testApp, testSuite));
}

function testSuiteNode(carrier: Carrier, testApp: TestApp, testSuite: TestSuite): AppTreeNode {
  return {
    key: testSuite.name,
    title: testSuite.name,
    testApp: testApp.name,
    testSuite: testSuite.name,
    testCase: null,
    expanded: true,
    children: testCaseNodes(carrier, testApp, testSuite),
    type: NodeType.TEST_SUITE,
    carriers: [carrier.id]
  };
}

function testCaseNodes(carrier: Carrier, testApp: TestApp, testSuite: TestSuite): AppTreeNode[] {
  return testSuite.testCases.map(testCase => testCaseNode(carrier, testApp, testSuite, testCase));
}

function testCaseNode(carrier: Carrier, testApp: TestApp, testSuite: TestSuite, testCase: TestCase): AppTreeNode {
  return {
    key: testCase.name,
    title: testCase.name,
    testApp: testApp.name,
    testSuite: testSuite.name,
    testCase: testCase.name,
    children: [],
    isLeaf: true,
    type: NodeType.TEST_CASE,
    carriers: [carrier.id]
  };
}