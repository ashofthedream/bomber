import { Action } from '@ngrx/store';
import { Flight } from '../models/flight';


export enum FlightAction {
  GetActiveFlight = '[Flight] Get Active Flight',
  GetActiveFlightSuccess = '[Flight] Get Active Flight Success',

  GetAllFlights = '[Flight] Get All Flights',
  GetAllFlightsSuccess = '[Flight] Get All Flights Success'
}

export type FlightActions =
    GetActiveFlight | GetActiveFlightSuccess |
    GetAllFlights | GetAllFlightsSuccess;


export class GetActiveFlight implements Action {
  public readonly type = FlightAction.GetActiveFlight;
}

export class GetActiveFlightSuccess implements Action {
  public readonly type = FlightAction.GetActiveFlightSuccess;

  public constructor(public readonly flight: Flight) {
  }
}


export class GetAllFlights implements Action {
  public readonly type = FlightAction.GetAllFlights;
}

export class GetAllFlightsSuccess implements Action {
  public readonly type = FlightAction.GetAllFlightsSuccess;

  public constructor(public readonly flights: Flight[]) {
  }
}
