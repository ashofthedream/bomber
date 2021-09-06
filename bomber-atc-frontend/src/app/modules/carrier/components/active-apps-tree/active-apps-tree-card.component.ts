import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { FlightService } from '../../../flight/services/flight.service';
import { TestCase } from '../../../main/models/test-case';
import { TestSuite } from '../../../main/models/test-suite';
import { AtcState } from '../../../shared/store/atc.state';
import { Carrier } from '../../models/carrier';
import { activeCarriers, activeCarriersLoading } from '../../store/carrier.selectors';


@Component({
  selector: 'atc-applications-active-tree-card',
  templateUrl: './active-apps-tree-card.component.html'
})
export class ActiveAppsTreeCardComponent implements OnInit {
  tree: AppTreeNode[] = [];
  selected: AppTreeNode;

  activeCarriers = this.store.select(activeCarriers);
  activeCarriersLoading = this.store.select(activeCarriersLoading);

  constructor(private readonly store: Store<AtcState>,
              private readonly flightService: FlightService) {
  }

  ngOnInit(): void {
    this.activeCarriers
        .subscribe(carriers => {
          const nodes: AppTreeNode[] = carriers
              .map(carrier => this.testAppNode(carrier));
          if (this.tree.length > 0)
            return;

          this.tree = this.merge([], nodes);
        });

    // interval(3000)
    //     .subscribe(value => {
    //       this.showChecked(this.tree);
    //     });
  }

  select(node: AppTreeNode) {
    this.selected = node;
  }

  private showChecked(tree: AppTreeNode[]) {
    tree.filter(n => n.checked)
        .forEach(n => {
          console.log('checked: {}', n.key);
          this.showChecked(n.children);
        });
  }

  private merge(left: AppTreeNode[], right: AppTreeNode[]): AppTreeNode[] {
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

    console.log(keys, leftByKey, rightByKey);
    return Array.from(keys).map(key => {

      const l = leftByKey.get(key);
      const r = rightByKey.get(key);

      const exists = l != null ? l : r;
      const add = l == null ? l : r;

      console.log('key-l-r || exists-add', key, l, r, exists, add);

      const carriers = [...exists.carriers, ...( add ? add.carriers : [] )];
      return {
        key: exists.key,
        title: `${exists.key} (${carriers.length})`,
        expanded: true,
        children: this.merge(exists.children, add ? add.children : []),
        carriers
      };
    });
  }

  private testAppNode(carrier: Carrier): AppTreeNode {
    return {
      key: carrier.app.name,
      title: carrier.app.name,
      expanded: true,
      children: this.testSuiteNodes(carrier),
      carriers: [carrier]
    };
  }

  private testSuiteNode(carrier: Carrier, testSuite: TestSuite): AppTreeNode {
    return {
      key: testSuite.name,
      title: testSuite.name,
      expanded: true,
      children: this.testCaseNodes(carrier, testSuite),
      carriers: [carrier]
    };
  }

  private testCaseNodes(carrier: Carrier, testSuite: TestSuite): AppTreeNode[] {
    return testSuite.testCases.map(testCase => this.testCaseNode(carrier, testCase));
  }

  private testCaseNode(carrier: Carrier, testCase: TestCase): AppTreeNode {
    return {
      key: testCase.name,
      title: testCase.name,
      children: [],
      isLeaf: true,
      carriers: [carrier]
    };
  }

  private testSuiteNodes(carrier: Carrier): AppTreeNode[] {
    return carrier.app.testSuites.map(testSuite => this.testSuiteNode(carrier, testSuite));
  }
}

export interface AppTreeNode extends NzTreeNodeOptions {
  children: AppTreeNode[];
  carriers: Carrier[];
}
