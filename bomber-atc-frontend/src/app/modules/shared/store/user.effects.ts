import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { UserService } from '../services/user.service';
import { GetUser, GetUserSuccess, UserAction } from './user.actions';

@Injectable()
export class UserEffects {

  constructor(private readonly userService: UserService,
              private readonly actions: Actions) {
  }

  @Effect()
  public getUser(): Observable<GetUserSuccess> {
    return this.actions
        .pipe(
            ofType<GetUser>(UserAction.GetUser),
            switchMap(act => this.userService.getUser()),
            map(user => new GetUserSuccess(user))
        );
  }
}
