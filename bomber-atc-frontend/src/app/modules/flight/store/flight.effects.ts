import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, timer } from 'rxjs';
import { filter, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { isAuthenticated } from '../../auth/store/auth.selectors';
import { activeCarrierIds, activeCarriers } from '../../carrier/store/carrier.selectors';
import { AtcState } from '../../shared/store/atc.state';
import { FlightService } from '../services/flight.service';
import {
  StartCreatedFlight,
  FlightAction,
  GetActiveFlight,
  GetActiveFlightSuccess,
  StartFlight,
  StartFlightSuccess,
  StopFlight,
  StopFlightSuccess
} from './flight.actions';
import { createdFlightPlan, defaultFlightPlan } from './flight.selectors';

@Injectable()
export class FlightEffects {

  constructor(private readonly flightService: FlightService,
              private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }

  @Effect()
  public startFlightWithDefaultPlan(): Observable<StartFlight> {
    return this.actions
        .pipe(
            ofType(FlightAction.StartDefaultFlight),
            withLatestFrom(this.store.select(defaultFlightPlan)),
            map(([action, flight]) => new StartFlight(flight)),
        );
  }

  @Effect()
  public startFlightWithCreatedPlan(): Observable<StartFlight> {
    return this.actions
        .pipe(
            ofType(FlightAction.StartCreatedFlight),
            withLatestFrom(this.store.select(createdFlightPlan)),
            map(([action, flight]) => new StartFlight(flight)),
        );
  }

  @Effect()
  public startFlight(): Observable<StartFlightSuccess> {
    return this.actions
        .pipe(
            ofType<StartFlight>(FlightAction.StartFlight),
            withLatestFrom(this.store.select(activeCarrierIds)),
            switchMap(([action, carriers]) => this.flightService.startFlight(carriers, action.flight)),
            map(response => new StartFlightSuccess(response.flightId))
        );
  }

  @Effect()
  public stopFlight(): Observable<StopFlightSuccess> {
    return this.actions
        .pipe(
            ofType<StopFlight>(FlightAction.StopFlight),
            switchMap(action => this.flightService.stopAll()),
            map(response => new StopFlightSuccess())
        );
  }

  @Effect()
  public getActiveFlightTimer(): Observable<GetActiveFlight> {
    return timer(0, 5_000)
        .pipe(
            switchMap(n => this.store.select(isAuthenticated)),
            filter(auth => auth),
            map(n => new GetActiveFlight())
        );
  }

  @Effect()
  public getActiveFlight(): Observable<GetActiveFlightSuccess> {
    return this.actions
        .pipe(
            ofType(FlightAction.GetActiveFlight),
            switchMap(() => this.flightService.getActive()),
            map(flight => new GetActiveFlightSuccess(flight))
        );
  }
}
