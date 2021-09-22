import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { activeCarriersLoading } from '../../../carrier/store/carrier.selectors';
import { AddToFlight } from '../../../flight/store/flight.actions';
import { AtcState } from '../../../shared/store/atc.state';
import { applications } from '../../store/app.selectors';
import { AppTreeNode, NodeType } from '../../store/app.state';


@Component({
  selector: 'atc-app-active',
  templateUrl: './active-apps-tree-card.component.html'
})
export class ActiveAppsTreeCardComponent {
  selected: AppTreeNode;

  activeCarriersLoading = this.store.select(activeCarriersLoading);
  applications = this.store.select(applications);

  @Input()
  editable = false;

  constructor(private readonly store: Store<AtcState>) {
  }

  select(node: AppTreeNode) {
    this.selected = this.selected?.key !== node.key ? node : null;
  }

  add(node: AppTreeNode) {
    if (node.type === NodeType.TEST_APP) {
      node.children.forEach(testSuite => this.add(testSuite));
      return;
    }

    if (node.type === NodeType.TEST_SUITE) {
      node.children.forEach(testCase => this.add(testCase));
      return;
    }

    this.store.dispatch(new AddToFlight(node));
  }
}
