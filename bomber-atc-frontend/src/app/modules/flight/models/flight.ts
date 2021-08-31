import { FlightData } from './flight-data';

export interface Flight {
  id: number;
  data: Map<string, FlightData>;
}
