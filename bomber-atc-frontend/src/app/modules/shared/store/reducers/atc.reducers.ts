import { ActionReducerMap } from "@ngrx/store";
import { userReducers } from "./user.reducers";
import { carrierReducers } from "../../../carrier/store/reducers/carrier.reducers";
import { AtcState } from "../state/atc.state";

export const atcReducers: ActionReducerMap<AtcState, any> = {
  users: userReducers,
  carriers: carrierReducers,
}