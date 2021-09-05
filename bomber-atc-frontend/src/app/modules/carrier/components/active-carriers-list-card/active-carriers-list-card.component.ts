import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { activeCarriers } from '../../store/carrier.selectors';

@Component({
  selector: 'atc-carriers-active-list-card',
  templateUrl: './active-carriers-list-card.component.html'
})
export class ActiveCarriersListCardComponent {

  carriers = this.store.select(activeCarriers);

  constructor(private readonly store: Store<AtcState>) {
  }
}
