import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, switchMap, withLatestFrom } from 'rxjs/operators';
import { activeCarrierIds } from '../../carrier/store/carrier.selectors';
import { WebSocketService } from '../../shared/services/web-socket.service';
import { AtcState } from '../../shared/store/atc.state';
import { FlightService } from '../services/flight.service';
import {
  ActiveFlightUpdated,
  FlightAction,
  FlightHistogramUpdated,
  FlightLogUpdated,
  FlightProgressUpdated,
  GetActiveFlightSuccess,
  StartFlight,
  StartFlightSuccess,
  StopFlight,
  StopFlightSuccess
} from './flight.actions';
import { createdFlightPlan, defaultFlightPlan } from './flight.selectors';

@Injectable()
export class FlightEffects {

  constructor(private readonly webSocketService: WebSocketService,
              private readonly flightService: FlightService,
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
  public getActiveFlightByWebSocket(): Observable<ActiveFlightUpdated> {
    return this.webSocketService.activeFlight()
        .pipe(
            map(plan => new ActiveFlightUpdated(plan))
        );
  }

  @Effect()
  public getActiveFlightProgressByWebSocket(): Observable<FlightProgressUpdated> {
    return this.webSocketService.activeFlightProgress()
        .pipe(
            map(progress => new FlightProgressUpdated(progress))
        );
  }

  @Effect()
  public getActiveFlightHistogramByWebSocket(): Observable<FlightHistogramUpdated> {
    return this.webSocketService.activeFlightHistogram()
        .pipe(
            map(points => new FlightHistogramUpdated(points))
        );
  }

  @Effect()
  public getActiveFlightLogByWebSocket(): Observable<FlightLogUpdated> {
    return this.webSocketService.activeFlightLog()
        .pipe(
            map(logs => new FlightLogUpdated(logs))
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
