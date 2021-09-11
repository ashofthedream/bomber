import { Action } from '@ngrx/store';

export enum AppAction {
  Select = '[App] Select Test Case'
}

export type AppActions = Select;


export class Select implements Action {
  public readonly type = AppAction.Select;
}
