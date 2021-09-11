import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Logout } from '../../../auth/store/auth.actions';
import { activeCarriersCount } from '../../../carrier/store/carrier.selectors';
import { hasNoActiveFlight } from '../../../flight/store/flight.selectors';
import { AtcState } from '../../../shared/store/atc.state';

@Component({
  selector: 'atc-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent {

  activeCarriersCount = this.store.select(activeCarriersCount);
  hasNoActiveFlight = this.store.select(hasNoActiveFlight);

  constructor(private readonly store: Store<AtcState>) {
  }

  logout() {
    this.store.dispatch(new Logout());
  }
}
