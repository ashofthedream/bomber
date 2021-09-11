import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { activeCarriersLoading, applications } from '../../../carrier/store/carrier.selectors';
import { AtcState } from '../../../shared/store/atc.state';
import { AppTreeNode } from '../../store/app.state';


@Component({
  selector: 'atc-app-active',
  templateUrl: './active-apps-tree-card.component.html'
})
export class ActiveAppsTreeCardComponent implements OnInit {
  apps: AppTreeNode[] = [];
  selected: AppTreeNode;

  activeCarriersLoading = this.store.select(activeCarriersLoading);
  applications = this.store.select(applications);

  @Input()
  editable = false;

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit(): void {
    this.applications.subscribe(apps => this.apps = apps);
  }

  select(node: AppTreeNode) {
    console.log(node);
    this.selected = this.selected?.key !== node.key ? node : null;
  }

  private showChecked(tree: AppTreeNode[]) {
    tree.filter(n => n.checked)
        .forEach(n => {
          console.log('checked: {}', n.key);
          this.showChecked(n.children);
        });
  }
}
