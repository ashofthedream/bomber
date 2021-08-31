import { FlightRecord } from './flight-record';

export interface FlightData {
  carrierId: string;
  records: FlightRecord[];
  actual: FlightRecord;
}
