import { Carrier } from '../models/carrier';

export interface CarrierState {
  active: Carrier[];
}

export const initialCarrierState: CarrierState = {
  active: []
};
