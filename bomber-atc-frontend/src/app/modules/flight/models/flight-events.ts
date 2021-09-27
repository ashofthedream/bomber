import { Carrier } from '../../carrier/models/carrier';
import { FlightProgress, HistogramPoint, SinkEvent } from './flight';
import { TestFlightPlan } from './test-flight-plan';

export type FlightEvents = ActiveCarriersEvent | ActiveFlightEvent | FlightLogEvent | FlightProgressEvent | FlightHistogramEvent;



export interface ActiveCarriersEvent {
  type: FlightEventType;
  carriers: Carrier[];
}

export interface FlightLogEvent {
  type: FlightEventType;
  event: SinkEvent;
}

export interface FlightProgressEvent {
  type: FlightEventType;
  progress: FlightProgress;
}

export interface FlightHistogramEvent {
  type: FlightEventType;
  points: HistogramPoint[];
}

export interface ActiveFlightEvent {
  type: FlightEventType;
  plan: TestFlightPlan;
}

export enum FlightEventType {
  ACTIVE_CARRIERS = 'ACTIVE_CARRIERS',
  ACTIVE_FLIGHT_STARTED = 'ACTIVE_FLIGHT_STARTED',
  ACTIVE_FLIGHT_FINISHED = 'ACTIVE_FLIGHT_FINISHED',
  ACTIVE_FLIGHT_PROGRESS = 'ACTIVE_FLIGHT_PROGRESS',
  ACTIVE_FLIGHT_LOG = 'ACTIVE_FLIGHT_LOG',
  ACTIVE_FLIGHT_HISTOGRAM = 'ACTIVE_FLIGHT_HISTOGRAM'
}