import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, timer } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { isAuthenticated } from '../../auth/store/auth.selectors';
import { AtcState } from '../../shared/store/atc.state';
import { FlightService } from '../services/flight.service';
import { FlightAction, GetActiveFlight, GetActiveFlightSuccess } from './flight.actions';

@Injectable()
export class FlightEffects {

  constructor(private readonly flightService: FlightService,
              private readonly actions: Actions,
              private readonly store: Store<AtcState>) {
  }

  @Effect()
  public getActiveFlightTimer(): Observable<GetActiveFlight> {
    return timer(0, 3000)
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
            tap(action => console.log(action)),
            ofType(FlightAction.GetActiveFlight),
            switchMap(() => this.flightService.getActive()),
            map(flight => new GetActiveFlightSuccess(flight))
        );
  }
}
