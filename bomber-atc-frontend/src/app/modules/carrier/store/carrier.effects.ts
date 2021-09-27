import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, timer } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { AuthService } from '../../auth/services/auth.service';
import { isAuthenticated } from '../../auth/store/auth.selectors';
import { WebSocketService } from '../../shared/services/web-socket.service';
import { AtcState } from '../../shared/store/atc.state';
import { CarrierService } from '../services/carrier.service';
import { CarrierAction, GetActiveCarriers, GetActiveCarriersSuccess } from './carrier.actions';

@Injectable()
export class CarrierEffects {

  constructor(private readonly webSocketService: WebSocketService,
              private readonly carrierService: CarrierService,
              private readonly authService: AuthService,
              private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }

  // @Effect()
  public getActiveCarriersTimer(): Observable<GetActiveCarriers> {
    return timer(0, 10_000)
        .pipe(
            switchMap(n => this.store.select(isAuthenticated)),
            filter(auth => auth),
            map(n => new GetActiveCarriers())
        );
  }

  @Effect()
  public getActiveCarriersByWebSocket(): Observable<GetActiveCarriersSuccess> {
    return this.webSocketService.getCarriers()
        .pipe(
            map(carriers => new GetActiveCarriersSuccess(carriers))
        );
  }

  @Effect()
  public getActiveCarriers(): Observable<GetActiveCarriersSuccess> {
    return this.actions
        .pipe(
            ofType(CarrierAction.GetActiveCarriers),
            switchMap(() => this.carrierService.getActive()),
            map(carriers => new GetActiveCarriersSuccess(carriers))
        );
  }
}
