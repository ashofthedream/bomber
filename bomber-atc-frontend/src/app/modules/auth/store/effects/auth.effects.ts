import { AuthService } from "../../services/auth.service";
import { Actions, Effect, ofType } from "@ngrx/effects";
import { Observable } from "rxjs";
import { map, switchMap, tap } from "rxjs/operators";
import { AuthAction, Login, LoginSuccess, LogoutSuccess } from "../actions/auth.actions";
import { Injectable } from "@angular/core";
import { GetUserSuccess } from "../../../shared/store/actions/user.actions";

@Injectable()
export class AuthEffects {

  constructor(private readonly authService: AuthService,
              private readonly actions: Actions) {
  }

  @Effect()
  public login(): Observable<LoginSuccess> {
    return this.actions
        .pipe(
            ofType<Login>(AuthAction.Login),
            switchMap(act => this.authService.login(act.login)),
            map(user => new LoginSuccess(user))
        );
  }

  @Effect()
  public loginSuccess(): Observable<GetUserSuccess> {
    return this.actions
        .pipe(
            ofType<LoginSuccess>(AuthAction.LoginSuccess),
            tap(act => this.authService.loginSuccessful(act.user)),
            map(act => new GetUserSuccess(act.user))
        );
  }


  @Effect()
  public logout(): Observable<LogoutSuccess> {
    return this.actions
        .pipe(
            ofType(AuthAction.Logout),
            switchMap(act => this.authService.logout()),
            map(success => new LogoutSuccess())
        );
  }

  @Effect()
  public logoutSuccess(): Observable<GetUserSuccess> {
    return this.actions
        .pipe(
            ofType(AuthAction.LogoutSuccess),
            tap(success => this.authService.logoutSuccessful()),
            map(act => new GetUserSuccess(null))
        );
  }
}