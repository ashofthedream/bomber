import { Carrier } from '../models/carrier';

export interface CarrierState {
  active: Carrier[];
  loadingActive: boolean;
}

export const initialCarrierState: CarrierState = {
  active: [],
  loadingActive: false
};
