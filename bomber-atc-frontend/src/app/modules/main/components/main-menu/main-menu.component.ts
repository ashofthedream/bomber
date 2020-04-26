import { Component } from '@angular/core';
import { Store } from "@ngrx/store";
import { AtcState } from "../../../shared/store/state/atc.state";
import { Logout } from "../../../auth/store/actions/auth.actions";

@Component({
  selector: 'atc-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent {

  constructor(private readonly store: Store<AtcState>) {
  }

  logout() {
    this.store.dispatch(new Logout());
  }
}
