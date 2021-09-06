import { Injectable } from '@angular/core';
import { Actions, Effect } from '@ngrx/effects';
import { EMPTY, Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

@Injectable()
export class TraceEffects {

  constructor(private readonly actions: Actions) {
  }

  @Effect()
  public trace(): Observable<any> {
    return this.actions
        .pipe(
            tap(action => console.log('action', action)),
            switchMap(() => EMPTY)
        );
  }
}