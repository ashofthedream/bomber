import {FlightRecord} from "./flight-record";

export class FlightData {
  carrierId: string;
  records: FlightRecord[];
  actual: FlightRecord;
}
