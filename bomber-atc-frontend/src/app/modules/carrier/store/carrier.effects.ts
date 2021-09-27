import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AuthService } from '../../auth/services/auth.service';
import { WebSocketService } from '../../shared/services/web-socket.service';
import { AtcState } from '../../shared/store/atc.state';
import { CarrierService } from '../services/carrier.service';
import { CarrierAction, GetActiveCarriersSuccess } from './carrier.actions';

@Injectable()
export class CarrierEffects {

  constructor(private readonly webSocketService: WebSocketService,
              private readonly carrierService: CarrierService,
              private readonly authService: AuthService,
              private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }

  @Effect()
  public getActiveCarriersByWebSocket(): Observable<GetActiveCarriersSuccess> {
    return this.webSocketService.carriers()
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
