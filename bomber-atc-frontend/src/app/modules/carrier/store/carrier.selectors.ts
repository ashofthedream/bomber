import { createSelector } from '@ngrx/store';
import { TestApp } from '../../app/models/test-app';
import { TestCase } from '../../app/models/test-case';
import { TestSuite } from '../../app/models/test-suite';
import { AppTreeNode } from '../../app/store/app.state';
import { AtcState } from '../../shared/store/atc.state';
import { Carrier } from '../models/carrier';

const carriersState = (state: AtcState) => state.carriers;

export const activeCarriers = createSelector(carriersState, state => state.active);
export const activeCarriersCount = createSelector(activeCarriers, carriers => carriers.length);
export const hasActiveCarriers = createSelector(activeCarriersCount, count => count > 0);

export const activeCarriersLoading = createSelector(carriersState, state => state.loadingActive);


export const isStartDisabled = createSelector(hasActiveCarriers, hasActive => !hasActive);
export const isStopDisabled = createSelector(hasActiveCarriers, hasActive => !hasActive);


export const applications = createSelector(activeCarriers, carriers => {
  return carriers
      .map(carrier => testAppNode(carrier))
      .reduce((a, b) => merge(a, b), []);
});



export function merge(left: AppTreeNode[], right: AppTreeNode[]): AppTreeNode[] {
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

    // console.log('key-l-r || exists-add', key, l, r, exists, add);


    // exists.carriers
    const carriers = new Set<string>();
    exists.carriers.forEach(carrier => carriers.add(carrier));
    if (add) {
      add.carriers.forEach(carrier => carriers.add(carrier));
    }

    // const carriers = [...exists.carriers, ...( add ? add.carriers : [] )];
    return {
      key: exists.key,
      title: `${exists.key} (${carriers.size})`,
      expanded: true,
      children: merge(exists.children, add ? add.children : []),
      carriers: Array.from(carriers.values())
    };
  });
}

export function testAppNode(carrier: Carrier): AppTreeNode[] {
  return carrier.apps.map(app => {
    return {
      key: app.name,
      title: app.name,
      expanded: true,
      children: testSuiteNodes(carrier, app),
      carriers: [carrier.id]
    };
  });
}


function testSuiteNodes(carrier: Carrier, app: TestApp): AppTreeNode[] {
  return app.testSuites.map(testSuite => testSuiteNode(carrier, testSuite));
}

function testSuiteNode(carrier: Carrier, testSuite: TestSuite): AppTreeNode {
  return {
    key: testSuite.name,
    title: testSuite.name,
    expanded: true,
    children: testCaseNodes(carrier, testSuite),
    carriers: [carrier.id]
  };
}

function testCaseNodes(carrier: Carrier, testSuite: TestSuite): AppTreeNode[] {
  return testSuite.testCases.map(testCase => testCaseNode(carrier, testCase));
}

function testCaseNode(carrier: Carrier, testCase: TestCase): AppTreeNode {
  return {
    key: testCase.name,
    title: testCase.name,
    children: [],
    isLeaf: true,
    carriers: [carrier.id]
  };
}