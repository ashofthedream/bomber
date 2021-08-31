import { Action } from '@ngrx/store';
import { User } from '../../shared/models/user';
import { LoginRequest } from '../model/login.request';

export enum AuthAction {
  Login = '[Auth] Log In',
  LoginSuccess = '[Auth] Log In Success',
  Logout = '[Auth] Log Out',
  LogoutSuccess = '[Auth] Log Out Success',
}

export type AuthActions =
    Login | LoginSuccess |
    Logout | LogoutSuccess;


export class Login implements Action {
  public readonly type = AuthAction.Login;

  constructor(public readonly login: LoginRequest) {
  }
}

export class LoginSuccess implements Action {
  public readonly type = AuthAction.LoginSuccess;

  constructor(public readonly user: User) {
  }
}


export class Logout implements Action {
  public readonly type = AuthAction.Logout;
}

export class LogoutSuccess implements Action {
  public readonly type = AuthAction.LogoutSuccess;
}
