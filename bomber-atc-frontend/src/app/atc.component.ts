import { Component, OnInit } from '@angular/core';
import { Store } from "@ngrx/store";
import { AtcState } from "./modules/shared/store/state/atc.state";
import { GetUser } from "./modules/shared/store/actions/user.actions";
import { isAuthenticated } from "./modules/auth/store/selectors/auth.selectors";


@Component({
  selector: 'atc',
  templateUrl: './atc.component.html',
  styleUrls: ['./atc.component.css']
})
export class AtcComponent implements OnInit {

  authenticated = this.store.select(isAuthenticated)

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit() {
    this.store.dispatch(new GetUser());
  }
}

