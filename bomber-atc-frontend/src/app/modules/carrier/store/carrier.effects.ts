import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { CarrierService } from '../services/carrier.service';
import { AtcState } from '../../shared/store/atc.state';
import { Store } from '@ngrx/store';
import { CarrierAction, GetActiveCarriers, GetActiveCarriersSuccess } from './carrier.actions';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { Observable, timer } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { isAuthenticated } from '../../auth/store/auth.selectors';

@Injectable()
export class CarrierEffects {

  constructor(private readonly carrierService: CarrierService,
              private readonly authService: AuthService,
              private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }

  @Effect()
  public getActiveCarriersTimer(): Observable<GetActiveCarriers> {
    return timer(0, 3000)
        .pipe(
            switchMap(n => this.store.select(isAuthenticated)),
            filter(auth => auth),
            map(n => new GetActiveCarriers())
        );
  }

  @Effect()
  public getActiveCarriers(): Observable<GetActiveCarriersSuccess> {
    return this.actions
        .pipe(
            tap(action => console.log(action)),
            ofType(CarrierAction.GetActiveCarriers),
            switchMap(() => this.carrierService.getActiveCarriers()),
            map(carriers => new GetActiveCarriersSuccess(carriers))
        );
  }
}
