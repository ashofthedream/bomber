import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map, shareReplay } from 'rxjs/operators';
import { webSocket } from 'rxjs/webSocket';
import { Carrier } from '../../carrier/models/carrier';
import { FlightProgress, HistogramPoint, SinkEvent } from '../../flight/models/flight';
import {
  ActiveCarriersEvent, ActiveFlightEvent,
  FlightEvents,
  FlightEventType,
  FlightHistogramEvent,
  FlightLogEvent,
  FlightProgressEvent
} from '../../flight/models/flight-events';
import { TestFlightPlan } from '../../flight/models/test-flight-plan';


@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private socket = webSocket('ws://localhost:4200/atc/socket');

  carriers(): Observable<Carrier[]> {
    return this.socket
        .pipe(
            filter((event: FlightEvents) => event.type === FlightEventType.ACTIVE_CARRIERS),
            map((event: ActiveCarriersEvent) => event.carriers),
            shareReplay(1)
        );
  }

  activeFlight(): Observable<TestFlightPlan> {
    return this.socket
        .pipe(
            filter((event: FlightEvents) => event.type === FlightEventType.ACTIVE_FLIGHT_STARTED || event.type === FlightEventType.ACTIVE_FLIGHT_FINISHED),
            map((event: ActiveFlightEvent) => event.plan),
            shareReplay(1)
        );
  }

  activeFlightProgress(): Observable<FlightProgress> {
    return this.socket
        .pipe(
            filter((event: FlightEvents) => event.type === FlightEventType.ACTIVE_FLIGHT_PROGRESS),
            map((event: FlightProgressEvent) => event.progress),
            shareReplay(1)
        );
  }


  activeFlightLog(): Observable<SinkEvent> {
    return this.socket
        .pipe(
            filter((event: FlightEvents) => event.type === FlightEventType.ACTIVE_FLIGHT_LOG),
            map((event: FlightLogEvent) => event.event),
            shareReplay(1)
        );
  }

  activeFlightHistogram(): Observable<HistogramPoint[]> {
    return this.socket
        .pipe(
            filter((event: FlightEvents) => event.type === FlightEventType.ACTIVE_FLIGHT_HISTOGRAM),
            map((event: FlightHistogramEvent) => event.points),
            shareReplay(1)
        );
  }
}
