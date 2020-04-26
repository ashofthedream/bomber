import { AtcState } from "../../../shared/store/state/atc.state";
import { createSelector } from "@ngrx/store";
import { CarrierState } from "../state/carrier.state";

const selectCarriersState = (state: AtcState) => state.carriers;

export const activeCarriers = createSelector(selectCarriersState,
    (state: CarrierState) => state.active
);

