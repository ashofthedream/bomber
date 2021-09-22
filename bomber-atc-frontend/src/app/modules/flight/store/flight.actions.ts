import { Action } from '@ngrx/store';
import { AppTreeNode } from '../../app/store/app.state';
import { Flight } from '../models/flight';
import { TestFlight } from '../models/test-flight';


export enum FlightAction {
  StartDefaultFlight = '[Flight] Start All',
  CancelCreatedFlight = '[Flight] Cancel Created Flight',
  StartCreatedFlight = '[Flight] Start Created Flight',
  StartFlight = '[Flight] Start Flight',
  StartFlightSuccess = '[Flight] Start Flight Success',

  StopFlight = '[Flight] Stop Flight',
  StopFlightSuccess = '[Flight] Stop Flight Success',

  GetActiveFlight = '[Flight] Get Active Flight',
  GetActiveFlightSuccess = '[Flight] Get Active Flight Success',

  GetAllFlights = '[Flight] Get All Flights',
  GetAllFlightsSuccess = '[Flight] Get All Flights Success',

  AddToFlight = '[Flight] Add to Flight',
  RemoveFromFlight = '[Flight] Remove From Flight',
  ToggleCarrier = '[Flight] Toggle Carrier'
}

export type FlightActions =
    CancelCreatedFlight | StartDefaultFlight | StartCreatedFlight | StartFlight | StartFlightSuccess |
    StopFlight | StopFlightSuccess |
    GetActiveFlight | GetActiveFlightSuccess |
    GetAllFlights | GetAllFlightsSuccess |
    AddToFlight | RemoveFromFlight;


export class StartDefaultFlight implements Action {
  public readonly type = FlightAction.StartDefaultFlight;
}

export class StartCreatedFlight implements Action {
  public readonly type = FlightAction.StartCreatedFlight;
}

export class CancelCreatedFlight implements Action {
  public readonly type = FlightAction.CancelCreatedFlight;
}

export class StartFlight implements Action {
  public readonly type = FlightAction.StartFlight;

  public constructor(public readonly flight: TestFlight) {
  }
}

export class StartFlightSuccess implements Action {
  public readonly type = FlightAction.StartFlightSuccess;

  public constructor(public readonly flightId: number) {
  }
}


export class StopFlight implements Action {
  public readonly type = FlightAction.StopFlight;
}

export class StopFlightSuccess implements Action {
  public readonly type = FlightAction.StopFlightSuccess;
}


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


export class AddToFlight implements Action {
  public readonly type = FlightAction.AddToFlight;

  public constructor(public readonly testCase: AppTreeNode) {
  }
}

export class RemoveFromFlight implements Action {
  public readonly type = FlightAction.RemoveFromFlight;

  public constructor(public readonly testCase: AppTreeNode) {
  }
}


export class ToggleCarrier implements Action {
  public readonly type = FlightAction.ToggleCarrier;

  public constructor(public readonly id: string) {
  }
}
