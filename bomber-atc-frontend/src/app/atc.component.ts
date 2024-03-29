import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { webSocket } from 'rxjs/webSocket';
import { isAuthenticated } from './modules/auth/store/auth.selectors';
import { AtcState } from './modules/shared/store/atc.state';
import { GetUser } from './modules/shared/store/user.actions';


@Component({
  selector: 'atc',
  templateUrl: './atc.component.html',
  styleUrls: ['./atc.component.css']
})
export class AtcComponent implements OnInit {

  authenticated = this.store.select(isAuthenticated);

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit() {
    this.store.dispatch(new GetUser());
  }
}

