import { Injectable } from '@angular/core';
import { Actions } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { AtcState } from '../../shared/store/atc.state';

@Injectable()
export class AppEffects {

  constructor(private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }
}
