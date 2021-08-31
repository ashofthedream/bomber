import { Action } from '@ngrx/store';
import { User } from '../models/user';

export enum UserAction {
  GetUser = '[User] Get User',
  GetUserSuccess = '[User] Get User Success',
}

export type UserActions = GetUser | GetUserSuccess;


export class GetUser implements Action {
  public readonly type = UserAction.GetUser;
}

export class GetUserSuccess implements Action {
  public readonly type = UserAction.GetUserSuccess;

  constructor(public readonly user: User) {
  }
}
