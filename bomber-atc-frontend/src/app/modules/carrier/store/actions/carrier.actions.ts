import { Action } from "@ngrx/store";
import { Carrier } from "../../models/carrier";

export enum CarrierAction {
  GetActiveCarriers = '[Carrier] Get Active Carriers',
  GetActiveCarriersSuccess = '[Carrier] Get Active Carriers Success'
}

export type CarrierActions = GetActiveCarriers | GetActiveCarriersSuccess;

export class GetActiveCarriers implements Action {
  public readonly type = CarrierAction.GetActiveCarriers;
}

export class GetActiveCarriersSuccess implements Action {
  public readonly type = CarrierAction.GetActiveCarriersSuccess;

  public constructor(public readonly carries: Carrier[]) {
  }
}