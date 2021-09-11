import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { Carrier } from '../../models/carrier';
import { activeCarriers, activeCarriersLoading, hasActiveCarriers } from '../../store/carrier.selectors';

@Component({
  selector: 'atc-carrier-active-table-card',
  templateUrl: './active-carriers-table-card.component.html'
})
export class ActiveCarriersTableCardComponent {

  activeCarriers = this.store.select(activeCarriers);
  hasActiveCarriers = this.store.select(hasActiveCarriers);
  activeCarriersLoading = this.store.select(activeCarriersLoading);

  constructor(private readonly store: Store<AtcState>) {
  }

  check(carrier: Carrier, event: boolean) {

  }
}
