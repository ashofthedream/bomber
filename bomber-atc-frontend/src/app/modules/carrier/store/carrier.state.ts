import { Carrier } from '../models/carrier';

export interface CarrierState {
  active: Carrier[];
  activeLoading: boolean;
}

export const initialCarrierState: CarrierState = {
  active: [],
  activeLoading: false
};
